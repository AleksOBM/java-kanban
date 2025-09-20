package manager;

import data.Epic;
import data.Subtask;
import data.Task;

import java.util.ArrayList;

public interface TaskManager {

    Task setTask(Task newTask);

    Epic setEpic(Epic newEpic);

    Subtask setSubTask(Subtask newSubtask);

    Task getTask(int taskID);

    Epic getEpic(int epicID);

    Subtask getSubtask(int subTaskID);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getAllSubTasksByEpic(int epicId);

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

    ArrayList<? extends Task> getHistory();
}
