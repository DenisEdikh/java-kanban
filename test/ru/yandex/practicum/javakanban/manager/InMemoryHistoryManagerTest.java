package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void shouldEmptyHistory() {
        Task task = new Task("Task",
                "Task description",
                null,
                10);
        task.setId(1);
        historyManager.addTaskToHistory(task);
        historyManager.remove(task.getId());
        int sizeHistoryManager = historyManager.getHistory().size();

        assertEquals(0, sizeHistoryManager, "История не пустая");
    }
    @Test
    void shouldReturn10TaskAndDeleteFirstTaskAndAdd11Task() {
        Task task = new Task("Task",
                "Task description",
                null,
                10);
        task.setId(1);
        Task task1 = new Task("Task1",
                "Task1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 30),
                10);
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

        // Проверка на отсутствие дублирования
        assertNotNull(savedHistory, "История пустая");
        assertEquals(2, sizeOfHistory, "История пустая");
        assertEquals(task, savedHistory.get(0), "Неверная запись по порядку");
        assertEquals(task1, savedHistory.get(1), "Неверная запись по порядку");

    }

    @Test
    void shouldReturnCorrectLinkWhenFormingLinkedList() {
        Task task = new Task("Task",
                "Task description",
                null,
                10);
        Task task1 = new Task("Task1",
                "Task1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 30),
                10);

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
        Task task = new Task("Task",
                "Task description",
                null,
                10);
        task.setId(1);
        Task task1 = new Task("Task1",
                "Task1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 30),
                10);
        task1.setId(2);
        Task task2 = new Task("Task2",
                "Task2 description",
                LocalDateTime.of(2023, Month.MAY, 22, 16, 30),
                10);
        task2.setId(3);

        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task);
        historyManager.remove(task1.getId());

        // Удаление из начала
        assertFalse(historyManager.getHistory().contains(task1), "Запись найдена");

        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task2);
        historyManager.remove(task1.getId());

        // Удаление из конца
        assertFalse(historyManager.getHistory().contains(task1), "Запись найдена");

    }
}
