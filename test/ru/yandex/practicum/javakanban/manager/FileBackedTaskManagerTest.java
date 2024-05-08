package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Status;
import ru.yandex.practicum.javakanban.model.Subtask;
import ru.yandex.practicum.javakanban.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private String path;
    private TaskManager fileBackedTaskManager;
    private Task task;
    private Task task1;
    private Epic epic;
    private Epic epic1;

    @BeforeEach
    void beforeEach() {
        try {
            // Для тестов создается файл для сохранения во временной папке
            path = Files.createTempFile("temp", null).toFile().getPath();
            fileBackedTaskManager =
                    new FileBackedTaskManager(path);
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        task = new Task("Task", "Task description");
        task1 = new Task("Task1", "Task1 description");
        epic = new Epic("epic", "epic description");
        epic1 = new Epic("Epic1", "Epic1 description");
    }


    @Test
    void shouldReturn1AddedTask() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final Task savedTask = fileBackedTaskManager.getTaskById(taskId);


        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final List<Task> tasks = fileBackedTaskManager.getAllTasks();
        int sizeOfTasks = tasks.size();

        assertNotNull(tasks, "Задачи не найдены");
        assertEquals(1, sizeOfTasks, "Неверное кол-во задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldReturn1UpdatedTask() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        Task newTask = new Task("newTask", "newTask description", taskId);

        fileBackedTaskManager.updateTask(newTask);

        final List<Task> tasks = fileBackedTaskManager.getAllTasks();
        final Task savedTask = fileBackedTaskManager.getTaskById(taskId);
        int sizeOfTasks = tasks.size();

        assertNotNull(savedTask, "Новая задача не найдена");
        assertEquals(1, sizeOfTasks, "Неверное кол-во задач");
        assertEquals(newTask, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldReturnTasksAndHistoryWithOutDeletedTask() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final int taskId1 = fileBackedTaskManager.addNewTask(task1);

        fileBackedTaskManager.getTaskById(taskId);
        fileBackedTaskManager.getTaskById(taskId1);
        fileBackedTaskManager.removeTaskById(taskId);

        final int sizeOfTasks = fileBackedTaskManager.getAllTasks().size();
        final Task removedTask = fileBackedTaskManager.getTaskById(taskId);
        final boolean savedTask = fileBackedTaskManager.getHistory().contains(task);

        assertEquals(1, sizeOfTasks, "Неверное кол-во задач");
        assertFalse(savedTask, "Задача найдена");
        assertNull(removedTask, "Задача найдена");
    }

    @Test
    void shouldReturnEmptyListOfTasksAndHistory() {
        final int taskId1 = fileBackedTaskManager.addNewTask(task);
        final int taskId2 = fileBackedTaskManager.addNewTask(task1);

        fileBackedTaskManager.getTaskById(taskId1);
        fileBackedTaskManager.getTaskById(taskId1);
        fileBackedTaskManager.getTaskById(taskId2);
        fileBackedTaskManager.removeAllTasks();

        final int savedSizeOfHistory = fileBackedTaskManager.getHistory().size();
        final int savedSizeOfTasks = fileBackedTaskManager.getAllTasks().size();

        assertEquals(0, savedSizeOfTasks, "Задачи найдены");
        assertEquals(0, savedSizeOfHistory, "Задачи найдены в истории");
    }

    @Test
    void shouldReturn1AddedSubtask() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("subtask of epic", "subtask description", epicId);

        final int subtaskId = fileBackedTaskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = fileBackedTaskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
        assertTrue(epic.getSubtaskIdS().contains(subtaskId), "Подзадача не найдена в эпике");


        final List<Subtask> subtasks = fileBackedTaskManager.getAllSubtask();
        final int sizeOfSubtasks = subtasks.size();

        assertNotNull(subtasks, "Подзадачи не найдены");
        assertEquals(1, sizeOfSubtasks, "Неверное кол-во подзадач");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldReturn1UpdatedSubtask() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("subtask of epic", "subtask of epic description", epicId);
        final int subtaskId = fileBackedTaskManager.addNewSubtask(subtask);
        Subtask newSubtask = new Subtask("subtask of epic",
                "subtask of epic newDescription", Status.IN_PROGRESS, subtaskId, epicId);

        fileBackedTaskManager.updateSubTask(newSubtask);
        final Subtask savedNewSubtask = fileBackedTaskManager.getSubtaskById(subtaskId);

        assertNotNull(savedNewSubtask, "Подзадача не найдена");
        assertEquals(newSubtask, savedNewSubtask, "Подзадачи не совпадают");

        final List<Subtask> subtasks = fileBackedTaskManager.getAllSubtask();
        final int sizeOfSubtasks = subtasks.size();

        assertNotNull(subtasks, "Подзадачи не найдены");
        assertEquals(1, sizeOfSubtasks, "Неверное кол-во подзадач");
        assertEquals(newSubtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void shouldReturnSubtasksAndHistoryWithOutDeletedSubtask() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("subtask of epic", "subtask of epic description", epicId);
        Subtask subtask1 = new Subtask("subtask of epic", "subtask of epic description", epicId);
        final int subtaskId = fileBackedTaskManager.addNewSubtask(subtask);
        final int subtaskId1 = fileBackedTaskManager.addNewSubtask(subtask1);

        fileBackedTaskManager.getSubtaskById(subtaskId);
        fileBackedTaskManager.getSubtaskById(subtaskId1);

        fileBackedTaskManager.removeSubtaskById(subtaskId);

        final Subtask removedSubtask = fileBackedTaskManager.getSubtaskById(subtaskId);
        final int sizeOfSubtasks = fileBackedTaskManager.getAllSubtask().size();
        final boolean savedSubtask = fileBackedTaskManager.getHistory().contains(subtask);
        final boolean savedList = fileBackedTaskManager.getEpicById(epicId).getSubtaskIdS().contains(subtaskId);

        assertEquals(1, sizeOfSubtasks, "Неверное кол-во задач");
        assertFalse(savedSubtask, "Задача найдена");
        assertNull(removedSubtask, "Задача найдена");
        assertFalse(savedList, "Задача найдена");
    }

    @Test
    void shouldReturnEmptyListOfSubtasksAndHistory() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", epicId);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epicId);

        final int subtaskId = fileBackedTaskManager.addNewSubtask(subtask);
        final int subtaskId1 = fileBackedTaskManager.addNewSubtask(subtask1);

        fileBackedTaskManager.getSubtaskById(subtaskId);
        fileBackedTaskManager.getSubtaskById(subtaskId1);
        fileBackedTaskManager.getSubtaskById(subtaskId1);
        fileBackedTaskManager.removeAllSubtask();

        final int savedSizeOfSubtasks = fileBackedTaskManager.getAllSubtask().size();
        final int savedSizeOfHistory = fileBackedTaskManager.getHistory().size();

        assertEquals(0, savedSizeOfSubtasks, "Подзадачи найдены");
        assertEquals(0, savedSizeOfHistory, "Подзадачи найдены в истории");
    }

    @Test
    void shouldReturn1AddedEpic() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        final Epic savedEpic = fileBackedTaskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Подзадача не найдена");
        assertEquals(epic, savedEpic, "Подзадачи не совпадают");

        final List<Epic> savedEpics = fileBackedTaskManager.getAllEpics();
        final int sizeOfEpics = savedEpics.size();

        assertNotNull(savedEpics, "Эпики не найдены");
        assertEquals(1, sizeOfEpics, "Неверное кол-во эпиков");
        assertEquals(savedEpic, savedEpics.get(0));
    }

    @Test
    void shouldReturn1UpdatedEpic() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);

        Epic newEpic = new Epic("newEpic", "newEpic descriptionNew", epicId);

        fileBackedTaskManager.updateEpic(newEpic);

        final List<Epic> epics = fileBackedTaskManager.getAllEpics();
        final Epic savedEpic = fileBackedTaskManager.getEpicById(epicId);
        final int sizeOfEpics = epics.size();

        assertNotNull(savedEpic, "Новый эпик не найден");
        assertEquals(1, sizeOfEpics, "Неверное кол-во эпиков");
        assertEquals(newEpic, epics.get(0), "Эпики не совпадают");
    }

    @Test
    void shouldReturnEpicsAndHistoryWithOutDeletedEpics() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        final int epicId1 = fileBackedTaskManager.addNewEpic(epic1);

        Subtask subtask = new Subtask("Subtask", "Subtask description", epicId);

        final int subtaskId = fileBackedTaskManager.addNewSubtask(subtask);

        fileBackedTaskManager.getEpicById(epicId);
        fileBackedTaskManager.getEpicById(epicId);
        fileBackedTaskManager.getEpicById(epicId1);
        fileBackedTaskManager.getSubtaskById(subtaskId);
        fileBackedTaskManager.removeEpicById(epicId);

        final int sizeOfEpics = fileBackedTaskManager.getAllEpics().size();
        final Epic removedEpic = fileBackedTaskManager.getEpicById(epicId);
        final boolean savedEpic = fileBackedTaskManager.getHistory().contains(epic);
        final boolean savedSubtask = fileBackedTaskManager.getHistory().contains(subtask);

        assertEquals(1, sizeOfEpics, "Неверное кол-во эпиков'");
        assertFalse(savedEpic, "Эпик найден в истории");
        assertFalse(savedSubtask, "Подзадача эпика найдена в истории");
        assertNull(removedEpic, "Эпик найден");
    }

    @Test
    void shouldReturnEmptyListOfEpicsAndHistory() {
        fileBackedTaskManager.addNewEpic(epic);
        fileBackedTaskManager.addNewEpic(epic1);
        fileBackedTaskManager.removeAllEpic();

        final int savedSizeOfEpics = fileBackedTaskManager.getAllEpics().size();
        final int savedSizeOfHistory = fileBackedTaskManager.getHistory().size();

        assertEquals(0, savedSizeOfEpics, "Эпики найдены");
        assertEquals(0, savedSizeOfHistory, "Эпики найдены в истории");
    }

    @Test
    void shouldReturnUpdatedStatusEpics() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        final Epic savedEpic = fileBackedTaskManager.getEpicById(epicId);
        Subtask subtask = new Subtask("subtask", "subtask description", epicId);
        Subtask subtask1 = new Subtask("subtask1", "subtask1 description", epicId);

        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус некорректне");

        final int subtaskId = fileBackedTaskManager.addNewSubtask(subtask);
        final int subtask1Id = fileBackedTaskManager.addNewSubtask(subtask1);

        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус некорректен");

        Subtask newSubtask1 = new Subtask("newSubtask1", "newSubtask1 description",
                Status.DONE, subtask1Id, epicId);

        fileBackedTaskManager.updateSubTask(newSubtask1);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус некорректен");
    }

    @Test
    void shouldReturnEqualityOfTasksIfIdsEqual() {
        fileBackedTaskManager.addNewTask(task);
        task1.setId(1);

        assertEquals(task, task1, "Задачи не равны");

    }

    @Test
    void shouldReturnSameFieldsAfterAddingToTaskManager() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final Task savedTask = fileBackedTaskManager.getTaskById(taskId);

        assertEquals(task.getTitle(), savedTask.getTitle(), "Задача изменилась");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Задача изменилась");
    }

    @Test
    void shouldReturn2EntriesOfHistory() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final int taskId1 = fileBackedTaskManager.addNewTask(task1);
        fileBackedTaskManager.getTaskById(taskId);
        fileBackedTaskManager.getTaskById(taskId);
        fileBackedTaskManager.getTaskById(taskId1);
        fileBackedTaskManager.getTaskById(taskId);

        final List<Task> history = fileBackedTaskManager.getHistory();
        final int sizeOfHistory = history.size();

        assertEquals(2, sizeOfHistory, "История некорректна");
    }

    @Test
    void shouldCreateEmptyFileAndLoadFile() {
        assertTrue(Files.exists(Path.of(path)), "Файл не найден");
    }

    @Test
    void shouldAdd2TaskToFile() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final int taskId1 = fileBackedTaskManager.addNewTask(task1);

        try {
            final List<String> listTask = Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
            final int size = listTask.size() - 1; // Не учитывается заголовок
            assertEquals(2, size, "Неверное кол-во задач");
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    @Test
    void shouldReturn2TaskFromFile() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final int taskId1 = fileBackedTaskManager.addNewTask(task1);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(Path.of(path).toFile());
        final List<Task> taskList = fileBackedTaskManager1.getAllTasks();
        final int size = taskList.size();

        assertEquals(2, size, "Неверное кол-во задач");
    }
}


