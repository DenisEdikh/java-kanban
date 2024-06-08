package ru.yandex.practicum.javakanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.javakanban.manager.Managers;
import ru.yandex.practicum.javakanban.manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        switch (getMethodOfHttp(exchange)) {
            case GET:
                if (path.matches("/prioritized$")) {
                    String response = gson.toJson(manager.getPrioritizedTasks());
                    sendGoodRequest(exchange, response);
                    break;
                }
            default:
                sendMethodNotAllowed(exchange);
        }
    }
}
