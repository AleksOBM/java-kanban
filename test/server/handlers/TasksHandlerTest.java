package server.handlers;

import data.Endpoint;
import data.Status;
import data.Task;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import server.tokens.TaskListTypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TasksHandlerTest extends HttpTaskServerTest {

    @Test
    void BASE() {
        shouldReturnStatus405AndBodyOfFormatExceptionAfterGetTaskWithStringId();
    }

    @Test
    void GET_TASK() {
        shouldReturnStatus200AndBodyOfTask1AfterGetTask1();
        shouldReturnStatus404AndBodyOfNotFoundAfterGetTask3();
        shouldReturnStatus405AndBodyOfFormatExceptionAfterGetTaskWithLongId();
        shouldReturnStatus406AndBodyOfTimeOverlapsAfterTryAddNewIntersectionTask();
    }

    @Test
    void GET_ALL_TASK() {
        shouldReturnStatus200AndListOfTwoElementsAfterGetAllTasks();
    }

    @Test
    void POST_NEW_TASK() {
        shouldReturnStatus200AndBodyOfSuccessAddingAfterAddNewTask();
        shouldReturnStatus400AndBodyOfBadRequestAfterTryAddingBrokenTask();
    }

    @Test
    void POST_UPDATE_TASK() {
        shouldReturnStatus200AndBodyOfSuccessUpdatingAfterUpdateTask();
        shouldReturnStatus422AndBodyOfUnsupportedAfterTryUpdateTask();
        shouldReturnStatus404AndBodyOfNotFoundAfterTryUpdateLostTask();
        shouldReturnStatus422AndBodyOfUnsupportedAfterTryUpdateWrongTask();
        shouldReturnStatus406AndBodyOfOverlapsAfterTryUpdateIntersectionTask();
        shouldReturnStatus200AndBodyOfTask2AfterTryGetHistory();
    }

    @Test
    void DELETE_TASK() {
        shouldReturnStatus200AndBodyOfSuccessRemovingAfterDeleteTask();
    }


    // ---------------------------------HOOD----------------------------------------


    /// Пытается получить задачу, но вместо id указана буква
    private void shouldReturnStatus405AndBodyOfFormatExceptionAfterGetTaskWithStringId() {
        HttpResponse<String> response = responseOfNewGetRequest("tasks/l");

        assertEquals(405, response.statusCode());
        assertEquals("\tUNKNOWN 405 path format exception: /tasks/l", response.body());
    }

    /// Пытается получить задачу, но указан слишком большой id
    private void shouldReturnStatus405AndBodyOfFormatExceptionAfterGetTaskWithLongId() {
        HttpResponse<String> response = responseOfNewGetRequest("tasks/9999999999");

        assertEquals(405, response.statusCode());
        assertEquals("\tGET_TASK 405 path format exception: /tasks/9999999999", response.body());
    }

    /// Пытается получить задачу, но задачи с таким id нет в базе
    private void shouldReturnStatus404AndBodyOfNotFoundAfterGetTask3() {
        HttpResponse<String> response = responseOfNewGetRequest("tasks/3");

        assertEquals(404, response.statusCode());
        assertEquals("\tGET_TASK 404 not found", response.body());
    }

    /// Пытается добавить задачу, но она пересекается по срокам с существующей
    private void shouldReturnStatus406AndBodyOfTimeOverlapsAfterTryAddNewIntersectionTask() {
        manager.updateTask(new Task(2, null, null, null,
                LocalDateTime.parse("2025-11-20 08:00", DATE_TIME_FORMATTER),
                Duration.ofMinutes(30)
        ));

        Task newTask = new Task(null, "NEW TASK", "new task", null,
                LocalDateTime.parse("2025-11-20 08:20", DATE_TIME_FORMATTER),
                Duration.ofMinutes(30)
        );

        HttpResponse<String> response = responseOfNewPostRequest("tasks", newTask);

        assertEquals(406, response.statusCode());
        assertEquals("\tPOST_NEW_TASK 406 time overlaps: 2025-11-20 08:20 - 2025-11-20 08:50",
                response.body()
        );
    }

    /// Получает задачу из базы и сверяет все поля с оригиналом
    private void shouldReturnStatus200AndBodyOfTask1AfterGetTask1() {
        HttpResponse<String> response = responseOfNewGetRequest("tasks/1");

        assertEquals(200, response.statusCode());

        Task requestTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task1.getTitle(), requestTask.getTitle());
        assertEquals(task1.getDescription(), requestTask.getDescription());
        assertEquals(task1.getStatus(), requestTask.getStatus());
        assertEquals(task1.getStartTime(), requestTask.getStartTime());
        assertEquals(task1.getDuration(), requestTask.getDuration());
    }

    /// Получает список всех задач и проверяет их количество
    private void shouldReturnStatus200AndListOfTwoElementsAfterGetAllTasks() {
        HttpResponse<String> response = responseOfNewGetRequest("tasks");

        assertEquals(200, response.statusCode());

        List<Task> requestList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(2, requestList.size());
    }

    /// Добавляет новую задачу и проверяет ответ сервера
    private void shouldReturnStatus200AndBodyOfSuccessAddingAfterAddNewTask() {
        Task task3 = new Task(null, "ADDED TASK", "added task");

        HttpResponse<String> response = responseOfNewPostRequest("tasks", task3);

        assertEquals(200, response.statusCode());
        assertEquals("task adding success, taskId=3", response.body());
    }

    /// Пытается добавить новую задачу через неправильно заполненный json
    private void shouldReturnStatus400AndBodyOfBadRequestAfterTryAddingBrokenTask() {
        String brokenString = """
                {
                  "id": null,
                  "title": "BROKEN TASK",
                  "description": "broken task",
                  "status": null,
                  "startTime": "THIS IS FAIL",
                  "duration": 32
                }""";

        URI uri = URI.create(host + "tasks");

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(brokenString))
                    .uri(uri)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            response = client.send(request, handler);

        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        assertEquals(400, response.statusCode());
        assertEquals("\t" + Endpoint.POST_NEW_TASK + " 400 bad request body", response.body());
    }

    /**
     * Обновляет существующую задачу и проверяет ответ от сервера,
     * затем получает ее через менеджер и сверяет все поля
     */
    private void shouldReturnStatus200AndBodyOfSuccessUpdatingAfterUpdateTask() {
        Task task3 = new Task(
                2,
                "UPDATED TASK",
                null,
                Status.IN_PROGRESS,
                LocalDateTime.parse("2025-11-20 10:00", DATE_TIME_FORMATTER),
                Duration.ofMinutes(15)
        );

        HttpResponse<String> response = responseOfNewPostRequest("tasks/2", task3);
        assertEquals(200, response.statusCode());

        assertEquals("task updated success", response.body());

        Task requestTask = manager.getWithoutHistory(2);
        assertEquals("UPDATED TASK", requestTask.getTitle());
        assertEquals("second task", requestTask.getDescription());
        assertEquals(Status.IN_PROGRESS, requestTask.getStatus());
        assertEquals(LocalDateTime.parse("2025-11-20 10:00", DATE_TIME_FORMATTER), requestTask.getStartTime());
        assertEquals(LocalDateTime.parse("2025-11-20 10:15", DATE_TIME_FORMATTER), requestTask.getEndTime());
    }

    /// Пытается обновить задачу, но в теле задачи не указан ID
    private void shouldReturnStatus422AndBodyOfUnsupportedAfterTryUpdateTask() {
        Task task = new Task(null, "TASK WITHOUT ID", null);

        HttpResponse<String> response = responseOfNewPostRequest("tasks/1", task);

        assertEquals(422, response.statusCode());
        assertEquals("\t" + Endpoint.POST_UPDATE_TASK + " 422 unprocessable entity", response.body());
    }

    /// Пытается обновить задачу с несуществующим id
    private void shouldReturnStatus404AndBodyOfNotFoundAfterTryUpdateLostTask() {
        Task task = new Task(3, "TASK WITH LOST ID", null);

        HttpResponse<String> response = responseOfNewPostRequest("tasks/3", task);

        assertEquals(404, response.statusCode());
        assertEquals("\t" + Endpoint.POST_UPDATE_TASK + " 404 not found", response.body());
    }

    /// Пытается обновить задачу, но в эндпоинте и теле указаны разные существующие id
    private void shouldReturnStatus422AndBodyOfUnsupportedAfterTryUpdateWrongTask() {
        Task task = new Task(2, "TASK WITH LOST ID", null);

        HttpResponse<String> response = responseOfNewPostRequest("tasks/1", task);

        assertEquals(422, response.statusCode());
        assertEquals("\t" + Endpoint.POST_UPDATE_TASK + " 422 unprocessable entity", response.body());
    }

    /// Пытается обновить задачу, но время пересекается с уже существующей
    private void shouldReturnStatus406AndBodyOfOverlapsAfterTryUpdateIntersectionTask() {
        Task task = new Task(
                1,
                "UPDATED TASK",
                null,
                Status.IN_PROGRESS,
                LocalDateTime.parse("2025-11-20 10:10", DATE_TIME_FORMATTER),
                Duration.ofMinutes(10)
        );

        HttpResponse<String> response = responseOfNewPostRequest("tasks/1", task);
        assertEquals(406, response.statusCode());

        assertEquals("\tPOST_UPDATE_TASK 406 time overlaps: 2025-11-20 10:10 - 2025-11-20 10:20",
                response.body());
    }

    /// Проверяет историю после множества действий по обновлению задач
    private void shouldReturnStatus200AndBodyOfTask2AfterTryGetHistory() {
        HttpResponse<String> response = responseOfNewGetRequest("history");
        assertEquals(404, response.statusCode());
    }

    /// Удаляет задачу и проверяет ответ от сервера
    private void shouldReturnStatus200AndBodyOfSuccessRemovingAfterDeleteTask() {
        HttpResponse<String> response = responseOfNewDeleteRequest("tasks/1");

        assertEquals(200, response.statusCode());
        assertEquals("task removed success", response.body());
    }
}
