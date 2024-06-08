package ru.yandex.practicum.javakanban.manager;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Status;
import ru.yandex.practicum.javakanban.model.Subtask;
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

public class HttpSubtaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HttpSubtaskServerTest() throws IOException {
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
    public void shouldAdd1SubtaskWhenEpicIsMissing() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Subtask",
                "Subtask description",
                LocalDateTime.of(2024, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10),
                1);

        String jsonSubtask1 = gson.toJson(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask1))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtaskList = manager.getAllSubtask();

        assertEquals(404, response.statusCode(), "Неверный код ответа");
        assertEquals(0, subtaskList.size(), "Неверное кол-во задач");
    }

    @Test
    public void shouldAdd1Subtask() throws IOException, InterruptedException {
        Epic epic = new Epic("E", "Ed");
        int epicId = manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask",
                "Subtask description",
                LocalDateTime.of(2024, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10),
                epicId);

        String jsonSubtask1 = gson.toJson(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask1))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа");

        List<Subtask> subtaskList = manager.getAllSubtask();
        List<Subtask> subtaskListOfEpic = manager.getEpicSubtasks(epicId);

        assertNotNull(subtaskList, "Подзадачи не найдены");
        assertEquals(1, subtaskList.size(), "Неверное кол-во задач");
        assertEquals(1, subtaskListOfEpic.size(), "Неверное кол-во задач");
        assertEquals("Subtask", subtaskList.get(0).getTitle(), "Подзадачи не совпадают");
    }

    @Test
    public void shouldAddUpdatedSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("E", "Ed");
        int epicId = manager.addNewEpic(epic);
        int subtaskId = manager.addNewSubtask(new Subtask("Subtask",
                "Subtask description",
                LocalDateTime.of(2024, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10),
                epicId));

        Subtask subtask1 = new Subtask("Subtask New",
                "Subtask description New",
                Status.IN_PROGRESS,
                LocalDateTime.of(2023, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10),
                epicId,
                subtaskId);


        String jsonTask1 = gson.toJson(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа");

        List<Subtask> subtaskList = manager.getAllSubtask();

        assertNotNull(subtaskList, "Подзадачи не найдены");
        assertEquals(1, subtaskList.size(), "Неверное кол-во задач");
        assertEquals(subtask1.getTitle(), subtaskList.get(0).getTitle(), "Подзадачи не совпадают");
        assertEquals(subtask1.getDescription(), subtaskList.get(0).getDescription(), "Подзадачи не совпадают");
        assertEquals(subtask1.getStatus(), subtaskList.get(0).getStatus(), "Подзадачи не совпадают");
        assertEquals(subtask1.getStartTime(), subtaskList.get(0).getStartTime(), "Подзадачи не совпадают");
        assertEquals(subtask1.getDuration(), subtaskList.get(0).getDuration(), "Подзадачи не совпадают");
        assertEquals(subtask1.getEndTime(), subtaskList.get(0).getEndTime(), "Подзадачи не совпадают");
    }

    @Test
    public void ShouldReturnAddedSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("E", "Ed");
        int epicId = manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask",
                "Subtask description",
                LocalDateTime.of(2024, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10),
                epicId);
        int subtaskId = manager.addNewSubtask(subtask1);

        URI urlGet = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Subtask> subtaskList = manager.getAllSubtask();

        assertNotNull(subtaskList, "Подзадачи не найдены");
        assertEquals(1, subtaskList.size(), "Неверное кол-во задач");
        assertEquals(subtask1.getTitle(), subtaskList.get(0).getTitle(), "Подзадачи не совпадают");
        assertEquals(subtask1.getDescription(), subtaskList.get(0).getDescription(), "Подзадачи не совпадают");
        assertEquals(subtask1.getStatus(), subtaskList.get(0).getStatus(), "Подзадачи не совпадают");
        assertEquals(subtask1.getStartTime(), subtaskList.get(0).getStartTime(), "Подзадачи не совпадают");
        assertEquals(subtask1.getDuration(), subtaskList.get(0).getDuration(), "Подзадачи не совпадают");
        assertEquals(subtask1.getEndTime(), subtaskList.get(0).getEndTime(), "Подзадачи не совпадают");
    }

    @Test
    public void ShouldReturnAddedSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("E", "Ed");
        int epicId = manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask",
                "Subtask description",
                LocalDateTime.of(2024, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10),
                epicId);
        int subtaskId = manager.addNewSubtask(subtask1);

        URI urlGet = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        Subtask subtaskNew = gson.fromJson(response.body(), Subtask.class);

        assertNotNull(subtaskNew, "Подзадачи не найдены");
        assertEquals(subtask1.getTitle(), subtaskNew.getTitle(), "Подзадачи не совпадают");
        assertEquals(subtask1.getDescription(), subtaskNew.getDescription(), "Подзадачи не совпадают");
        assertEquals(subtask1.getStatus(), subtaskNew.getStatus(), "Подзадачи не совпадают");
        assertEquals(subtask1.getStartTime(), subtaskNew.getStartTime(), "Подзадачи не совпадают");
        assertEquals(subtask1.getDuration(), subtaskNew.getDuration(), "Подзадачи не совпадают");
        assertEquals(subtask1.getEndTime(), subtaskNew.getEndTime(), "Подзадачи не совпадают");
    }

    @Test
    public void shouldReturnEmptyListOfSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("E", "Ed");
        int epicId = manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask",
                "Subtask description",
                LocalDateTime.of(2024, Month.JUNE, 8, 12, 0),
                Duration.ofMinutes(10),
                epicId);
        int subtaskId = manager.addNewSubtask(subtask1);

        URI urlDelete = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Subtask> subtaskList = manager.getAllSubtask();

        assertEquals(0, subtaskList.size(), "Неверное кол-во задач");
    }

    @Test
    public void shouldReturnMistakeWhenUncorrectedRequest() throws IOException, InterruptedException {
        URI urlPUT = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestPUT = HttpRequest.newBuilder()
                .uri(urlPUT)
                .PUT(HttpRequest.BodyPublishers.ofString("forTest"))
                .build();
        HttpResponse<String> responsePUT = client.send(requestPUT, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, responsePUT.statusCode(), "Неверный код ответа");

        URI urlPOST = URI.create("http://localhost:8080/subtasks/");
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(urlPOST)
                .POST(HttpRequest.BodyPublishers.ofString("forTest"))
                .build();
        HttpResponse<String> responsePOST = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, responsePOST.statusCode(), "Неверный код ответа");
    }

    @Test
    public void shouldReturnMistake404WhenSubtaskIsMissing() throws IOException, InterruptedException {
        URI urlGet = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный код ответа");
    }
}
