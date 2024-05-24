package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Subtask;
import ru.yandex.practicum.javakanban.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static java.nio.file.Files.createTempFile;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    Path path = Files.createTempFile("temp", null);
    FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(path);

    public FileBackedTaskManagerTest() throws IOException {
        super(new FileBackedTaskManager(createTempFile("temp", null)));
    }

    @Test
    void testException() {
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(Path.of("memory-file.csv").toFile());
        }, "Файл отсутствует!");
    }

    @Test
    void shouldCreateEmptyFileAndLoadFile() {
        assertTrue(Files.exists(path), "Файл не найден");
    }

    @Test
    void shouldAdd2TaskToFile() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final int taskId1 = fileBackedTaskManager.addNewTask(task1);

        try {
            final List<String> listTask = Files.readAllLines(path, StandardCharsets.UTF_8);
            final int size = listTask.size() - 1; // Не учитывается заголовок
            assertEquals(2, size, "Неверное кол-во задач");
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    @Test
    void shouldReturn2Tasks1Epic1SubtaskHistoryPrioritizedTasksFromFile() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                epicId,
                LocalDateTime.of(2024, Month.MAY, 20, 16, 32),
                7);
        final int subtaskId1 = fileBackedTaskManager.addNewSubtask(subtask);
        final List<Task> prioritizedList = fileBackedTaskManager.getPrioritizedTasks();

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(path.toFile());
        final Task loadedTask = fileBackedTaskManager1.getTaskById(taskId);
        final Epic loadedEpic = fileBackedTaskManager1.getEpicById(epicId);
        final Subtask loadedSubtask = fileBackedTaskManager1.getSubtaskById(subtaskId1);
        final List<Task> loadedPrioritizedList = fileBackedTaskManager1.getPrioritizedTasks();

        assertEquals(task.getTitle(), loadedTask.getTitle(), "Неверное название задачи");
        assertEquals(task.getDescription(), loadedTask.getDescription(), "Неверное описание задачи");
        assertEquals(task.getStatus(), loadedTask.getStatus(), "Неверный статус задачи");
        assertEquals(task.getStartTime(), loadedTask.getStartTime(), "Неверное время начала задачи");
        assertEquals(task.getDuration(), loadedTask.getDuration(), "Неверная длительность задачи");
        assertEquals(task.getEndTime(), loadedTask.getEndTime(), "Неверная длительность задачи");

        assertEquals(epic.getTitle(), loadedEpic.getTitle(), "Неверное название эпика");
        assertEquals(epic.getDescription(), loadedEpic.getDescription(), "Неверное описание эпика");
        assertEquals(epic.getStatus(), loadedEpic.getStatus(), "Неверный статус эпика");
        assertEquals(epic.getStartTime(), loadedEpic.getStartTime(), "Неверное время начала эпика");
        assertEquals(epic.getDuration(), loadedEpic.getDuration(), "Неверная длительность эпика");
        assertEquals(epic.getEndTime(), loadedEpic.getEndTime(), "Неверная длительность эпика");

        assertEquals(subtask.getTitle(), loadedSubtask.getTitle(), "Неверное название подзадачи");
        assertEquals(subtask.getDescription(), loadedSubtask.getDescription(), "Неверное описание подзадачи");
        assertEquals(subtask.getStatus(), loadedSubtask.getStatus(), "Неверный статус подзадачи");
        assertEquals(subtask.getStartTime(), loadedSubtask.getStartTime(), "Неверное время начала подзадачи");
        assertEquals(subtask.getDuration(), loadedSubtask.getDuration(), "Неверная длительность подзадачи");
        assertEquals(subtask.getEndTime(), loadedSubtask.getEndTime(), "Неверная длительность подзадачи");

        assertTrue(prioritizedList.containsAll(loadedPrioritizedList));
        assertTrue(fileBackedTaskManager.getAllTasks().containsAll(fileBackedTaskManager1.getAllTasks()));
        assertTrue(fileBackedTaskManager.getAllEpics().containsAll(fileBackedTaskManager1.getAllEpics()));
        assertTrue(fileBackedTaskManager.getAllSubtask().containsAll(fileBackedTaskManager1.getAllSubtask()));
    }
}


