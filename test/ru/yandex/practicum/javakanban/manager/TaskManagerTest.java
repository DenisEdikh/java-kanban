package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.yandex.practicum.javakanban.exception.ManagerTimeException;
import ru.yandex.practicum.javakanban.exception.TaskNotFoundException;
import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Status;
import ru.yandex.practicum.javakanban.model.Subtask;
import ru.yandex.practicum.javakanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Task task1;
    protected Epic epic;
    protected Epic epic1;

    protected TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @BeforeEach
    void beforeEach() {
        task = new Task("Task", "Task description", null, Duration.ofMinutes(10));
        task1 = new Task("Task1",
                "Task1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 30),
                Duration.ofMinutes(10));
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
        Task newTask = new Task("newTask",
                "newTask description",
                Status.DONE,
                LocalDateTime.of(2025, Month.MAY, 22, 16, 45),
                Duration.ofMinutes(10),
                taskId);


        taskManager.updateTask(newTask);

        final List<Task> tasks = taskManager.getAllTasks();
        final Task savedTask = taskManager.getTaskById(taskId);
        int sizeOfTasks = tasks.size();

        assertNotNull(savedTask, "Новая задача не найдена");
        assertEquals(1, sizeOfTasks, "Неверное кол-во задач");
        assertEquals(newTask, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldReturnTasksAndHistoryWithOutDeletedTask() {
        final int taskId = taskManager.addNewTask(task);
        final int taskId1 = taskManager.addNewTask(task1);

        taskManager.getTaskById(taskId);
        taskManager.getTaskById(taskId1);
        taskManager.removeTaskById(taskId);

        final int sizeOfTasks = taskManager.getAllTasks().size();
        final boolean savedTask = taskManager.getHistory().contains(task);

        assertEquals(1, sizeOfTasks, "Неверное кол-во задач");
        assertFalse(savedTask, "Задача найдена");
        assertThrows(TaskNotFoundException.class, () -> taskManager.getTaskById(taskId), "Задача найдена");
    }

    @Test
    void shouldReturnEmptyListOfTasksAndHistory() {
        final int taskId1 = taskManager.addNewTask(task);
        final int taskId2 = taskManager.addNewTask(task1);

        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId2);
        taskManager.removeAllTasks();

        final int savedSizeOfHistory = taskManager.getHistory().size();
        final int savedSizeOfTasks = taskManager.getAllTasks().size();

        assertEquals(0, savedSizeOfTasks, "Задачи найдены");
        assertEquals(0, savedSizeOfHistory, "Задачи найдены в истории");
    }

    @Test
    void shouldReturn1AddedSubtask() {
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2024, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(7),
                epicId);

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
        Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2024, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(7),
                epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        Subtask newSubtask = new Subtask("subtask of epic",
                "subtask of epic newDescription",
                Status.IN_PROGRESS,
                LocalDateTime.of(2025, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(10),
                epicId,
                subtaskId);

        taskManager.updateSubtask(newSubtask);
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
    void shouldReturnSubtasksAndHistoryWithOutDeletedSubtask() {
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2024, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(7),
                epicId);
        Subtask subtask1 = new Subtask("subtask1 of epic",
                "subtask1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(8),
                epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final int subtaskId1 = taskManager.addNewSubtask(subtask1);

        taskManager.getSubtaskById(subtaskId);
        taskManager.getSubtaskById(subtaskId1);

        taskManager.removeSubtaskById(subtaskId);

        final int sizeOfSubtasks = taskManager.getAllSubtask().size();
        final boolean savedSubtask = taskManager.getHistory().contains(subtask);
        final boolean savedList = taskManager.getEpicById(epicId).getSubtaskIdS().contains(subtaskId);

        assertEquals(1, sizeOfSubtasks, "Неверное кол-во задач");
        assertFalse(savedSubtask, "Задача найдена");
        assertFalse(savedList, "Задача найдена");
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtaskById(subtaskId), "Задача найдена");
    }

    @Test
    void shouldReturnEmptyListOfSubtasksAndHistory() {
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2024, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(7),
                epicId);
        Subtask subtask1 = new Subtask("subtask1 of epic",
                "subtask1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(8),
                epicId);

        final int subtaskId = taskManager.addNewSubtask(subtask);
        final int subtaskId1 = taskManager.addNewSubtask(subtask1);

        taskManager.getSubtaskById(subtaskId);
        taskManager.getSubtaskById(subtaskId1);
        taskManager.getSubtaskById(subtaskId1);
        taskManager.removeAllSubtask();

        final int savedSizeOfSubtasks = taskManager.getAllSubtask().size();
        final int savedSizeOfHistory = taskManager.getHistory().size();

        assertEquals(0, savedSizeOfSubtasks, "Подзадачи найдены");
        assertEquals(0, savedSizeOfHistory, "Подзадачи найдены в истории");
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
    void shouldReturnEpicsAndHistoryWithOutDeletedEpics() {
        final int epicId = taskManager.addNewEpic(epic);
        final int epicId1 = taskManager.addNewEpic(epic1);

        Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2024, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(8),
                epicId);

        final int subtaskId = taskManager.addNewSubtask(subtask);

        taskManager.getEpicById(epicId);
        taskManager.getEpicById(epicId);
        taskManager.getEpicById(epicId1);
        taskManager.getSubtaskById(subtaskId);
        taskManager.removeEpicById(epicId);

        final int sizeOfEpics = taskManager.getAllEpics().size();
        final boolean savedEpic = taskManager.getHistory().contains(epic);
        final boolean savedSubtask = taskManager.getHistory().contains(subtask);

        assertEquals(1, sizeOfEpics, "Неверное кол-во эпиков'");
        assertFalse(savedEpic, "Эпик найден в истории");
        assertFalse(savedSubtask, "Подзадача эпика найдена в истории");
        assertThrows(TaskNotFoundException.class, () -> taskManager.getEpicById(epicId), "Эпик найден");
    }

    @Test
    void shouldReturnEmptyListOfEpicsAndHistory() {
        taskManager.addNewEpic(epic);
        taskManager.addNewEpic(epic1);
        taskManager.removeAllEpic();

        final int savedSizeOfEpics = taskManager.getAllEpics().size();
        final int savedSizeOfHistory = taskManager.getHistory().size();

        assertEquals(0, savedSizeOfEpics, "Эпики найдены");
        assertEquals(0, savedSizeOfHistory, "Эпики найдены в истории");
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    void shouldReturnUpdatedStatusEpics_1(Status status) {
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2024, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(7),
                epicId);
        Subtask subtask1 = new Subtask("subtask1 of epic",
                "subtask1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(8),
                epicId);

        subtask.setStatus(status);
        subtask1.setStatus(status);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        assertEquals(status, savedEpic.getStatus(), "Статус некорректен");
    }

    @Test
    void shouldReturnUpdatedStatusEpics_2() {
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2024, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(7),
                epicId);
        Subtask subtask1 = new Subtask("subtask1 of epic",
                "subtask1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 32),
                Duration.ofMinutes(8),
                epicId);
        subtask.setStatus(Status.NEW);
        subtask1.setStatus(Status.DONE);

        final int subtaskId = taskManager.addNewSubtask(subtask);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

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

    @Test
    void shouldNotAddTaskIfHaveOverlayByStartTime() {
        Task task2 = new Task("Task1",
                "Task1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 40),
                Duration.ofMinutes(5));
        Task task3 = new Task("Task1",
                "Task1 description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 20),
                Duration.ofMinutes(10));
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2025, Month.MAY, 22, 16, 35),
                Duration.ofMinutes(10),
                epicId);
        final Subtask subtask1 = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2026, Month.MAY, 22, 17, 20),
                Duration.ofMinutes(200),
                epicId);
        final Subtask subtask2 = new Subtask("subtask of epic",
                "subtask description",
                LocalDateTime.of(2027, Month.MAY, 22, 16, 20),
                Duration.ofMinutes(20),
                epicId);

        final int taskId1 = taskManager.addNewTask(task1);
        final int subtaskId1 = taskManager.addNewSubtask(subtask1);
        final int subtaskId2 = taskManager.addNewSubtask(subtask2);

        assertTrue(taskManager.getAllTasks().contains(task1), "Задача не найдена");
        assertTrue(taskManager.getAllSubtask().contains(subtask1), "Подзадача не найдена");
        assertTrue(taskManager.getAllSubtask().contains(subtask2), "Подзадача не найдена");
        assertThrows(ManagerTimeException.class, () -> taskManager.addNewTask(task2), "Задача найдена");
        assertThrows(ManagerTimeException.class, () -> taskManager.addNewTask(task3), "Задача найдена");
        assertThrows(ManagerTimeException.class, () -> taskManager.addNewSubtask(subtask), "Задача найдена");
        assertEquals(220, epic.getDuration().toMinutes(), "Неверная длительность");
        assertEquals(LocalDateTime.of(2026, Month.MAY, 22, 17, 20),
                epic.getStartTime(), "Неверное время начала эпика");
        assertEquals(LocalDateTime.of(2027, Month.MAY, 22, 16, 40),
                epic.getEndTime(), "Неверное время окончания эпика");
    }

    @Test
    void shouldAdd1TaskToPrioritizedTasks() {
        final int taskId = taskManager.addNewTask(task);
        final int taskId1 = taskManager.addNewTask(task1);
        final int sizeOfPrioritizedTasks = taskManager.getPrioritizedTasks().size();
        final int sizeOfTasks = taskManager.getAllTasks().size();

        assertEquals(1, sizeOfPrioritizedTasks, "Неверное кол-во задач");
        assertEquals(2, sizeOfTasks, "Неверное кол-во задач");
    }
}

