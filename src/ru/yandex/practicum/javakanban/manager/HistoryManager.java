package ru.yandex.practicum.javakanban.manager;

import ru.yandex.practicum.javakanban.model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void addTaskToHistory(Task task);
    ArrayList<Task> getHistory();
}
