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
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                Task task = manager.getTask(id);
                if (task == null) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                String jsonTask;
                try {
                    jsonTask = gson.toJson(task);
                } catch (Exception exception) {
                    sendServerError(exchange, endpoint);
                    System.out.println(exception.getMessage());
                    return;
                }

                sendText(exchange, endpoint, jsonTask);

            }

            case GET_ALL_TASKS -> {

                List<Task> taskList = manager.getAllTasks();
                if (taskList.isEmpty()) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                String jsonTaskList;
                try {
                    jsonTaskList = gson.toJson(taskList);
                } catch (Exception exception) {
                    sendServerError(exchange, endpoint);
                    System.out.println(exception.getMessage());
                    return;
                }
                sendText(exchange, endpoint, jsonTaskList);

            }

            case POST_NEW_TASK -> {

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Task task;
                try {
                    task = gson.fromJson(body, Task.class);
                } catch (Exception e) {
                    sendBadRequestBoby(exchange, endpoint);
                    System.out.println(e.getMessage());
                    return;
                }

                Task newTask = manager.addTask(task);

                if (newTask == null) {
                    sendHasOverlaps(exchange, endpoint, task);
                } else {
                    sendText(exchange, endpoint,"task adding success, taskId=" + newTask.getId());
                }

            }

            case POST_UPDATE_TASK -> {

                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                InputStream bodyInputStream = exchange.getRequestBody();
                String body = new String(bodyInputStream.readAllBytes(), CHARSET);

                Task task;
                try {
                    task = gson.fromJson(body, Task.class);
                } catch (Exception e) {
                    sendBadRequestBoby(exchange, endpoint);
                    System.out.println(e.getMessage());
                    return;
                }

                Integer taskId = task.getId();
                if (taskId == null || id != taskId) {
                    sendUnprocessableEntity(exchange, endpoint);
                    return;
                } else if (manager.getWithoutHistory(taskId) == null) {
                    sendNotFound(exchange, endpoint);
                    return;
                }

                if (manager.updateTask(task) == null) {
                    sendHasOverlaps(exchange, endpoint, task);
                } else {
                    sendText(exchange, endpoint,"task updated success");
                }

            }

            case DELETE_TASK -> {

                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathId(pathId);
                if (id == -1) {
                    sendFormatException(exchange, endpoint, path);
                    return;
                }

                if (manager.removeTask(id)) {
                    sendText(exchange, endpoint, "task removed success");
                } else {
                    sendNotFound(exchange, endpoint);
                }

            }

            case UNKNOWN -> sendFormatException(exchange, endpoint, path);
        }


    }

}
