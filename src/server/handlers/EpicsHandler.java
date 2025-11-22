package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import data.Endpoint;
import data.Epic;
import data.Subtask;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        Endpoint endpoint = getEndpoint(method, path);

        switch (endpoint) {

            case GET_EPIC -> {

                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                Epic epic = manager.getEpic(id);
                if (epic == null) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                String jsonEpic;
                try {
                    jsonEpic = gson.toJson(epic);
                } catch (Exception exception) {
                    sendServerError(exchange, endpoint);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, endpoint, jsonEpic);

            }

            case GET_ALL_EPICS -> {

                List<Epic> epicList = manager.getAllEpics();
                if (epicList.isEmpty()) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                String jsonEpicList;
                try {
                    jsonEpicList = gson.toJson(epicList);
                } catch (Exception exception) {
                    sendServerError(exchange, endpoint);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, endpoint, jsonEpicList);

            }

            case POST_NEW_EPIC -> {

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Epic epic;
                try {
                    epic = gson.fromJson(body, Epic.class);
                } catch (Exception e) {
                    sendBadRequestBoby(exchange, endpoint);
                    System.out.println(e.getMessage());
                    return;
                }

                Integer epicId;
                try {
                    epicId = manager.addEpic(epic).getId();
                } catch (Exception exception) {
                    sendServerError(exchange, endpoint);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, endpoint, "epic adding success, epicId=" + epicId);

            }

            case POST_UPDATE_EPIC -> {

                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Epic epic;
                try {
                    epic = gson.fromJson(body, Epic.class);
                } catch (Exception e) {
                    sendBadRequestBoby(exchange, endpoint);
                    System.out.println(e.getMessage());
                    return;
                }

                Integer epicId = epic.getId();
                if (epicId == null || id != epicId) {
                    sendUnprocessableEntity(exchange, endpoint);
                    return;
                }

                if (manager.updateEpic(epic) == null) {
                    sendNotFound(exchange, endpoint);
                } else {
                    sendText(exchange, endpoint, "epic updated success");
                }

            }

            case DELETE_EPIC -> {

                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                if (manager.removeEpic(id)) {
                    sendText(exchange, endpoint, "epic removed success");
                } else {
                    sendNotFound(exchange, endpoint);
                }

            }

            case GET_ALL_SUBTASKS_BY_EPIC -> {

                String pathId = path.replaceFirst("/epics/", "");
                pathId = pathId.replaceFirst("/subtasks", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                List<Subtask> subtaskList = manager.getAllSubTasksByEpic(id);
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

            case UNKNOWN -> sendFormatException(exchange, endpoint, path);
        }
    }
}
