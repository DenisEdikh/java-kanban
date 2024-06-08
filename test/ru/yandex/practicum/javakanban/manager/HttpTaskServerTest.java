package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Status;
import ru.yandex.practicum.javakanban.model.Task;
import ru.yandex.practicum.javakanban.server.BaseHttpHandler;
import ru.yandex.practicum.javakanban.server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtask();
        manager.removeAllEpic();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void shouldAdd1Task() throws IOException, InterruptedException {
        Task task1 = new Task("Task",
                "Task description",
                LocalDateTime.of(2024, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10));

        String jsonTask1 = BaseHttpHandler.gson.toJson(task1);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа");

        List<Task> taskList = manager.getAllTasks();

        assertNotNull(taskList, "Задачи не найдены");
        assertEquals(1, taskList.size(), "Неверное кол-во задач");
        assertEquals("Task", taskList.get(0).getTitle(), "Задачи не совпадают");
    }

    @Test
    public void shouldAddUpdatedTask() throws IOException, InterruptedException {
        manager.addNewTask(new Task("Task",
                "Task description",
                LocalDateTime.of(2024, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10)));

        Task task1 = new Task("Task new",
                "Task updated description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2023, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(100),
                1);

        String jsonTask1 = BaseHttpHandler.gson.toJson(task1);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа");

        List<Task> taskList = manager.getAllTasks();

        assertNotNull(taskList, "Задачи не найдены");
        assertEquals(1, taskList.size(), "Неверное кол-во задач");
        assertEquals(task1.getTitle(), taskList.get(0).getTitle(), "Задачи не совпадают");
        assertEquals(task1.getDescription(), taskList.get(0).getDescription(), "Задачи не совпадают");
        assertEquals(task1.getStatus(), taskList.get(0).getStatus(), "Задачи не совпадают");
        assertEquals(task1.getStartTime(), taskList.get(0).getStartTime(), "Задачи не совпадают");
        assertEquals(task1.getDuration(), taskList.get(0).getDuration(), "Задачи не совпадают");
        assertEquals(task1.getEndTime(), taskList.get(0).getEndTime(), "Задачи не совпадают");
    }

    @Test
    public void ShouldReturnAddedTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task new",
                "Task updated description",
                LocalDateTime.of(2023, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(100));
        manager.addNewTask(task1);

        URI urlGet = URI.create("http://localhost:8080/tasks");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Task> taskList = manager.getAllTasks();

        assertNotNull(taskList, "Задачи не найдены");
        assertEquals(1, taskList.size(), "Неверное кол-во задач");
        assertEquals(task1.getTitle(), taskList.get(0).getTitle(), "Задачи не совпадают");
        assertEquals(task1.getDescription(), taskList.get(0).getDescription(), "Задачи не совпадают");
        assertEquals(task1.getStatus(), taskList.get(0).getStatus(), "Задачи не совпадают");
        assertEquals(task1.getStartTime(), taskList.get(0).getStartTime(), "Задачи не совпадают");
        assertEquals(task1.getDuration(), taskList.get(0).getDuration(), "Задачи не совпадают");
        assertEquals(task1.getEndTime(), taskList.get(0).getEndTime(), "Задачи не совпадают");
    }

    @Test
    public void ShouldReturnAddedTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Task new",
                "Task updated description",
                LocalDateTime.of(2023, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(100));
        int id = manager.addNewTask(task1);

        URI urlGet = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        Task taskNew = BaseHttpHandler.gson.fromJson(response.body(), Task.class);

        assertNotNull(taskNew, "Задачи не найдены");
        assertEquals(task1.getTitle(), taskNew.getTitle(), "Задачи не совпадают");
        assertEquals(task1.getDescription(), taskNew.getDescription(), "Задачи не совпадают");
        assertEquals(task1.getStatus(), taskNew.getStatus(), "Задачи не совпадают");
        assertEquals(task1.getStartTime(), taskNew.getStartTime(), "Задачи не совпадают");
        assertEquals(task1.getDuration(), taskNew.getDuration(), "Задачи не совпадают");
        assertEquals(task1.getEndTime(), taskNew.getEndTime(), "Задачи не совпадают");
    }

    @Test
    public void shouldReturnEmptyListOfTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task new",
                "Task updated description",
                LocalDateTime.of(2023, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(100));
        manager.addNewTask(task1);

        URI urlDelete = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Task> taskList = manager.getAllTasks();

        assertEquals(0, taskList.size(), "Неверное кол-во задач");
    }

    @Test
    public void shouldReturnMistakeWhenUncorrectedRequest() throws IOException, InterruptedException {
        URI urlPUT = URI.create("http://localhost:8080/tasks");
        HttpRequest requestPUT = HttpRequest.newBuilder()
                .uri(urlPUT)
                .PUT(HttpRequest.BodyPublishers.ofString("forTest"))
                .build();
        HttpResponse<String> responsePUT = client.send(requestPUT, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, responsePUT.statusCode(), "Неверный код ответа");

        URI urlPOST = URI.create("http://localhost:8080/tasks/");
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(urlPOST)
                .POST(HttpRequest.BodyPublishers.ofString("forTest"))
                .build();
        HttpResponse<String> responsePOST = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, responsePOST.statusCode(), "Неверный код ответа");
    }

    @Test
    public void shouldReturnMistake404WhenTaskIsMissing() throws IOException, InterruptedException {
        URI urlGet = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный код ответа");
    }
}
