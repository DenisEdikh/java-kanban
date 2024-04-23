package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Status;
import ru.yandex.practicum.javakanban.model.Subtask;
import ru.yandex.practicum.javakanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Task task1;
    private Epic epic;
    private Epic epic1;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        task = new Task("Task", "Task description");
        task1 = new Task("Task1", "Task1 description");
        epic = new Epic("epic", "epic description");
        epic1 = new Epic("Epic1", "Epic1 description");
    }


    @Test
    void shouldReturn1AddedTask() {
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getAllTasks();
        int sizeOfTasks = tasks.size();

        assertNotNull(tasks, "Задачи не найдены");
        assertEquals(1, sizeOfTasks, "Неверное кол-во задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldReturn1UpdatedTask() {
        final int taskId = taskManager.addNewTask(task);
        Task newTask = new Task("newTask", "newTask description", taskId);

        taskManager.updateTask(newTask);

        final List<Task> tasks = taskManager.getAllTasks();
        final Task savedTask = taskManager.getTaskById(taskId);
        int sizeOfTasks = tasks.size();

        assertNotNull(savedTask, "Новая задача не найдена");
        assertEquals(1, sizeOfTasks, "Неверное кол-во задач");
        assertEquals(newTask, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldReturnTasksWithOutDeletedTask() {
        final int taskId = taskManager.addNewTask(task);

        taskManager.removeTaskById(taskId);

        final List<Task> tasks = taskManager.getAllTasks();
        final int sizeOfTasks = tasks.size();
        final Task removedTask = taskManager.getTaskById(taskId);

        assertEquals(0, sizeOfTasks, "Неверное кол-во задач");
        assertNull(removedTask, "Задача найдена");
    }

    @Test
    void shouldReturnEmptyListOfTasks() {
        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);
        taskManager.removeAllTasks();

        final List<Task> tasks = taskManager.getAllTasks();
        final int savedSizeOfTasks = tasks.size();

        assertEquals(0, savedSizeOfTasks, "Задачи найдены");
    }

    @Test
    void shouldReturn1AddedSubtask() {
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("subtask of epic", "subtask description", epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
        assertTrue(epic.getSubtaskIdS().contains(subtaskId), "Подзадача не найдена в эпике");


        final List<Subtask> subtasks = taskManager.getAllSubtask();
        final int sizeOfSubtasks = subtasks.size();

        assertNotNull(subtasks, "Подзадачи не найдены");
        assertEquals(1, sizeOfSubtasks, "Неверное кол-во подзадач");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldReturn1UpdatedSubtask() {
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("subtask of epic", "subtask of epic description", epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        Subtask newSubtask = new Subtask("subtask of epic",
                "subtask of epic newDescription", Status.IN_PROGRESS, subtaskId, epicId);

        taskManager.updateSubTask(newSubtask);
        final Subtask savedNewSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedNewSubtask, "Подзадача не найдена");
        assertEquals(newSubtask, savedNewSubtask, "Подзадачи не совпадают");

        final List<Subtask> subtasks = taskManager.getAllSubtask();
        final int sizeOfSubtasks = subtasks.size();

        assertNotNull(subtasks, "Подзадачи не найдены");
        assertEquals(1, sizeOfSubtasks, "Неверное кол-во подзадач");
        assertEquals(newSubtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void shouldReturnSubtasksWithOutDeletedSubtask() {
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("subtask of epic", "subtask of epic description", epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        taskManager.removeSubtaskById(subtaskId);

        final List<Subtask> subtasks = taskManager.getAllSubtask();
        final int sizeOfTasks = subtasks.size();
        final Subtask removedSubtask = taskManager.getSubtaskById(subtaskId);
        final boolean savedList = taskManager.getEpicById(epicId).getSubtaskIdS().contains(subtaskId);

        assertEquals(0, sizeOfTasks, "Неверное кол-во задач");
        assertNull(removedSubtask, "Задача найдена");
        assertFalse(savedList, "Задача найдена");
    }

    @Test
    void shouldReturnEmptyListOfSubtasks() {
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", epicId);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epicId);

        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask1);
        taskManager.removeAllSubtask();

        final List<Subtask> subtasks = taskManager.getAllSubtask();
        final int savedSizeOfSubtasks = subtasks.size();

        assertEquals(0, savedSizeOfSubtasks, "Подзадачи найдены");
    }

    @Test
    void shouldReturn1AddedEpic() {
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Подзадача не найдена");
        assertEquals(epic, savedEpic, "Подзадачи не совпадают");

        final List<Epic> savedEpics = taskManager.getAllEpics();
        final int sizeOfEpics = savedEpics.size();

        assertNotNull(savedEpics, "Эпики не найдены");
        assertEquals(1, sizeOfEpics, "Неверное кол-во эпиков");
        assertEquals(savedEpic, savedEpics.get(0));
    }

    @Test
    void shouldReturn1UpdatedEpic() {
        final int epicId = taskManager.addNewEpic(epic);
        Epic newEpic = new Epic("newEpic", "newEpic descriptionNew", epicId);
        taskManager.updateEpic(newEpic);

        final List<Epic> epics = taskManager.getAllEpics();
        final Epic savedEpic = taskManager.getEpicById(epicId);
        final int sizeOfEpics = epics.size();

        assertNotNull(savedEpic, "Новый эпик не найден");
        assertEquals(1, sizeOfEpics, "Неверное кол-во эпиков");
        assertEquals(newEpic, epics.get(0), "Эпики не совпадают");
    }

    @Test
    void shouldReturnEpicsWithOutDeletedEpics() {
        final int epicId = taskManager.addNewEpic(epic);

        taskManager.removeEpicById(epicId);

        final List<Epic> epics = taskManager.getAllEpics();
        final int sizeOfEpics = epics.size();
        final Epic removedEpic = taskManager.getEpicById(epicId);

        assertEquals(0, sizeOfEpics, "Неверное кол-во эпиков'");
        assertNull(removedEpic, "Эпик найден");
    }

    @Test
    void shouldReturnEmptyListOfEpics() {
        taskManager.addNewEpic(epic);
        taskManager.addNewEpic(epic1);
        taskManager.removeAllEpic();

        final List<Epic> epics = taskManager.getAllEpics();
        final int savedSizeOfEpics = epics.size();

        assertEquals(0, savedSizeOfEpics, "Эпики найдены");
    }

    @Test
    void shouldReturnUpdatedStatusEpics() {
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        Subtask subtask = new Subtask("subtask", "subtask description", epicId);
        Subtask subtask1 = new Subtask("subtask1", "subtask1 description", epicId);

        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус некорректне");

        final int subtaskId = taskManager.addNewSubtask(subtask);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус некорректен");

        Subtask newSubtask1 = new Subtask("newSubtask1", "newSubtask1 description",
                Status.DONE, subtask1Id, epicId);

        taskManager.updateSubTask(newSubtask1);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус некорректен");
    }

    @Test
    void shouldReturnEqualityOfTasksIfIdsEqual() {
        taskManager.addNewTask(task);
        task1.setId(1);

        assertEquals(task, task1, "Задачи не равны");

    }

    @Test
    void shouldReturnSameFieldsAfterAddingToTaskManager() {
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertEquals(task.getTitle(), savedTask.getTitle(), "Задача изменилась");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Задача изменилась");
    }

    @Test
    void shouldReturn2EntriesOfHistory() {
        final int taskId = taskManager.addNewTask(task);
        final int taskId1 = taskManager.addNewTask(task1);
        taskManager.getTaskById(taskId);
        taskManager.getTaskById(taskId);
        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId);

        final List<Task> history = taskManager.getHistory();
        final int sizeOfHistory = history.size();

        assertEquals(2, sizeOfHistory, "История некорректна");
    }
}
