package ru.yandex.practicum.javakanban.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Task;
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

public class HttpPrioritizedTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();


    public HttpPrioritizedTest() throws IOException {
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
    public void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        manager.addNewTask(new Task("T1",
                "T1d",
                LocalDateTime.of(2024, Month.JUNE, 8, 17, 55),
                Duration.ofMinutes(100)));
        manager.addNewEpic(new Epic("Epic", "Epic description"));

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestGet = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Task> prioritizedList = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        assertNotNull(prioritizedList, "Задачи не найдены");
        assertEquals(manager.getPrioritizedTasks().size(), prioritizedList.size(), "Неверное кол-во задач");
        assertEquals(manager.getPrioritizedTasks().get(0), prioritizedList.get(0), "Задачи не совпадают");
    }
}
