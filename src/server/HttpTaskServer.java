package server;

import com.sun.net.httpserver.HttpServer;
import data.Property;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

import manager.TaskManagerType;
import server.handlers.*;

public class HttpTaskServer implements Property {

    private static HttpServer httpServer;
    private static TaskManager manager;

    HttpTaskServer(TaskManager manager) throws IOException {
        HttpTaskServer.manager = manager;
        httpServer = HttpServer.create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getTaskManager(TaskManagerType.FILE_BACKED));
        server.start();
    }

    public void start() throws IOException {
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.createContext("/", new BaseHttpHandler(manager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на http://localhost:" + PORT + '/');
    }

    public void stop() {
        httpServer.stop(1);
    }

}
