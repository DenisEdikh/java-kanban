package ru.yandex.practicum.javakaban.manager;
import ru.yandex.practicum.javakaban.model.Epic;
import ru.yandex.practicum.javakaban.model.Status;
import ru.yandex.practicum.javakaban.model.Subtask;
import ru.yandex.practicum.javakaban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int counterId = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    } // получение списка всех задач

    public void removeAllTasks() {
        tasks.clear();
    } // удаление всех задач

    public Task getTaskById(int id) {
        return tasks.get(id);
    } // получение задачи по идентификатору

    public int addNewTask(Task task) {
        task.setId(++counterId);
        tasks.put(counterId, task);
        return counterId;
    } // добавление новой задачи

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    } //обновление задачи

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    } //удаление по идентификатору

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    } // получение списка всех эпиков

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    } // удаление всех эпиков

    public Epic getEpicById(int id) {
        return epics.get(id);
    } // получение эпика по идентификатору

    public int addNewEpic(Epic epic) {
        epic.setId(++counterId);
        epics.put(counterId, epic);
        return counterId;
    } //добавление нового эпика

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            final Epic savedEpic = epics.get(epic.getId());
            savedEpic.setTitle(epic.getTitle());
            savedEpic.setDescription(epic.getDescription());
        }
    } // обновление эпика

    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            for (Integer i : epics.get(id).getSubtaskIdS()) {
                subtasks.remove(i);
            }
            epics.remove(id);
        }
    } // удаление эпика по идентификатору

    public ArrayList<Subtask> listOfSubtask(Epic epic) {
        ArrayList<Subtask> listOfSubtask = new ArrayList<>();

        for (Integer i : epic.getListOfSubtask()) {
            listOfSubtask.add(subtasks.get(i));
        }
        return listOfSubtask;
    } //получение списка подзадач определенного эпика

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    } // получение списка всех подзадач

    public void removeAllSubtask() {
        subtasks.clear();
        for (Epic value : epics.values()) {
            refreshStatusOfEpic(value.getId());
        }
    } // удаление всех подзадач

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    } // получение подзадачи по идентификатору

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

    public void updateSubTask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            final Subtask savedSubtask = subtasks.get(subtask.getId());
            savedSubtask.setTitle(subtask.getTitle());
            savedSubtask.setDescription(subtask.getDescription());
            savedSubtask.setStatus(subtask.getStatus());
            updateStatusOfEpic(savedSubtask.getEpicId());
        }
    } //обновление подзадачи

    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int tempSubtaskIdS = subtasks.get(id).getEpicId();
            epics.get(tempSubtaskIdS).removeSubtaskId(id);
            subtasks.remove(id);
            updateStatusOfEpic(tempSubtaskIdS);
        }
    } // удаление подзадачи по идентификатору

    public void refreshStatusOfEpic(int id) {
        if (epics.get(id).getListOfSubtask().isEmpty()) {
            epics.get(id).setStatus(Status.NEW);
        } else {
            int counterNew =0;
            int counterDone =0;

            for (Integer i : epics.get(id).getListOfSubtask()) {
                if (subtasks.get(i).getStatus().equals(Status.NEW)) {
                    counterNew++;
                } else if (subtasks.get(i).getStatus().equals(Status.DONE)) {
                    counterDone++;
                }
            }
            if (counterNew == epics.get(id).getListOfSubtask().size()) {
                epics.get(id).setStatus(Status.NEW);
            } else if (counterDone == epics.get(id).getListOfSubtask().size()) {
                epics.get(id).setStatus(Status.DONE);
            } else {
                epics.get(id).setStatus(Status.IN_PROGRESS);
            }
        }
    } // обновление статуса эпика
}
