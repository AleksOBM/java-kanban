package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import data.Endpoint;
import data.Task;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        Endpoint endpoint = getEndpoint(method, path);

        switch (endpoint) {

            case GET_TASK -> {

                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.GET_TASK, path);
                    return;
                }

                Task task = manager.getTask(id);
                if (task == null) {
                    sendNotFound(exchange, Endpoint.GET_TASK);
                    return;
                }

                String jsonTask;
                try {
                    jsonTask = gson.toJson(task);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_TASK);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, Endpoint.GET_TASK, jsonTask);

            }

            case GET_ALL_TASKS -> {

                List<Task> taskList = manager.getAllTasks();
                if (taskList.isEmpty()) {
                    sendNotFound(exchange, Endpoint.GET_ALL_TASKS);
                    return;
                }

                String jsonTaskList;
                try {
                    jsonTaskList = gson.toJson(taskList);
                } catch (Exception exception) {
                    sendServerError(exchange, Endpoint.GET_ALL_TASKS);
                    System.out.println(exception.getMessage());
                    return;
                }
                sendText(exchange, Endpoint.GET_ALL_TASKS, jsonTaskList);

            }

            case POST_NEW_TASK -> {

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Task task;
                try {
                    task = gson.fromJson(body, Task.class);
                } catch (Exception e) {
                    sendServerError(exchange, Endpoint.POST_NEW_TASK);
                    System.out.println(e.getMessage());
                    return;
                }

                if (manager.addTask(task) == null) {
                    sendHasOverlaps(exchange, Endpoint.POST_NEW_TASK, task);
                } else {
                    sendText(exchange, Endpoint.POST_NEW_TASK,"task adding success");
                }

            }

            case POST_UPDATE_TASK -> {

                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.POST_UPDATE_TASK, path);
                    return;
                }

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Task task;
                try {
                    task = gson.fromJson(body, Task.class);
                } catch (Exception e) {
                    sendServerError(exchange, Endpoint.POST_UPDATE_TASK);
                    System.out.println(e.getMessage());
                    return;
                }

                if (manager.updateTask(task) == null) {
                    sendNotFound(exchange, Endpoint.POST_UPDATE_TASK);
                } else {
                    sendText(exchange, Endpoint.POST_UPDATE_TASK,"task updated success");
                }

            }

            case DELETE_TASK -> {

                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, Endpoint.DELETE_TASK, path);
                    return;
                }

                if (manager.removeTask(id)) {
                    sendText(exchange, Endpoint.DELETE_TASK, "task removed success");
                } else {
                    sendServerError(exchange, Endpoint.DELETE_TASK);
                }

            }

            case UNKNOWN -> sendFormatException(exchange, Endpoint.UNKNOWN, path);
        }


    }

}
