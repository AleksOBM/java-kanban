package manager;

import data.Epic;
import data.Subtask;
import data.Task;
import data.Type;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    /// Внесение задачи в хранилище по объекту
    Task addTask(Task newTask);

    /// Внесение эпика в хранилище по объекту
    Epic addEpic(Epic newEpic);

    /// Внесение подзадачи в хранилище по объекту
    Subtask addSubtask(Subtask newSubtask);

    /// Получение задачи по ID с сохранением в историю
    Task getTask(int taskID);

    /// Получение эпика по ID с сохранением в историю
    Epic getEpic(int epicID);

    /// Получение подзадачи по ID с сохранением в историю
    Subtask getSubtask(int subTaskID);

    /// Получение списка всех задач в виде объекта
    List<Task> getAllTasks();

    /// Получение списка всех эпиков в виде объекта
    List<Epic> getAllEpics();

    /// Получение списка всех подзадач в виде объекта
    List<Subtask> getAllSubtasks();

    /// Получение списка всех подзадач эпика в виде объекта
    List<Subtask> getAllSubTasksByEpic(int epicId);

    List<Task> getPrioritizedTasks();

    /// Обновление задачи по объекту, в случае неудачи возвращает null
    Task updateTask(Task newTask);

    /// Обновление эпика по объекту, в случае неудачи возвращает null
    Epic updateEpic(Epic newEpic);

    /// Обновление подзадачи по объекту, в случае неудачи возвращает null
    Subtask updateSubtask(Subtask newSubtask);

    /// Удаление задачи по ID
    boolean removeTask(int taskId);

    /// Удаление эпика по ID
    boolean removeEpic(int epicId);

    /// Удаление подзадачи по ID
    boolean removeSubtask(int subtaskId);

    /// Удаление всех подзадач эпика по ID эпика
    void removeAllSubtasksByEpic(int epicId);

    /// Удаление всех задач
    void removeAllTasks();

    /// Удаление всех эпиков
    void removeAllEpics();

    /// Удаление всех подзадач
    void removeAllSubTasks();

    ///  Получить список с историей
    List<Task> getHistory();

    /// Получить все объекты из всех хранилищ
    TreeSet<Task> getAll();

    /// Получить объект по id с сохранением в историю
    Task get(int id);

    /// Получить объект по id без сохранения в историю
    Task getWithoutHistory(int id);

    /// Получить тип объекта по id
    Type getType(int id);
}
