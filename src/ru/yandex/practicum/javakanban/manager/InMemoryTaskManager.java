package ru.yandex.practicum.javakanban.manager;

import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Status;
import ru.yandex.practicum.javakanban.model.Subtask;
import ru.yandex.practicum.javakanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int counterId = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    } // получение списка всех задач

    @Override
    public void removeAllTasks() {
        tasks.keySet()
                .stream()
                .forEach(historyManager::remove);
        tasks.values()
                .stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasks::remove);
        tasks.clear();
    } // удаление всех задач

    @Override
    public Task getTaskById(int id) {
        final Optional<Task> optionalTask = Optional.ofNullable(tasks.get(id));
        final Task task = optionalTask.orElseThrow(() -> new ManagerSaveException("Задача отсутствует"));
        historyManager.addTaskToHistory(task);
        return task;
    } // получение задачи по идентификатору

    @Override
    public int addNewTask(Task task) {
        if (task == null) {
            throw new ManagerSaveException("Задача отсутствует");
        } else {
            if (task.getStartTime() != null) { //Проверка, что startTime указано, если нет, добавляем только в список tasks
                //Если задача накладывается на существующие, то не добавляем никуда
                if (prioritizedTasks
                        .stream()
                        .anyMatch(oldTask -> isOverlayTasks(oldTask, task))) {
                    throw new ManagerTimeException("Наложение задач по времени");
                }
                prioritizedTasks.add(task);
            }
            task.setId(++counterId);
            tasks.put(counterId, task);
            return counterId;
        }
    } // добавление новой задачи

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            if (task.getStartTime() != null) { //Проверка, что startTime указано, если нет, добавляем только в список tasks
                //Если задача накладывается на существующие, то не добавляем никуда
                if (prioritizedTasks
                        .stream()
                        .anyMatch(oldTask -> isOverlayTasks(oldTask, task))) {
                    throw new ManagerTimeException("Наложение задач по времени");
                }
                prioritizedTasks.add(task);
            }
            tasks.put(task.getId(), task);
        }
    } //обновление задачи

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            if (tasks.get(id).getStartTime() != null) {
                prioritizedTasks.remove(tasks.get(id));
            }
            tasks.remove(id);
            historyManager.remove(id);
        }
    } //удаление по идентификатору

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    } // получение списка всех эпиков

    @Override
    public void removeAllEpic() {
        epics.keySet()
                .stream()
                .forEach(historyManager::remove);
        subtasks.keySet()
                .stream()
                .forEach(historyManager::remove);
        // Удаляем все подзадачи из prioritizedTasks
        subtasks.values()
                .stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(prioritizedTasks::remove);
        epics.clear();
        subtasks.clear();
    } // удаление всех эпиков

    @Override
    public Epic getEpicById(int id) {
        final Optional<Epic> optionalEpic = Optional.ofNullable(epics.get(id));
        final Epic epic = optionalEpic.orElseThrow(() -> new ManagerSaveException("Эпик отсутствует"));
        historyManager.addTaskToHistory(epic);
        return epic;
    } // получение эпика по идентификатору

    @Override
    public int addNewEpic(Epic epic) {
        if (epic == null) {
            throw new ManagerSaveException("Эпик отсутствует");
        } else {
            epic.setId(++counterId);
            epics.put(counterId, epic);
            return counterId;
        }
    } //добавление нового эпика

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            final Epic savedEpic = epics.get(epic.getId());
            savedEpic.setTitle(epic.getTitle());
            savedEpic.setDescription(epic.getDescription());
        }
    } // обновление эпика

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            epics.keySet()
                    .stream()
                    .map(i -> epics.get(id).getSubtaskIdS())
                    .flatMap(list -> list.stream())
                    .peek(subtasks::remove)
                    .forEach(historyManager::remove);
            epics.remove(id);
            historyManager.remove(id);
        }
    } // удаление эпика по идентификатору

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        return epics.keySet()
                .stream()
                .filter(i -> epics.containsKey(epicId))
                .map(i -> epics.get(i).getSubtaskIdS())
                .flatMap(list -> list.stream())
                .map(subtasks::get)
                .toList();
    } //получение списка подзадач определенного эпика

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    } // получение списка всех подзадач

    @Override
    public void removeAllSubtask() {
        subtasks.keySet()
                .stream()
                .forEach(historyManager::remove);
        subtasks.values()
                .stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(prioritizedTasks::remove);
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        epics.values()
                .stream()
                .peek(Epic::clearSubtaskIdS)
                .map(Epic::getId)
                .peek(this::updateStatusOfEpic)
                .forEach(this::updateDurationAndStartTimeOfEpic);
    } // удаление всех подзадач

    @Override
    public Subtask getSubtaskById(int id) {
        final Optional<Subtask> optionalSubtask = Optional.ofNullable(subtasks.get(id));
        final Subtask subtask = optionalSubtask.orElseThrow(() -> new ManagerSaveException("Подзадача отсутствует"));
        historyManager.addTaskToHistory(subtask);
        return subtask;
    } // получение подзадачи по идентификатору

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new ManagerSaveException("Подзадача отсутствует");
        } else {
            if (epics.containsKey(subtask.getEpicId())) {
                //Проверка, что startTime указано, если нет, добавляем только в список subtasks
                if (subtask.getStartTime() != null) {
                    if (prioritizedTasks
                            .stream()
                            .anyMatch(oldSubtask -> isOverlayTasks(oldSubtask, subtask))) {
                        throw new ManagerTimeException("Наложение подзадач по времени");
                    }
                    prioritizedTasks.add(subtask);
                }
                subtask.setId(++counterId);
                epics.get(subtask.getEpicId()).addSubtaskId(counterId);
                subtasks.put(counterId, subtask);
                updateStatusOfEpic(subtask.getEpicId());
                //обновление длительности и времени начала/завершения эпика
                updateDurationAndStartTimeOfEpic(subtask.getEpicId());
                return counterId;
            }
            throw new ManagerSaveException("Подзадача отсутствует");
        }
    } //добавление подзадачи

    @Override
    public void updateSubTask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId()) &
                epics.get(subtask.getEpicId()).getSubtaskIdS().contains(subtask.getId())) {
            //Проверка, что startTime указано, если нет, добавляем только в список subtasks
            if (subtask.getStartTime() != null) {
                if (prioritizedTasks
                        .stream()
                        .anyMatch(oldTask -> isOverlayTasks(oldTask, subtask))) {
                    throw new ManagerTimeException("Наложение подзадач по времени");
                }
                prioritizedTasks.add(subtask);
            }
            subtasks.put(subtask.getId(), subtask);
            updateStatusOfEpic(subtask.getEpicId());
            //обновление длительности и времени начала/завершения эпика
            updateDurationAndStartTimeOfEpic(subtask.getEpicId());
        }
    } //обновление подзадачи

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            final Subtask savedSubtask = subtasks.get(id);
            final int tempEpicId = savedSubtask.getEpicId();

            if (savedSubtask.getStartTime() != null) {
                prioritizedTasks.remove(savedSubtask);
            }

            epics.get(tempEpicId).removeSubtaskId(id);
            subtasks.remove(id);
            historyManager.remove(id);
            updateStatusOfEpic(tempEpicId);
            //обновление длительности и времени начала/завершения эпика
            updateDurationAndStartTimeOfEpic(tempEpicId);
        }
    } // удаление подзадачи по идентификатору

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void updateStatusOfEpic(int id) {
        final Epic tempEpic = epics.get(id);
        if (tempEpic.getSubtaskIdS().isEmpty()) {
            tempEpic.setStatus(Status.NEW);
        } else {
            int counterNew = 0;
            int counterDone = 0;

            for (Integer i : tempEpic.getSubtaskIdS()) {
                if (subtasks.get(i).getStatus().equals(Status.NEW)) {
                    counterNew++;
                } else if (subtasks.get(i).getStatus().equals(Status.DONE)) {
                    counterDone++;
                }
            }
            if (counterNew == tempEpic.getSubtaskIdS().size()) {
                tempEpic.setStatus(Status.NEW);
            } else if (counterDone == tempEpic.getSubtaskIdS().size()) {
                tempEpic.setStatus(Status.DONE);
            } else {
                tempEpic.setStatus(Status.IN_PROGRESS);
            }
        }
    } // обновление статуса эпика

    // обновление полей duration и startTime эпика
    protected void updateDurationAndStartTimeOfEpic(int id) {
        final Epic savedEpic = epics.get(id);
        // создаем поток подзадач, хранящихся в эпике, убираем пустые с временем начала, сортируем
        final List<Subtask> arrayOfEpicsSubtask = subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == savedEpic.getId())
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Optional::ofNullable)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(Subtask::getStartTime))
                .toList();

        if (!arrayOfEpicsSubtask.isEmpty()) { //Проверка, что список из подзадач не пустой
            final LocalDateTime startTimeOfEpic = arrayOfEpicsSubtask.getFirst().getStartTime();
            final LocalDateTime endTimeOfEpic = arrayOfEpicsSubtask.getLast().getEndTime();

            savedEpic.setStartTime(startTimeOfEpic);
            savedEpic.setEndTime(endTimeOfEpic);
            savedEpic.setDuration(Duration.between(startTimeOfEpic, endTimeOfEpic).toMinutes());
        } else {
            savedEpic.setStartTime(null);
            savedEpic.setEndTime(null);
            savedEpic.setDuration(0);
        }
    }

    // метод по сравнению наложения по времени двух задач
    private boolean isOverlayTasks(Task task1, Task task2) {
        if (!prioritizedTasks.isEmpty()) {
            return task1.getStartTime().equals(task2.getStartTime())
                    || task1.getEndTime().equals(task2.getEndTime())
                    || task1.getStartTime().isAfter(task2.getStartTime())
                    && task1.getStartTime().isBefore(task2.getEndTime())
                    || task2.getStartTime().isAfter(task1.getStartTime())
                    && task2.getStartTime().isBefore(task1.getEndTime());
        } else {
            return false;
        }
    }
}

