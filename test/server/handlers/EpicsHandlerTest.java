package server.handlers;

import data.*;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import server.tokens.TaskListTypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicsHandlerTest extends HttpTaskServerTest {

    @Test
    void GET_EPIC() {
        shouldReturnStatus200AndBodyOfEpic3AfterGetEpic3();
        shouldReturnStatus404AndBodyOfNotFoundAfterGetEpic1();
    }

    @Test
    void GET_ALL_EPICS() {
        shouldReturnStatus200AndListOfTwoElementsAfterGetAllEpics();
    }

    @Test
    void POST_NEW_EPIC() {
        shouldReturnStatus200AndBodyOfSuccessAddingAfterAddNewEpic();
        shouldReturnStatus400AndBodyOfBadRequestAfterTryAddBrokenEpic();
    }

    @Test
    void POST_UPDATE_EPIC() {
        shouldReturnStatus200AndBodyOfSuccessUpdatingAfterUpdateEpic();
        shouldReturnStatus422AndBodyOfUnsupportedAfterTryUpdateEpic();
        shouldReturnStatus404AndBodyOfNotFoundAfterTryUpdateLostEpic();

    }

    @Test
    void DELETE_EPIC() {
        shouldReturnStatus200AndBodyOfSuccessRemovingAfterDeleteEpic();
    }

    @Test
    void GET_ALL_SUBTASKS_BY_EPIC() {
        shouldReturnStatus200AndListOfTwoElementsAfterGetAllSubtasksByEpic();
    }


    // ---------------------------------HOOD----------------------------------------


    /// Создает новый эпик, запрашивает его и сверяет поля
    private void shouldReturnStatus200AndBodyOfEpic3AfterGetEpic3() {
        manager.addEpic(new Epic(null, "FIRST EPIC", "first epic"));

        HttpResponse<String> response = responseOfNewGetRequest("epics/3");

        assertEquals(200, response.statusCode());

        Epic requestEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals("FIRST EPIC", requestEpic.getTitle());
        assertEquals("first epic", requestEpic.getDescription());
        assertEquals(Status.NEW, requestEpic.getStatus());
    }

    /// Запрашивает эпик, но в id указан id задачи
    private void shouldReturnStatus404AndBodyOfNotFoundAfterGetEpic1() {
        HttpResponse<String> response = responseOfNewGetRequest("epics/1");

        assertEquals(404, response.statusCode());
        assertEquals("\tGET_EPIC 404 not found", response.body());
    }

    /// Создает два эпика, запрашивает все эпики, проверяет код ответа и количество
    private void shouldReturnStatus200AndListOfTwoElementsAfterGetAllEpics() {
        manager.addEpic(new Epic(null, "FIRST EPIC", "first epic"));
        manager.addEpic(new Epic(null, "SECOND EPIC", "second epic"));

        HttpResponse<String> response = responseOfNewGetRequest("epics");

        assertEquals(200, response.statusCode());

        List<Task> requestList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(2, requestList.size());
    }

    /// Постит новый эпик, проверяет код и тело ответа
    private void shouldReturnStatus200AndBodyOfSuccessAddingAfterAddNewEpic() {
        Epic epic = new Epic(null, "ADDED EPIC", "added epic");

        HttpResponse<String> response = responseOfNewPostRequest("epics", epic);

        assertEquals(200, response.statusCode());
        assertEquals("epic adding success, epicId=3", response.body());
    }

    /// Создает новый эпик, постит обновление этого эпика, проверяет статус и тело ответа
    private void shouldReturnStatus200AndBodyOfSuccessUpdatingAfterUpdateEpic() {
        manager.addEpic(new Epic(null, "FIRST EPIC", "first epic"));

        Epic newEpic = new Epic(3, "FIRST EPIC UPDATED", null);

        HttpResponse<String> response = responseOfNewPostRequest("epics/3", newEpic);
        assertEquals(200, response.statusCode());

        assertEquals("epic updated success", response.body());

        Epic requestTask = (Epic) manager.getWithoutHistory(3);
        assertEquals("FIRST EPIC UPDATED", requestTask.getTitle());
        assertEquals("first epic", requestTask.getDescription());

    }

    /// Пытается обновить эпик, но в теле эпика не указан ID
    private void shouldReturnStatus422AndBodyOfUnsupportedAfterTryUpdateEpic() {
        Epic epic = new Epic(null, "TASK WITHOUT ID", null);

        HttpResponse<String> response = responseOfNewPostRequest("epics/3", epic);

        assertEquals(422, response.statusCode());
        assertEquals("\t" + Endpoint.POST_UPDATE_EPIC + " 422 unprocessable entity", response.body());
    }

    /// Пытается обновить эпик с несуществующим id
    private void shouldReturnStatus404AndBodyOfNotFoundAfterTryUpdateLostEpic() {
        Epic epic = new Epic(4, "TASK WITH LOST ID", null);

        HttpResponse<String> response = responseOfNewPostRequest("epics/4", epic);

        assertEquals(404, response.statusCode());
        assertEquals("\t" + Endpoint.POST_UPDATE_EPIC + " 404 not found", response.body());
    }

    /// Удаляет эпик и проверяет ответ от сервера
    private void shouldReturnStatus200AndBodyOfSuccessRemovingAfterDeleteEpic() {
        manager.addEpic(new Epic(null, "FIRST EPIC", "first epic"));

        HttpResponse<String> response = responseOfNewDeleteRequest("epics/3");

        assertEquals(200, response.statusCode());
        assertEquals("epic removed success", response.body());
    }

    /**
     * Создает эпик с двумя подзадачами, запрашивает все подзадачи этого эпика,
     * проверяет статус ответа и количество подзадач в ответе
     */
    private void shouldReturnStatus200AndListOfTwoElementsAfterGetAllSubtasksByEpic() {
        manager.addEpic(new Epic(null, "FIRST EPIC", "first epic"));
        manager.addSubtask(new Subtask(null, "FIRST SUB", "first sub",
                null, 3, null, null));
        manager.addSubtask(new Subtask(null, "SECOND SUB", "second sub",
                null, 3, null, null));

        HttpResponse<String> response = responseOfNewGetRequest("epics/3/subtasks");

        assertEquals(200, response.statusCode());

        List<Subtask> requestList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(2, requestList.size());
    }

    /// Пытается добавить новый эпик, но отправляет сломанный json в теле запроса
    private void shouldReturnStatus400AndBodyOfBadRequestAfterTryAddBrokenEpic() {
        String brokenEpic = """
                {
                       "id" : "THIS IS FAIL",
                       "title" : "BROKEN EPIC",
                       "description" : "broken epic",
                       "status" : null,
                       "startTime" : null,
                       "duration" : 0
                     }""";

        URI uri = URI.create(host + "epics");

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(brokenEpic))
                    .uri(uri)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            response = client.send(request, handler);

        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        assertEquals(400, response.statusCode());
        assertEquals("\t" + Endpoint.POST_NEW_EPIC + " 400 bad request body", response.body());
    }
}
