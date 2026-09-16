package ru.practicum.kanban.manager;

import ru.practicum.kanban.data.Task;

import java.util.List;

public interface HistoryManager {

	void add(Task task);

	void remove(int id);

	List<Task> getHistory();
}
