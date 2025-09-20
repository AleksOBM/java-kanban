package manager;

import data.Epic;
import data.Subtask;
import data.Task;

import java.util.ArrayList;

public interface HistoryManager<T> {

    ArrayList<T> getHistory();

    void add(Task task);

    void add(Epic epic);

    void add(Subtask subtask);

}
