package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void shouldReturn10TaskAndDeleteFirstTaskAndAdd11Task() {
        Task task = new Task("test addTask", "test addTask description");
        task.setId(1);
        Task task1 = new Task("test addNewTask", "test addNewTask description");
        task1.setId(2);

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

        assertNotNull(savedHistory, "История пустая");
        assertEquals(2, sizeOfHistory, "История пустая");
        assertEquals(task, savedHistory.get(0), "Неверная запись по порядку");
        assertEquals(task1, savedHistory.get(1), "Неверная запись по порядку");

    }

    @Test
    void shouldReturnCorrectLinkWhenFormingLinkedList() {
        Task task = new Task("test addTask", "test addTask description");
        Task task1 = new Task("test addNewTask", "test addNewTask description");

        historyManager.linkLast(task);
        assertNotNull(historyManager.getHead(), "Некорректная ссылка");
        assertNotNull(historyManager.getTail(), "Некорректная ссылка");

        historyManager.linkLast(task1);
        historyManager.linkLast(task1);
        assertNull(historyManager.getHead().getPrev(), "Некорректная ссылка");
        assertNull(historyManager.getTail().getNext(), "Некорректная ссылка");
        assertNotNull(historyManager.getHead().getNext(), "Некорректная ссылка");
        assertEquals(historyManager.getHead().getNext(), historyManager.getTail().getPrev(),
                "Несоответствие ссылок");
    }

    @Test
    void shouldTaskBeMissingAfterDeletion() {
        Task task = new Task("test addTask", "test addTask description");
        Task task1 = new Task("test addNewTask", "test addNewTask description");

        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task);
        historyManager.remove(0);

        assertFalse(historyManager.getHistory().contains(task1), "Запись найдена");
    }
}
