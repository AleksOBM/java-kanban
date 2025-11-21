package server;

import com.google.gson.Gson;
import data.Property;
import data.Task;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import manager.TaskManagerType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HttpTaskServerTest implements Property {
    protected TaskManager manager;
    protected HttpTaskServer taskServer;
    protected Gson gson;
    protected final String host = "http://localhost:" + PORT + '/';

    protected Task task1;
    protected Task task2;

    @BeforeEach
    void beforeEach() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = Managers.getGson();
        taskServer.start();

        task1 = new Task(null, "FIRST TASK", "first task");
        task2 = new Task(null, "SECOND TASK", "second task");
        manager.addTask(task1);
        manager.addTask(task2);
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
    }

    /// Отправляет GET-запрос и возвращает ответ сервера
    protected HttpResponse<String> responseOfNewGetRequest(String stringUri) {
        URI uri = URI.create(host + stringUri);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            return client.send(request, handler);

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /// Отправляет POST-запрос и возвращает ответ сервера
    protected  <T> HttpResponse<String> responseOfNewPostRequest(String stringUri, T object) {
        URI uri = URI.create(host + stringUri);

        try (HttpClient client = HttpClient.newHttpClient()) {
            String objectToJson = gson.toJson(object);

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(objectToJson))
                    .uri(uri)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            return client.send(request, handler);

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /// Отправляет DELETE-запрос и возвращает ответ сервера
    protected HttpResponse<String> responseOfNewDeleteRequest(String stringUri) {
        URI uri = URI.create(host + stringUri);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            return client.send(request, handler);

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
