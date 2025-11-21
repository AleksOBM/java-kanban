package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import data.Endpoint;
import data.Task;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        Endpoint endpoint = getEndpoint(method, path);

        switch (endpoint) {

            case GET_PRIORITIZED -> {

                List<Task> prioritizedList = manager.getPrioritizedTasks();
                if (prioritizedList.isEmpty()) {
                    sendNotFound(exchange, Endpoint.GET_PRIORITIZED);
                    return;
                }

                String jsonPrioritizedList;
                try {
                    jsonPrioritizedList = gson.toJson(prioritizedList);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_PRIORITIZED);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, Endpoint.GET_PRIORITIZED, jsonPrioritizedList);

            }

            case UNKNOWN -> sendFormatException(exchange, Endpoint.UNKNOWN, path);
        }
    }
}
