package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import data.Endpoint;
import data.Task;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        Endpoint endpoint = getEndpoint(method, path);

        switch (endpoint) {

            case GET_HISTORY -> {

                List<Task> historyList = manager.getHistory();
                if (historyList.isEmpty()) {
                    sendNotFound(exchange, Endpoint.GET_HISTORY);
                    return;
                }

                String jsonHistoryList;
                try {
                    jsonHistoryList = gson.toJson(historyList);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_HISTORY);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, Endpoint.GET_HISTORY, jsonHistoryList);

            }

            case UNKNOWN -> sendFormatException(exchange, Endpoint.UNKNOWN, path);
        }
    }
}
