package ru.yandex.practicum.javakanban.manager;

import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Subtask;
import ru.yandex.practicum.javakanban.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTasks(); // получение списка всех задач


    void removeAllTasks(); // удаление всех задач


    Task getTaskById(int id); // получение задачи по идентификатору


    int addNewTask(Task task); // добавление новой задачи


    void updateTask(Task task); //обновление задачи


    void removeTaskById(int id); //удаление по идентификатору


    ArrayList<Epic> getAllEpics(); // получение списка всех эпиков


    void removeAllEpic(); // удаление всех эпиков


    Epic getEpicById(int id); // получение эпика по идентификатору


    int addNewEpic(Epic epic); //добавление нового эпика


    void updateEpic(Epic epic); // обновление эпика


    void removeEpicById(int id); // удаление эпика по идентификатору


    ArrayList<Subtask> getEpicSubtasks(int epicId); //получение списка подзадач определенного эпика


    ArrayList<Subtask> getAllSubtask(); // получение списка всех подзадач


    void removeAllSubtask(); // удаление всех подзадач


    Subtask getSubtaskById(int id); // получение подзадачи по идентификатору


    int addNewSubtask(Subtask subtask); //добавление подзадачи


    void updateSubTask(Subtask subtask); //обновление подзадачи


    void removeSubtaskById(int id); // удаление подзадачи по идентификатору

    ArrayList<Task> getHistory();

}
