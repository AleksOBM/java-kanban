package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.Endpoint;
import data.Property;
import data.Task;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class BaseHttpHandler implements HttpHandler, Property {

    protected final TaskManager manager;
    protected static Gson gson;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        gson = Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        Endpoint endpoint = getEndpoint(method, path);

        switch (endpoint) {
            case BASE -> {
                   String text =
                            "\tWelcome to java server.\n" +
                            "\tUse next endpoints:\n" +
                            "\thttp://localhost:" + PORT + "/tasks\n" +
                            "\thttp://localhost:" + PORT + "/subtasks\n" +
                            "\thttp://localhost:" + PORT + "/epics\n" +
                            "\thttp://localhost:" + PORT + "/history\n" +
                            "\thttp://localhost:" + PORT + ":/prioritized";

                   sendText(exchange, Endpoint.BASE, text);
            }

            case UNKNOWN -> sendFormatException(exchange, Endpoint.UNKNOWN, path);
        }
    }

    /// Для отправки общего ответа в случае успеха
    protected void sendText(HttpExchange exchange, Endpoint endpoint, String text) throws IOException {
        byte[] resp = text.getBytes(CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println("\t" + endpoint + " 200 success");
        exchange.close();
    }

    /// Для отправки ответа в случае, если объект не был найден
    protected void sendNotFound(HttpExchange exchange, Endpoint endpoint) throws IOException {
        String text = ("\t" + endpoint + " 404 not found");
        byte[] resp = text.getBytes(CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println(text);
        exchange.close();
    }

    /// Для отправки ответа в случае, если объект не был найден
    protected void sendFormatException(HttpExchange exchange, Endpoint endpoint, String path) throws IOException {
        String text = ("\t" + endpoint + " 405 path format exception: " + path);
        byte[] resp = text.getBytes(CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(405, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println(text);
        exchange.close();
    }

    /// Для отправки ответа, если при создании или обновлении задача пересекается с уже существующими
    protected void sendHasOverlaps(HttpExchange exchange, Endpoint endpoint, Task taskObject) throws IOException {
        String text = ("\t" + endpoint + " 406 time overlaps: " +
                taskObject.getStartTime().format(DATE_TIME_FORMATTER) + " - " +
                taskObject.getEndTime().format(DATE_TIME_FORMATTER));

        byte[] resp = text.getBytes(CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println(text);
        exchange.close();
    }

    /// Для отправки ответа, если произошла ошибка на сервере
    protected void sendServerError(HttpExchange exchange, Endpoint endpoint) throws IOException {
        String text = ("\t" + endpoint + " 500 Internal Server Error");
        byte[] resp = text.getBytes(CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(500, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println(text);
        exchange.close();
    }

    protected static int parsePathId(String pathId) {
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    protected Endpoint getEndpoint(String method, String path) {

        switch (method) {
            case "GET": {
                if (Pattern.matches("^/$", path)) {
                    return Endpoint.BASE;
                }

                if (Pattern.matches("^/tasks/\\d+$", path)) {
                    return Endpoint.GET_TASK;
                } else if (Pattern.matches("^/tasks$", path)) {
                    return Endpoint.GET_ALL_TASKS;
                } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                    return Endpoint.GET_SUBTASK;
                } else if (Pattern.matches("^/subtasks$", path)) {
                    return Endpoint.GET_ALL_SUBTASKS;
                } else if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                    return Endpoint.GET_ALL_SUBTASKS_BY_EPIC;
                } else if (Pattern.matches("^/epics/\\d+$", path)) {
                    return Endpoint.GET_EPIC;
                } else if (Pattern.matches("^/epics$", path)) {
                    return Endpoint.GET_ALL_EPICS;
                } else if (Pattern.matches("^/history$", path)) {
                    return Endpoint.GET_HISTORY;
                } else if (Pattern.matches("^/prioritized$", path)) {
                    return Endpoint.GET_PRIORITIZED;
                }
                break;
            }
            case "POST": {
                if (Pattern.matches("^/tasks/\\d+$", path)) {
                    return Endpoint.POST_UPDATE_TASK;
                } else if (Pattern.matches("^/tasks$", path)) {
                    return Endpoint.POST_NEW_TASK;
                } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                    return Endpoint.POST_UPDATE_SUBTASK;
                } else if (Pattern.matches("^/subtasks$", path)) {
                    return Endpoint.POST_NEW_SUBTASK;
                } else if (Pattern.matches("^/epics/\\d+$", path)) {
                    return Endpoint.POST_UPDATE_EPIC;
                } else if (Pattern.matches("^/epics$", path)) {
                    return Endpoint.POST_NEW_EPIC;
                }
                break;
            }
            case "DELETE": {
                if (Pattern.matches("^/tasks/\\d+$", path)) {
                    return Endpoint.DELETE_TASK;
                } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                    return Endpoint.DELETE_SUBTASK;
                } else if (Pattern.matches("^/epics/\\d+$", path)) {
                    return Endpoint.DELETE_EPIC;
                }
                break;
            }
            default: {
                return Endpoint.UNKNOWN;
            }
        }

        return Endpoint.UNKNOWN;
    }

}
