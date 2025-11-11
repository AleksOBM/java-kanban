package manager;

import data.Epic;
import data.Subtask;
import data.Task;
import data.Type;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    Task addTask(Task newTask);

    Epic addEpic(Epic newEpic);

    Subtask addSubtask(Subtask newSubtask);

    Task getTask(int taskID);

    Epic getEpic(int epicID);

    Subtask getSubtask(int subTaskID);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getAllSubTasksByEpic(int epicId);

    Task updateTask(Task newTask);

    Epic updateEpic(Epic newEpic);

    Subtask updateSubtask(Subtask newSubtask);

    boolean removeTask(int taskId);

    boolean removeEpic(int epicId);

    boolean removeSubtask(int subtaskId);

    void removeAllSubtasksByEpic(int epicId);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    List<Task> getHistory();

    TreeSet<Task> getAll();

    Task get(int id);

    Task getWithoutHistory(int id);

    Type getType(int id);
}
