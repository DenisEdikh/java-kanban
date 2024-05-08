package ru.yandex.practicum.javakanban.manager;


import ru.yandex.practicum.javakanban.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    // Конструктор для создания менеджера с файлом сохранения по пути path
    public FileBackedTaskManager(String path) {
        this.path = Path.of(path);
    }

    // Конструктор для загрузки менеджера из файла
    private FileBackedTaskManager(File file) {
        this(file.getPath());
        try {
            // Проверяем, есть ли файл и в случае отсутствия или несоответствия пути выбрасываем своё исключение
            if (!Files.exists(path)) {
                throw new ManagerSaveException();
            }
            // Записываем в список всё содержимое файла построчно
            List<String> taskList = Files.readAllLines(path, StandardCharsets.UTF_8);

            // Пропускаем заголовок и заполняем мапы в зависимости от типа задачи
            for (int i = 1; i < taskList.size(); i++) {
                int id = toTaskFromString(taskList.get(i)).getId();

                if (toTaskFromString(taskList.get(i)).getClass().equals(Task.class)) {
                    tasks.put(id, toTaskFromString(taskList.get(i)));
                } else if (toTaskFromString(taskList.get(i)).getClass().equals(Epic.class)) {
                    epics.put(id, (Epic) toTaskFromString(taskList.get(i)));
                } else {
                    subtasks.put(id, (Subtask) toTaskFromString(taskList.get(i)));
                }
                // Счетчику присваиваем значение созданной последней задачи
                if (counterId < id) {
                    counterId = id;
                }
            }
            // Заполняем информацию для эпика по хранящимся подзадачам у него
            for (Subtask subtask : subtasks.values()) {
                epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void save() {
        try {
            // Создаем список, в котором будут храниться в виде стринги все имеющиеся задачи
            List<String> taskList = new ArrayList<>();
            // Добавляем заголовок
            String heading = "id,type,name,status,description,epic";
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
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        // создаем новый объект через конструктор
        return new FileBackedTaskManager(file);
    }

    private String toStringFromTask(Task task) {
        TypeOfTask type;
        String numberOfEpic = "";
        // Проверяем кдасс поступившей задачи и присваиваем нужное значение
        if (Task.class == task.getClass()) {
            type = TypeOfTask.TASK;
        } else if (Epic.class == task.getClass()) {
            type = TypeOfTask.EPIC;
        } else {
            type = TypeOfTask.SUBTASK;
            numberOfEpic = String.valueOf(((Subtask) task).getEpicId());
        }
        // Собираем стрингу
        return String.join(",",
                String.valueOf(task.getId()),
                String.valueOf(type),
                task.getTitle(),
                String.valueOf(task.getStatus()),
                task.getDescription(),
                numberOfEpic
        );
    }

    private Task toTaskFromString(String value) {
        // Разделяем стрингу
        String[] str = value.split(",");
        // Присваиваем определенным переменным значения из массива стринги str
        int id = Integer.parseInt(str[0]);
        TypeOfTask type = TypeOfTask.valueOf(str[1]);
        String title = str[2];
        Status status = Status.valueOf(str[3]);
        String description = str[4];

        // В зависимости от типа задачи инициализируем соответственно
        if (TypeOfTask.TASK.equals(type)) {
            return new Task(title, description, status, id);
        } else if (TypeOfTask.EPIC.equals(type)) {
            Epic epic = new Epic(title, description, id);
            epic.setStatus(status);
            return epic;
        } else {
            return new Subtask(title, description, status, id, Integer.parseInt(str[5]));
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
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    public static void main(String[] args) {

        TaskManager fileBackedTaskManager =
                new FileBackedTaskManager("src/ru/yandex/practicum/javakanban/resourses/memory-file.csv");

        int idT1 = fileBackedTaskManager.addNewTask(new Task("T1", "t1"));
        int idT2 = fileBackedTaskManager.addNewTask(new Task("T2", "t2"));
        int idE1 = fileBackedTaskManager.addNewEpic(new Epic("E1", "e1"));
        int idS1 = fileBackedTaskManager.addNewSubtask(new Subtask("S1", "s1", idE1));
        int idS2 = fileBackedTaskManager.addNewSubtask(new Subtask("S2", "s2", idE1));
        int idS3 = fileBackedTaskManager.addNewSubtask(new Subtask("S3", "s3", idE1));
        int idE2 = fileBackedTaskManager.addNewEpic(new Epic("E2", "e2"));

        List<Task> taskList = fileBackedTaskManager.getAllTasks();
        List<Epic> epicList = fileBackedTaskManager.getAllEpics();
        List<Subtask> subtaskList = fileBackedTaskManager.getAllSubtask();

        TaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(
                Path.of("src/ru/yandex/practicum/javakanban/resourses/memory-file.csv").toFile());

        List<Task> taskList1 = fileBackedTaskManager1.getAllTasks();
        List<Epic> epicList1 = fileBackedTaskManager1.getAllEpics();
        List<Subtask> subtaskList1 = fileBackedTaskManager1.getAllSubtask();

        System.out.println(taskList1.containsAll(taskList) &&
                epicList1.containsAll(epicList) &&
                subtaskList1.containsAll(subtaskList));

    }
}

