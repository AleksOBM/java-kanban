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
                    sendFormatException(exchange, Endpoint.GET_EPIC, path);
                    return;
                }

                Epic epic = manager.getEpic(id);
                if (epic == null) {
                    sendNotFound(exchange, Endpoint.GET_EPIC);
                    return;
                }

                String jsonEpic;
                try {
                    jsonEpic = gson.toJson(epic);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_EPIC);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, Endpoint.GET_EPIC, jsonEpic);

            }

            case GET_ALL_EPICS -> {

                List<Epic> epicList = manager.getAllEpics();
                if (epicList.isEmpty()) {
                    sendNotFound(exchange, Endpoint.GET_ALL_EPICS);
                    return;
                }

                String jsonEpicList;
                try {
                    jsonEpicList = gson.toJson(epicList);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_ALL_EPICS);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, Endpoint.GET_ALL_EPICS, jsonEpicList);

            }

            case POST_NEW_EPIC -> {

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Epic epic;
                try {
                    epic = gson.fromJson(body, Epic.class);
                } catch (Exception e) {
                    sendServerError(exchange, Endpoint.POST_NEW_EPIC);
                    System.out.println(e.getMessage());
                    return;
                }

                if (manager.addEpic(epic) == null) {
                    sendHasOverlaps(exchange, Endpoint.POST_NEW_EPIC, epic);
                } else {
                    sendText(exchange, Endpoint.POST_NEW_EPIC,"epic adding success");
                }

            }

            case POST_UPDATE_EPIC -> {

                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.POST_UPDATE_EPIC, path);
                    return;
                }

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Epic epic;
                try {
                    epic = gson.fromJson(body, Epic.class);
                } catch (Exception e) {
                    sendServerError(exchange, Endpoint.POST_UPDATE_EPIC);
                    System.out.println(e.getMessage());
                    return;
                }

                if (manager.updateEpic(epic) == null) {
                    sendNotFound(exchange, Endpoint.POST_UPDATE_EPIC);
                } else {
                    sendText(exchange, Endpoint.POST_UPDATE_EPIC,"epic updated success");
                }

            }

            case DELETE_EPIC -> {

                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.DELETE_EPIC, path);
                    return;
                }

                if (manager.removeEpic(id)) {
                    sendText(exchange, Endpoint.DELETE_EPIC, "epic removed success");
                } else {
                    sendServerError(exchange, Endpoint.DELETE_EPIC);
                }

            }

            case GET_ALL_SUBTASKS_BY_EPIC -> {

                String pathId = path.replaceFirst("/epics/", "");
                pathId = pathId.replaceFirst("/subtasks", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.GET_ALL_SUBTASKS_BY_EPIC, path);
                    return;
                }

                Epic epic = manager.getEpic(id);
                if (epic == null) {
                    sendNotFound(exchange, Endpoint.GET_ALL_SUBTASKS_BY_EPIC);
                    return;
                }

                List<Subtask> subtaskList = manager.getAllSubTasksByEpic(id);
                if (subtaskList.isEmpty()) {
                    sendNotFound(exchange, Endpoint.GET_ALL_SUBTASKS_BY_EPIC);
                    return;
                }

                String jsonSubtaskList;
                try {
                    jsonSubtaskList = gson.toJson(subtaskList);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_ALL_SUBTASKS_BY_EPIC);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, Endpoint.GET_ALL_SUBTASKS_BY_EPIC, jsonSubtaskList);

            }

            case UNKNOWN -> sendFormatException(exchange, Endpoint.UNKNOWN, path);
        }
    }
}
