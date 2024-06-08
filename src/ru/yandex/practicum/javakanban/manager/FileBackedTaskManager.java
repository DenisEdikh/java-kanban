package ru.yandex.practicum.javakanban.manager;


import ru.yandex.practicum.javakanban.exception.ManagerSaveException;
import ru.yandex.practicum.javakanban.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    // Конструктор для создания менеджера с файлом сохранения по пути path
    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    // Конструктор для загрузки менеджера из файла
    private FileBackedTaskManager(File file) {
        this(file.toPath());
        try {
            // Проверяем, есть ли файл и в случае отсутствия или несоответствия пути выбрасываем своё исключение
            if (!Files.exists(path)) {
                throw new ManagerSaveException("Файл отсутствует!");
            }
            // Записываем в список всё содержимое файла построчно
            List<String> taskList = Files.readAllLines(path, StandardCharsets.UTF_8);

            // Пропускаем заголовок и заполняем мапы в зависимости от типа задачи
            for (int i = 1; i < taskList.size(); i++) {
                int id = toTaskFromString(taskList.get(i)).getId();

                TypeOfTask taskOfType = toTaskFromString(taskList.get(i)).getType();
                if (taskOfType.equals(TypeOfTask.TASK)) {
                    final Task task = toTaskFromString(taskList.get(i));
                    tasks.put(id, task);
                    if (task.getStartTime() != null) {
                        prioritizedTasks.add(task);
                    }
                } else if (taskOfType.equals(TypeOfTask.EPIC)) {
                    epics.put(id, (Epic) toTaskFromString(taskList.get(i)));
                } else {
                    final Subtask subtask = (Subtask) toTaskFromString(taskList.get(i));
                    subtasks.put(id, subtask);
                    if (subtask.getStartTime() != null) {
                        prioritizedTasks.add(subtask);
                    }
                }
                // Счетчику присваиваем значение созданной последней задачи
                if (counterId < id) {
                    counterId = id;
                }
            }
            // Заполняем информацию для эпика по хранящимся подзадачам у него
            for (Subtask subtask : subtasks.values()) {
                epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
                updateDurationAndStartTimeOfEpic(subtask.getEpicId());
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private void save() {
        try {
            // Создаем список, в котором будут храниться в виде стринги все имеющиеся задачи
            List<String> taskList = new ArrayList<>();
            // Добавляем заголовок
            String heading = "id,type,name,status,description,epic,startTime,duration";
            taskList.add(heading);

            // Записываем все задачи, эпики и подзадачи
            for (Task task : tasks.values()) {
                taskList.add(toStringFromTask(task));
            }
            for (Epic epic : epics.values()) {
                taskList.add(toStringFromTask(epic));
            }
            for (Subtask subtask : subtasks.values()) {
                taskList.add(toStringFromTask(subtask));
            }

            // Записываем в файл в кодировке UTF-8
            Files.write(path, taskList, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        // создаем новый объект через конструктор
        return new FileBackedTaskManager(file);
    }

    private String toStringFromTask(Task task) {
        String title = " ";
        String description = " ";
        String numberOfEpic = " ";
        String startTime = " ";
        String duration = " ";

        if (task.getTitle() != null) {
            title = task.getTitle();
        }
        if (task.getDescription() != null) {
            description = task.getDescription();
        }
        // В случае subtask присваиваем новое значение переменной numberOfEpic
        if (task.getType().equals(TypeOfTask.SUBTASK)) {
            numberOfEpic = String.valueOf(((Subtask) task).getEpicId());
        }
        if (task.getStartTime() != null) {
            startTime = String.valueOf(task.getStartTime());
        }
        if (task.getDuration() != null) {
            duration = String.valueOf(task.getDuration());
        }

        // Собираем стрингу
        return String.join(",",
                String.valueOf(task.getId()),
                String.valueOf(task.getType()),
                title,
                String.valueOf(task.getStatus()),
                description,
                numberOfEpic,
                startTime,
                duration
        );
    }

    private Task toTaskFromString(String value) {
        // Разделяем стрингу
        final String[] str = value.split(",");
        // Присваиваем определенным переменным значения из массива стринги str
        final int id = Integer.parseInt(str[0]);
        final TypeOfTask type = TypeOfTask.valueOf(str[1]);
        final String title;
        final Status status = Status.valueOf(str[3]);
        final String description;
        final LocalDateTime startTime;
        final Duration duration;

        if ((" ").equals(str[2])) {
            title = null;
        } else {
            title = str[2];
        }
        if ((" ").equals(str[4])) {
            description = null;
        } else {
            description = str[4];
        }
        if ((" ").equals(str[6])) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(str[6]);
        }
        if ((" ").equals(str[7])) {
            duration = null;
        } else {
            duration = Duration.parse(str[7]);
        }

        // В зависимости от типа задачи инициализируем соответственно
        if (TypeOfTask.TASK.equals(type)) {
            return new Task(title, description, status, startTime, duration, id);
        } else if (TypeOfTask.EPIC.equals(type)) {
            Epic epic = new Epic(title, description, id);
            epic.setStatus(status);
            epic.setDuration(duration);
            epic.setStartTime(startTime);
            return epic;
        } else {
            return new Subtask(title, description, status, startTime, duration, Integer.parseInt(str[5]), id);
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }
}


