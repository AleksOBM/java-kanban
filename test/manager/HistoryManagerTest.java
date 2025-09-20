package manager;

import data.Task;
import data.Epic;
import data.Subtask;
import data.Status;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest<T> {

    @Test
    void add() {
        HistoryManager<T> historyManager = new InMemoryHistoryManager<>();

        Task task = new Task(1, "Hello", "World", Status.DONE);

        historyManager.add(task);

        historyManager.add(new Epic(2, "Hello", "World"));

        historyManager.add(new Subtask(3, "Hello", "World", Status.DONE, 2));

        ArrayList<T> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(3, history.size(), "История содержит неверное количество элементов.");

        for (int i = 4; i <= 11; i++) {
            historyManager.add(new Task(i, null, null));
        }
        history = historyManager.getHistory();

        assertEquals(10, history.size(),
                "История имеет неправильное ограничение на максимальное количество элементов");

        assertNotSame(history.getFirst(), task,
                "История не удаляет самый старый элемент при добавлении одиннадцатого элемента");
    }
}
