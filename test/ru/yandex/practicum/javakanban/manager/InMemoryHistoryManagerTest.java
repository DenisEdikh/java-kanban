package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.javakanban.model.Task;

import java.util.List;

public class InMemoryHistoryManagerTest {
   HistoryManager historyManager = new InMemoryHistoryManager();
   @Test
    void shouldReturn10TaskAndDeleteFirstTaskAndAdd11Task() {
       Task task = new Task("test addTask", "test addTask description");
       Task task1 = new Task("test addNewTask", "test addNewTask description");

       historyManager.addTaskToHistory(task1);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task);
       historyManager.addTaskToHistory(task1);

       final List<Task> savedHistory = historyManager.getHistory();
       final int sizeOfHistory = savedHistory.size();

       assertNotNull(savedHistory, "История не пустая");
       assertEquals(10, sizeOfHistory, "История не пустая");
       assertEquals(task1, savedHistory.get(9), "Неправильное хранение истории");

   }
}
