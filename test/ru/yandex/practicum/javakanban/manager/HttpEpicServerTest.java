package ru.yandex.practicum.javakanban.manager;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpEpicServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HttpEpicServerTest() throws IOException {
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
    public void shouldAdd1Epic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");

        String jsonEpic = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа");

        List<Epic> epicList = manager.getAllEpics();

        assertNotNull(epicList, "Подзадачи не найдены");
        assertEquals(1, epicList.size(), "Неверное кол-во задач");
        assertEquals("Epic", epicList.get(0).getTitle(), "Подзадачи не совпадают");
    }

    @Test
    public void shouldAddUpdatedEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Ed");
        int epicId = manager.addNewEpic(epic);

        Epic epicNew = new Epic("Epic New",
                "Epic description New",
                epicId);


        String jsonTask1 = gson.toJson(epicNew);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа");

        List<Epic> epicList = manager.getAllEpics();

        assertNotNull(epicList, "Подзадачи не найдены");
        assertEquals(1, epicList.size(), "Неверное кол-во задач");
        assertEquals(epicNew.getTitle(), epicList.get(0).getTitle(), "Подзадачи не совпадают");
        assertEquals(epicNew.getDescription(), epicList.get(0).getDescription(), "Подзадачи не совпадают");
    }

    @Test
    public void ShouldReturnAddedEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Ed");
        int epicId = manager.addNewEpic(epic);

        URI urlGet = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Epic> epicList = manager.getAllEpics();

        assertNotNull(epicList, "Подзадачи не найдены");
        assertEquals(1, epicList.size(), "Неверное кол-во задач");
        assertEquals(epic.getTitle(), epicList.get(0).getTitle(), "Подзадачи не совпадают");
        assertEquals(epic.getDescription(), epicList.get(0).getDescription(), "Подзадачи не совпадают");
    }

    @Test
    public void ShouldReturnAddedEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("E", "Ed");
        int epicId = manager.addNewEpic(epic);

        URI urlGet = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        Epic epicNew = gson.fromJson(response.body(), Epic.class);

        assertNotNull(epicNew, "Подзадачи не найдены");
        assertEquals(epic.getTitle(), epicNew.getTitle(), "Подзадачи не совпадают");
        assertEquals(epic.getDescription(), epicNew.getDescription(), "Подзадачи не совпадают");
        assertEquals(epic.getStatus(), epicNew.getStatus(), "Подзадачи не совпадают");
        assertEquals(epic.getStartTime(), epicNew.getStartTime(), "Подзадачи не совпадают");
        assertEquals(epic.getDuration(), epicNew.getDuration(), "Подзадачи не совпадают");
        assertEquals(epic.getEndTime(), epicNew.getEndTime(), "Подзадачи не совпадают");
    }

    @Test
    public void shouldReturnEmptyListOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("E", "Ed");
        int epicId = manager.addNewEpic(epic);

        URI urlDelete = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Epic> epicList = manager.getAllEpics();

        assertEquals(0, epicList.size(), "Неверное кол-во задач");
    }

    @Test
    public void shouldReturnMistakeWhenUncorrectedRequest() throws IOException, InterruptedException {
        URI urlPUT = URI.create("http://localhost:8080/epics");
        HttpRequest requestPUT = HttpRequest.newBuilder()
                .uri(urlPUT)
                .PUT(HttpRequest.BodyPublishers.ofString("forTest"))
                .build();
        HttpResponse<String> responsePUT = client.send(requestPUT, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, responsePUT.statusCode(), "Неверный код ответа");

        URI urlPOST = URI.create("http://localhost:8080/epics/fe/");
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(urlPOST)
                .POST(HttpRequest.BodyPublishers.ofString("forTest"))
                .build();
        HttpResponse<String> responsePOST = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, responsePOST.statusCode(), "Неверный код ответа");
    }

    @Test
    public void shouldReturnMistake404WhenEpicIsMissing() throws IOException, InterruptedException {
        URI urlGet = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный код ответа");
    }
}
