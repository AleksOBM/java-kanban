package server.handlers;

import data.Endpoint;
import data.Epic;
import data.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import server.tokens.TaskListTypeToken;

import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtasksHandlerTest extends HttpTaskServerTest {

    @BeforeEach
    void subtaskBeforeEach() {
        manager.addEpic(new Epic(3, "FIRST EPIC", "first epic"));
        manager.addSubtask(new Subtask(4, "FIRST SUB", "first sub",
                null, 3, null, null));
        manager.addSubtask(new Subtask(5, "SECOND SUB", "second sub",
                null, 3, null, null));
    }

    @Test
    void GET_SUBTASK() {
        shouldReturnStatus200AndBodyOfSubtask4AfterGetSubtask4();
    }

    @Test
    void GET_ALL_SUBTASKS() {
        shouldReturnStatus200AndListOfTwoElementsAfterGetAllSubtasks();
    }

    @Test
    void POST_NEW_SUBTASK() {
        shouldReturnStatus200AndBodyOfSuccessAddingAfterAddNewSubtask();
        shouldReturnStatus422AndBodyOfUnsupportedAfterTryAddSubtaskWithoutEpicId();
    }

    @Test
    void POST_UPDATE_SUBTASK() {
        shouldReturnStatus200AndBodyOfSuccessUpdatingAfterUpdateSubtask();
        shouldReturnStatus422AndBodyOfUnsupportedAfterTryUpdateSubtaskWithoutId();
        shouldReturnStatus404AndBodyOfNotFoundAfterTryUpdateLostSubtask();
    }

    @Test
    void DELETE_SUBTASK() {
        shouldReturnStatus200AndBodyOfSuccessRemovingAfterDeleteSubtask();
    }


    // ---------------------------------HOOD----------------------------------------


    /// Получает задачу из базы и сверяет все поля с оригиналом
    private void shouldReturnStatus200AndBodyOfSubtask4AfterGetSubtask4() {
        HttpResponse<String> response = responseOfNewGetRequest("subtasks/4");

        assertEquals(200, response.statusCode());

        Subtask requestSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals("FIRST SUB", requestSubtask.getTitle());
        assertEquals("first sub", requestSubtask.getDescription());
    }

    /// Получает список всех подзадач и проверяет их количество
    private void shouldReturnStatus200AndListOfTwoElementsAfterGetAllSubtasks() {
        HttpResponse<String> response = responseOfNewGetRequest("subtasks");

        assertEquals(200, response.statusCode());

        List<Subtask> requestList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(2, requestList.size());
    }

    /// Добавляет новую подзадачу и проверяет ответ сервера
    private void shouldReturnStatus200AndBodyOfSuccessAddingAfterAddNewSubtask() {
        Subtask subtask = new Subtask(null, "ADDED SUB", "added SUB",
                null, 3, null, null);

        HttpResponse<String> response = responseOfNewPostRequest("subtasks", subtask);

        assertEquals(200, response.statusCode());
        assertEquals("subtask adding success, subtaskId=6", response.body());
    }

    /// Пытается добавить подзадачу, но в теле подзадачи не указан ID эпика
    private void shouldReturnStatus422AndBodyOfUnsupportedAfterTryAddSubtaskWithoutEpicId() {
        Subtask subtask = new Subtask(null, "TASK WITHOUT EPIC-ID",
                null, null, null, null, null);

        HttpResponse<String> response = responseOfNewPostRequest("subtasks", subtask);

        assertEquals(422, response.statusCode());
        assertEquals("\t" + Endpoint.POST_NEW_SUBTASK + " 422 unprocessable entity", response.body());
    }

    /// Обновляет существующую подзадачу и проверяет ответ от сервера
    private void shouldReturnStatus200AndBodyOfSuccessUpdatingAfterUpdateSubtask() {
        Subtask newSubtask = new Subtask(5, "SECOND SUB UPDATED",
                null, null, null, null, null);

        HttpResponse<String> response = responseOfNewPostRequest("subtasks/5", newSubtask);
        assertEquals(200, response.statusCode());

        assertEquals("subtask updated success", response.body());

        Subtask requestTask = (Subtask) manager.getWithoutHistory(5);
        assertEquals("SECOND SUB UPDATED", requestTask.getTitle());
        assertEquals("second sub", requestTask.getDescription());
    }

    /// Пытается обновить подзадачу, но в теле подзадачи не указан ID подзадачи
    private void shouldReturnStatus422AndBodyOfUnsupportedAfterTryUpdateSubtaskWithoutId() {
        Subtask subtask = new Subtask(null, "TASK WITHOUT ID",
                null, null, 3, null, null);

        HttpResponse<String> response = responseOfNewPostRequest("tasks/1", subtask);

        assertEquals(422, response.statusCode());
        assertEquals("\t" + Endpoint.POST_UPDATE_TASK + " 422 unprocessable entity", response.body());
    }

    /// Пытается обновить подзадачу с несуществующим id
    private void shouldReturnStatus404AndBodyOfNotFoundAfterTryUpdateLostSubtask() {
        Subtask subtask = new Subtask(7, "TASK WITH LOST ID",
                null, null, null, null, null);

        HttpResponse<String> response = responseOfNewPostRequest("subtasks/7", subtask);

        assertEquals(404, response.statusCode());
        assertEquals("\t" + Endpoint.POST_UPDATE_SUBTASK + " 404 not found", response.body());
    }

    /// Удаляет подзадачу и проверяет ответ от сервера
    private void shouldReturnStatus200AndBodyOfSuccessRemovingAfterDeleteSubtask() {
        HttpResponse<String> response = responseOfNewDeleteRequest("subtasks/4");

        assertEquals(200, response.statusCode());
        assertEquals("subtask removed success", response.body());
    }

}
