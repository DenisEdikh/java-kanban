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

    public void addNewTask(Task task) {
        task.setId(++taskId);
        tasks.put(taskId, task);
    } // добавление новой задачи

    public void refreshOfTask(Task task) {
        tasks.put(task.getId(), task);
    } //обновление задачи

    public void removeTaskById(int id) {
        tasks.remove(id);
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

    public void addNewEpic(Epic epic) {
        epic.setId(++taskId);
        epics.put(taskId, epic);
    } //добавление нового эпика

    public void refreshOfEpic(Epic epic) {
        String tempTitle = epics.get(epic.getId()).getTitle(); // временное сохр. титула сущ. эпика
        Status tempStatus = epics.get(epic.getId()).getStatus(); // временное сохр. статуса сущ. эпика
        ArrayList<Integer> tempList = epics.get(epic.getId()).getListOfSubtask(); // временное сохр. подзадач сущ. эпика

        epics.put(epic.getId(), epic); // обновление эпика
        epics.get(epic.getId()).setStatus(tempStatus); // т.к. при обновлении эпика статус остается тем же
        epics.get(epic.getId()).setListOfSubtask(tempList); // даем информацию о связанных подзадачах
        // проверка на изменение имени эпика (тогда необходимо поменять у всех подзадач)
        if (!epic.getTitle().equals(tempTitle)) {
            for (Integer tempId : epics.get(epic.getId()).getListOfSubtask()) {
                subtasks.get(tempId).setEpic(epic.getTitle());
            }
        }
    } // обновление эпика

    public void removeEpicById(int id) {
        for (Integer i : epics.get(id).getListOfSubtask()) {
            subtasks.remove(i);
        }
        epics.remove(id);
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

    public void addNewSubtask(Subtask subtask) {
        subtask.setId(++taskId); //рассмотреть возможность без цикла, сразу выбрать значение эпика/ вроде не получается
        for (Epic value : epics.values()) {
            if (value.getTitle().equals(subtask.getEpic())) {
                value.getListOfSubtask().add(taskId);
                subtask.setEpicId(value.getId()); // установка в подзадаче id эпика, в который она добавлена
            }
        }
        subtasks.put(taskId, subtask);
    } //добавление подзадачи

    public void refreshOfSubTask(Subtask subtask) {
        int tempEpicId = subtasks.get(subtask.getId()).getEpicId();

        subtask.setEpicId(tempEpicId); // добавл. в обновленную подзадачу id эпика, кот-ый был в обновляемой подзадаче
        subtasks.put(subtask.getId(), subtask);
        refreshStatusOfEpic(tempEpicId); // обновление статуса эпика
    } //обновление подзадачи
    public void removeSubtaskById(int id) {
        //получение индекса передаваемого id, хранящегося в эпике
        int tempIndex = epics.get(subtasks.get(id).getEpicId()).getListOfSubtask().indexOf(id);
        int tempEpicId = subtasks.get(id).getEpicId();

        //удаление id подзадачи, хранящегося в эпике
        epics.get(subtasks.get(id).getEpicId()).getListOfSubtask().remove(tempIndex);
        subtasks.remove(id);
        refreshStatusOfEpic(tempEpicId);
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
