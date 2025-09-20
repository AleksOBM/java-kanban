package manager;

import data.Task;

import java.util.List;

public interface HistoryManager<T> {

    List<T> getHistory();

    void add(Task taskObject);
}
