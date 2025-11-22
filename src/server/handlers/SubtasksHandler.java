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
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                Subtask subtask = manager.getSubtask(id);
                if (subtask == null) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                String jsonSubtask;
                try {
                    jsonSubtask = gson.toJson(subtask);
                } catch (Exception exception) {
                    sendServerError(exchange, endpoint);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, endpoint, jsonSubtask);

            }

            case GET_ALL_SUBTASKS -> {

                List<Subtask> subtaskList = manager.getAllSubtasks();
                if (subtaskList.isEmpty()) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                String jsonSubtaskList;
                try {
                    jsonSubtaskList = gson.toJson(subtaskList);
                } catch (Exception exception) {
                    sendServerError(exchange, endpoint);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, endpoint, jsonSubtaskList);

            }

            case POST_NEW_SUBTASK -> {

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Subtask subtask;
                try {
                    subtask = gson.fromJson(body, Subtask.class);
                } catch (Exception e) {
                    sendBadRequestBoby(exchange, endpoint);
                    System.out.println(e.getMessage());
                    return;
                }

                Integer epicId = subtask.getEpicId();
                if (epicId == null) {
                    sendUnprocessableEntity(exchange, endpoint);
                } else if (manager.getWithoutHistory(epicId) == null) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                Subtask newSubtask = manager.addSubtask(subtask);

                if (newSubtask == null) {
                    sendHasOverlaps(exchange, endpoint, subtask);
                } else {
                    sendText(exchange, endpoint, "subtask adding success, subtaskId=" + newSubtask.getId());
                }

            }

            case POST_UPDATE_SUBTASK -> {

                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Subtask subtask;
                try {
                    subtask = gson.fromJson(body, Subtask.class);
                } catch (Exception e) {
                    sendBadRequestBoby(exchange, endpoint);
                    System.out.println(e.getMessage());
                    return;
                }

                Integer subtaskId = subtask.getId();
                if (subtaskId == null || id != subtaskId) {
                    sendUnprocessableEntity(exchange, endpoint);
                } else if (manager.getWithoutHistory(subtaskId) == null) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                if (manager.updateSubtask(subtask) == null) {
                    sendHasOverlaps(exchange, endpoint, subtask);
                } else {
                    sendText(exchange, endpoint, "subtask updated success");
                }

            }

            case DELETE_SUBTASK -> {

                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                if (manager.removeSubtask(id)) {
                    sendText(exchange, endpoint, "subtask removed success");
                } else {
                    sendNotFound(exchange, endpoint);
                }

            }

            case UNKNOWN -> sendFormatException(exchange, endpoint, path);
        }
    }
}
