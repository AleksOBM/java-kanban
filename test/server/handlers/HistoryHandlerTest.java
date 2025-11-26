package server.handlers;

import data.Task;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import server.tokens.TaskListTypeToken;

import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest extends HttpTaskServerTest {

    @Test
    void GET_HISTORY() {
        shouldReturnStatus200AndBodyOfHistoryTasks();
    }

    /// Добавляет задачу в историю, запрашивает список истории и проверяет количество
    private void shouldReturnStatus200AndBodyOfHistoryTasks() {
        manager.getTask(1);

        HttpResponse<String> response = responseOfNewGetRequest("history");
        List<Task> requestList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(1, requestList.size());
    }
}
