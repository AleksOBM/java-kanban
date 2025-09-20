package manager;

import data.Epic;
import data.Subtask;
import data.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T> implements HistoryManager<T> {

    private final int maxSize = 10;
    private final List<T> history = new ArrayList<>(maxSize);

    @Override
    public ArrayList<T> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        ArrayList<Task> taskInList = new ArrayList<>();
        taskInList.add(task);
        if (history.size() == maxSize) {
            history.removeFirst();
        }
        history.addAll((ArrayList<? extends T>) taskInList);
    }

    @Override
    public void add(Epic epic) {
        ArrayList<Epic> epicInList = new ArrayList<>();
        epicInList.add(epic);
        if (history.size() == maxSize) {
            history.removeFirst();
        }
        history.addAll((ArrayList<? extends T>) epicInList);
    }

    @Override
    public void add(Subtask subtask) {
        ArrayList<Subtask> subtaskInList = new ArrayList<>();
        subtaskInList.add(subtask);
        if (history.size() == maxSize) {
            history.removeFirst();
        }
        history.addAll((ArrayList<? extends T>) subtaskInList);
    }
}
