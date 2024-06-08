package ru.yandex.practicum.javakanban.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.javakanban.exception.CreateJsonException;
import ru.yandex.practicum.javakanban.exception.ManagerSaveException;
import ru.yandex.practicum.javakanban.exception.ManagerTimeException;
import ru.yandex.practicum.javakanban.exception.TaskNotFoundException;
import ru.yandex.practicum.javakanban.manager.TaskManager;
import ru.yandex.practicum.javakanban.model.Subtask;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            Optional<Integer> subtaskId = getIdFromHttp(exchange);

            switch (getMethodOfHttp(exchange)) {
                case GET:
                    if (path.matches("/subtasks$")) {
                        String response = gson.toJson(manager.getAllSubtask());
                        sendGoodRequest(exchange, response);
                        break;
                    }

                    if (path.matches("/subtasks/\\d+$")) {
                        if (subtaskId.isPresent()) {
                            String response = gson.toJson(manager.getSubtaskById(subtaskId.get()));
                            sendGoodRequest(exchange, response);
                        } else {
                            sendBadRequest(exchange);
                        }
                        break;
                    } else {
                        sendBadRequest(exchange);
                        break;
                    }
                case POST:
                    if (path.matches("/subtasks$")) {
                        final Subtask subtask = fromJsonToSubtask(exchange);

                        if (subtask.getId() == 0) {
                            manager.addNewSubtask(subtask);
                        } else {
                            manager.updateSubtask(subtask);
                        }
                        sendGoodPost(exchange);
                    } else {
                        sendBadRequest(exchange);
                    }
                    break;
                case DELETE:
                    if (path.matches("/subtasks/\\d+$")) {

                        if (subtaskId.isPresent()) {
                            manager.removeSubtaskById(subtaskId.get());
                            sendGoodRequest(exchange, "Запрос обработан успешно");
                        } else {
                            sendBadRequest(exchange);
                        }
                        break;
                    } else {
                        sendBadRequest(exchange);
                    }
                    break;
                default:
                    sendMethodNotAllowed(exchange);
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (ManagerTimeException | CreateJsonException e) {
            sendNotAcceptable(exchange, e.getMessage());
        } catch (ManagerSaveException e) {
            sendInternalServerError(exchange);
        }
    }

    private Subtask fromJsonToSubtask(HttpExchange exchange) throws IOException {
        String requestBody = readText(exchange);
        JsonObject jo = JsonParser.parseString(requestBody).getAsJsonObject();

        if (checkJson(jo)
                && jo.has("epicId")
                && !jo.has("endTime")
                && !jo.has("subtaskIdS")) {
            return gson.fromJson(requestBody, Subtask.class);
        } else {
            throw new CreateJsonException("JSON некорректен");
        }
    }
}
