package ru.yandex.practicum.javakanban.manager;

import ru.yandex.practicum.javakanban.model.Task;

import java.util.List;

public interface HistoryManager {

    void addTaskToHistory(Task task); // добавление задачи в историю

    List<Task> getHistory(); // предоставление списка просмотров

    void remove(int id); // удаление задачи из просмотра
  }
