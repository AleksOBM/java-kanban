package manager;

import data.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T> implements HistoryManager<T> {

    private final int maxSize = 10;
    private final List<Task> history = new ArrayList<>(maxSize);

    @Override
    public List<T> getHistory() {
        return new ArrayList<>((ArrayList<? extends T>) history);
    }

    @Override
    public void add(Task taskObject) {
        if (history.size() == maxSize) {
            history.removeFirst();
        }
        history.add(taskObject);
    }
}
