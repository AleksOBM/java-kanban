package server.handlers;

import data.Task;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import server.tokens.TaskListTypeToken;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest extends HttpTaskServerTest {

    @Test
    void GET_PRIORITIZED() {
        shouldReturnStatus200AndBodyOfPrioritizedTasks();
    }

    /// Приоритизирует задачи, запрашивает их список и проверяет их количество
    private void shouldReturnStatus200AndBodyOfPrioritizedTasks() {
        manager.updateTask(new Task(1, null, null, null,
                LocalDateTime.parse("2025-11-20 20:00", DATE_TIME_FORMATTER),
                Duration.ofMinutes(30)
                ));

        manager.updateTask(new Task(2, null, null, null,
                LocalDateTime.parse("2025-11-20 20:30", DATE_TIME_FORMATTER),
                Duration.ofMinutes(30)
        ));

        HttpResponse<String> response = responseOfNewGetRequest("prioritized");
        List<Task> requestList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(2, requestList.size());
    }
}
