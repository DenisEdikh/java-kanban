package ru.yandex.practicum.javakanban.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.javakanban.manager.TaskManager;

import java.io.IOException;
import java.util.Objects;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (Objects.requireNonNull(getMethodOfHttp(exchange)) == MethodOfHttp.GET) {
            if (path.matches("/prioritized$")) {
                String response = gson.toJson(manager.getPrioritizedTasks());
                sendGoodRequest(exchange, response);
                return;
            }
        }
        sendMethodNotAllowed(exchange);
    }
}
