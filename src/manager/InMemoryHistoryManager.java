package manager;

import data.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    /// История просмотров
    ArrayList<Task> history = new ArrayList<>(10);

    @Override
    public void add(Task task) {

    }

    @Override
    public ArrayList<Task> getHistory() {
        // TODO
        return history;
    }
}
