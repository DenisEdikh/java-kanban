package ru.yandex.practicum.javakanban.manager;

import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Status;
import ru.yandex.practicum.javakanban.model.Subtask;
import ru.yandex.practicum.javakanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int counterId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    } // получение списка всех задач

    @Override
    public void removeAllTasks() {
        for (Integer i : tasks.keySet()) {
           historyManager.remove(i);
        }
        tasks.clear();
    } // удаление всех задач

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        historyManager.addTaskToHistory(task);
        return task;
    } // получение задачи по идентификатору

    @Override
    public int addNewTask(Task task) {
        task.setId(++counterId);
        tasks.put(counterId, task);
        return counterId;
    } // добавление новой задачи

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    } //обновление задачи

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    } //удаление по идентификатору

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    } // получение списка всех эпиков

    @Override
    public void removeAllEpic() {
        for (Integer i : epics.keySet()) {
            historyManager.remove(i);
        }
        for (Integer i : subtasks.keySet()) {
            historyManager.remove(i);
        }
        epics.clear();
        subtasks.clear();
    } // удаление всех эпиков

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);
        historyManager.addTaskToHistory(epic);
        return epic;
    } // получение эпика по идентификатору

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(++counterId);
        epics.put(counterId, epic);
        return counterId;
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
            for (Integer i : epics.get(id).getSubtaskIdS()) {
                subtasks.remove(i);
                historyManager.remove(i);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    } // удаление эпика по идентификатору

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        final ArrayList<Subtask> epicSubtask = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (Integer i : epics.get(epicId).getSubtaskIdS()) {
                epicSubtask.add(subtasks.get(i));
            }
        }
        return epicSubtask;
    } //получение списка подзадач определенного эпика

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    } // получение списка всех подзадач

    @Override
    public void removeAllSubtask() {
        for (Integer i : subtasks.keySet()) {
            historyManager.remove(i);
        }
        subtasks.clear();
        for (Epic value : epics.values()) {
            value.clearSubtaskIdS();
            updateStatusOfEpic(value.getId());
        }
    } // удаление всех подзадач

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.addTaskToHistory(subtask);
        return subtask;
    } // получение подзадачи по идентификатору

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(++counterId);
            epics.get(subtask.getEpicId()).addSubtaskId(counterId);
            subtasks.put(counterId, subtask);
            updateStatusOfEpic(subtask.getEpicId());
            return counterId;
        }
        return -1;
    } //добавление подзадачи

    @Override
    public void updateSubTask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId()) &
                epics.get(subtask.getEpicId()).getSubtaskIdS().contains(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateStatusOfEpic(subtask.getEpicId());
        }
    } //обновление подзадачи

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            final int tempEpicId = subtasks.get(id).getEpicId();
            epics.get(tempEpicId).removeSubtaskId(id);
            subtasks.remove(id);
            historyManager.remove(id);
            updateStatusOfEpic(tempEpicId);
        }
    } // удаление подзадачи по идентификатору

    private void updateStatusOfEpic(int id) {
        final Epic tempEpic = epics.get(id);
        if (tempEpic.getSubtaskIdS().isEmpty()) {
            tempEpic.setStatus(Status.NEW);
        } else {
            int counterNew = 0;
            int counterDone = 0;

            for (Integer i : tempEpic.getSubtaskIdS()) {
                if (subtasks.get(i).getStatus().equals(Status.NEW)) { //потом поменять местами переменные
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}

