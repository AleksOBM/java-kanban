package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import data.Endpoint;
import data.Subtask;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        Endpoint endpoint = getEndpoint(method, path);

        switch (endpoint) {

            case GET_SUBTASK -> {

                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.GET_SUBTASK, path);
                    return;
                }

                Subtask subtask = manager.getSubtask(id);
                if (subtask == null) {
                    sendNotFound(exchange, Endpoint.GET_SUBTASK);
                    return;
                }

                String jsonSubtask;
                try {
                    jsonSubtask = gson.toJson(subtask);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_SUBTASK);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, Endpoint.GET_SUBTASK, jsonSubtask);

            }

            case GET_ALL_SUBTASKS -> {

                List<Subtask> subtaskList = manager.getAllSubtasks();
                if (subtaskList.isEmpty()) {
                    sendNotFound(exchange, Endpoint.GET_ALL_SUBTASKS);
                    return;
                }

                String jsonSubtaskList;
                try {
                    jsonSubtaskList = gson.toJson(subtaskList);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_ALL_SUBTASKS);
                    System.out.println(exception.getMessage());
                    return;
                }
                sendText(exchange, Endpoint.GET_ALL_SUBTASKS, jsonSubtaskList);

            }

            case POST_NEW_SUBTASK -> {

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Subtask subtask;
                try {
                    subtask = gson.fromJson(body, Subtask.class);
                } catch (Exception e) {
                    sendServerError(exchange, Endpoint.POST_NEW_SUBTASK);
                    System.out.println(e.getMessage());
                    return;
                }

                if (manager.addSubtask(subtask) == null) {
                    sendHasOverlaps(exchange, Endpoint.POST_NEW_SUBTASK, subtask);
                } else {
                    sendText(exchange, Endpoint.POST_NEW_SUBTASK,"subtask adding success");
                }

            }

            case POST_UPDATE_SUBTASK -> {

                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.POST_UPDATE_SUBTASK, path);
                    return;
                }

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Subtask subtask;
                try {
                    subtask = gson.fromJson(body, Subtask.class);
                } catch (Exception e) {
                    sendServerError(exchange, Endpoint.POST_UPDATE_SUBTASK);
                    System.out.println(e.getMessage());
                    return;
                }

                if (manager.updateSubtask(subtask) == null) {
                    sendNotFound(exchange, Endpoint.POST_UPDATE_SUBTASK);
                } else {
                    sendText(exchange, Endpoint.POST_UPDATE_SUBTASK,"subtask updated success");
                }

            }

            case DELETE_SUBTASK -> {

                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.DELETE_SUBTASK, path);
                    return;
                }

                if (manager.removeSubtask(id)) {
                    sendText(exchange, Endpoint.DELETE_SUBTASK, "subtask removed success");
                } else {
                    sendServerError(exchange, Endpoint.DELETE_SUBTASK);
                }

            }

            case UNKNOWN -> sendFormatException(exchange, Endpoint.UNKNOWN, path);
        }
    }
}
