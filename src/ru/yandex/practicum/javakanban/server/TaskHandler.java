package ru.yandex.practicum.javakanban.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.javakanban.exception.CreateJsonException;
import ru.yandex.practicum.javakanban.exception.ManagerSaveException;
import ru.yandex.practicum.javakanban.exception.ManagerTimeException;
import ru.yandex.practicum.javakanban.exception.TaskNotFoundException;
import ru.yandex.practicum.javakanban.manager.Managers;
import ru.yandex.practicum.javakanban.manager.TaskManager;
import ru.yandex.practicum.javakanban.model.Task;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            Optional<Integer> taskId = getIdFromHttp(exchange);

            switch (getMethodOfHttp(exchange)) {
                case GET:
                    if (path.matches("/tasks$")) {
                        String response = gson.toJson(manager.getAllTasks());
                        sendGoodRequest(exchange, response);
                        break;
                    }

                    if (path.matches("/tasks/\\d+$")) {
                        if (taskId.isPresent()) {
                            String response = gson.toJson(manager.getTaskById(taskId.get()));
                            sendGoodRequest(exchange, response);
                        } else {
                            sendBadRequest(exchange);
                        }
                    } else {
                        sendBadRequest(exchange);
                    }
                    break;
                case POST:
                    if (path.matches("/tasks$")) {
                        final Task task = fromJsonToTask(exchange);

                        if (task.getId() == 0) {
                            manager.addNewTask(task);
                        } else {
                            manager.updateTask(task);
                        }
                        sendGoodPost(exchange);
                    } else {
                        sendBadRequest(exchange);
                    }
                    break;
                case DELETE:
                    if (path.matches("/tasks/\\d+$")) {

                        if (taskId.isPresent()) {
                            manager.removeTaskById(taskId.get());
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

    private Task fromJsonToTask(HttpExchange exchange) throws IOException {
        String requestBody = readText(exchange);
        JsonObject jo = JsonParser.parseString(requestBody).getAsJsonObject();

        if (checkJson(jo)
                && !jo.has("epicId")
                && !jo.has("endTime")
                && !jo.has("subtaskIdS")) {
            return gson.fromJson(requestBody, Task.class);
        } else {
            throw new CreateJsonException("JSON некорректен");
        }
    }

}
