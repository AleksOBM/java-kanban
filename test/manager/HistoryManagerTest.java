package manager;

import data.Task;
import data.Epic;
import data.Subtask;
import data.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    static HistoryManager historyManager;
    static List<Task> history;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Hello", "World", Status.DONE);
        historyManager.add(task);
        historyManager.add(new Epic(2, "Hello", "World"));
        historyManager.add(new Subtask(3, "Hello", "World", Status.DONE, 2));
        historyManager.add(new Task(1, "Goodbye", "World", Status.IN_PROGRESS));
        history = historyManager.getHistory();

        historyManager.remove(4);
    }

    @Test
    void shouldReturnEmptyListWhenHistoryIsEmpty() {
        HistoryManager hm = new InMemoryHistoryManager();
        hm.add(new Subtask(1, null, null, null, 2));
        hm.remove(1);
        assertEquals("[]", hm.getHistory().toString(),
                "Пустая история не выдает пустой лист");
    }

    @Test
    void shouldReturnNotNullWhenGetHistory() {
        assertNotNull(history, "История не возвращается.");
    }

    @Test
    void shouldReturnHistorySizeEqualsThree() {
        assertEquals(3, history.size(), "История содержит неверное количество элементов.");
    }

    @Test
    void shouldReturnLastElementWithIdThree() {
        historyManager.remove(1);
        int elementId = historyManager.getHistory().getLast().getId();
        assertEquals(3, elementId,
                "Неверная перелинковка нодов при удалении");
    }

    @Test
    void shouldWorkInLessThanOneTenthOfMillisecond() {

        for (int i = 4; i <= 10_000; i++) {
            historyManager.add(new Task(i, null, null));
        }

        int historySize = historyManager.getHistory().size();
        int idToRemove = 5039;
        final long startTime = System.nanoTime();
        historyManager.remove(idToRemove);
        final long endTime = System.nanoTime();

        final long resultTime = endTime - startTime;

        System.out.println("Удаление " + idToRemove + "-го элемента из " + historySize
                + " элементов за " + resultTime +  " мкс.");

        assertTrue(resultTime <= 100_000,
                "Удаление одного элемента из истории с 10_000 элементов занимает больше 0,1 миллисекунд"
                        + "\n this test not working with coverage"
                );
    }
}
