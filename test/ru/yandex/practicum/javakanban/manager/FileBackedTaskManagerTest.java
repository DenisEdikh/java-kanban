package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    void shouldReturn2TaskFromFile() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final int taskId1 = fileBackedTaskManager.addNewTask(task1);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(path.toFile());
        final List<Task> taskList = fileBackedTaskManager1.getAllTasks();
        final int size = taskList.size();

        assertEquals(2, size, "Неверное кол-во задач");
    }
}


