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
import ru.yandex.practicum.javakanban.model.Epic;

import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            Optional<Integer> epicId = getIdFromHttp(exchange);

            switch (getMethodOfHttp(exchange)) {
                case GET:
                    if (path.matches("/epics$")) {

                        String response = gson.toJson(manager.getAllEpics());
                        sendGoodRequest(exchange, response);
                        break;
                    } else if (path.matches("/epics/\\d+$")) {

                        if (epicId.isPresent()) {
                            String response = gson.toJson(manager.getEpicById(epicId.get()));
                            sendGoodRequest(exchange, response);
                        } else {
                            sendBadRequest(exchange);
                        }
                        break;
                    } else if (path.matches("/epics/\\d+/subtasks$")) {

                        if (epicId.isPresent()) {
                            String response = gson.toJson(manager.getEpicSubtasks(epicId.get()));
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
                    if (path.matches("/epics$")) {
                        Epic epic = fromJsonToEpic(exchange);

                        if (epic.getId() == 0) {
                            manager.addNewEpic(epic);
                        } else {
                            manager.updateEpic(epic);
                        }
                        sendGoodPost(exchange);
                    } else {
                        sendBadRequest(exchange);
                    }
                    break;
                case DELETE:
                    if (path.matches("/epics/\\d+$")) {

                        if (epicId.isPresent()) {
                            manager.removeEpicById(epicId.get());
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

    private Epic fromJsonToEpic(HttpExchange exchange) throws IOException {
        String requestBody = readText(exchange);
        JsonObject jo = JsonParser.parseString(requestBody).getAsJsonObject();

        if (checkJson(jo)
                && jo.has("endTime")
                && jo.has("subtaskIdS")
                && !jo.has("epicId")) {
            return gson.fromJson(requestBody, Epic.class);
        } else {
            throw new CreateJsonException("JSON некорректен");
        }
    }

//    TypeToken<ArrayList<Integer>>
}


