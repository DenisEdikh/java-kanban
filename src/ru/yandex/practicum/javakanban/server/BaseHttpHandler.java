package ru.yandex.practicum.javakanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.javakanban.adapter.DurationAdapter;
import ru.yandex.practicum.javakanban.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    // Проверка наличия основных полей, когда прилетает задача от клиента
    protected boolean checkJson(JsonObject jo) {
        return jo.has("title")
                && jo.has("description")
                && jo.has("status")
                && jo.has("id")
                && jo.has("startTime")
                && jo.has("duration");
    }

    // Метод чтения тела запроса и представление в виде строки
    protected String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendGoodRequest(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }

    protected void sendGoodPost(HttpExchange exchange) throws IOException {
        sendText(exchange, "Данные добавлены успешно", 201);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, "Некорректный идентификатор", 400);
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 404);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendText(exchange, "Метод не поддерживается", 405);
    }

    protected void sendNotAcceptable(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 406);
    }

    protected void sendInternalServerError(HttpExchange exchange) throws IOException {
        sendText(exchange, "Внутренняя ошибка сервера", 500);
    }

    // Метод отправки данных HTTP ответа
    protected void sendText(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    // Метод определения HTTP-метода в запросе
    protected MethodOfHttp getMethodOfHttp(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        return switch (method) {
            case "GET" -> MethodOfHttp.GET;
            case "POST" -> MethodOfHttp.POST;
            case "DELETE" -> MethodOfHttp.DELETE;
            default -> MethodOfHttp.UNKNOWN;
        };
    }

    // Метод определения id в пути
    protected Optional<Integer> getIdFromHttp(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] pathSplit = path.split("/");

        try {
            return Optional.of(Integer.parseInt(pathSplit[2]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    protected enum MethodOfHttp {
        GET,
        POST,
        DELETE,
        UNKNOWN;
    }
}
