import data.*;

import java.util.ArrayList;
import java.util.HashMap;

class TaskManager {

    /// Хранилище задач
    private HashMap<Integer, Task> idToTask = new HashMap<>();

    /// Хранилище эпиков
    private HashMap<Integer, Epic> idToEpic = new HashMap<>();

    /// Хранилище подзадач
    private HashMap<Integer, Subtask> idToSubtask = new HashMap<>();

    /// Счетчик ID
    int counter = 1;

    /// Генерация ID
    private int nextId() {
        return counter++;
    }

    /// Внесение задачи в хранилище по объекту *
    public Task setTask(Task newTask) {
        newTask.setId(nextId());
        newTask.setStatus(Status.NEW);
        idToTask.put(newTask.getId(), newTask);
        return newTask;
    }

    /// Внесение эпика в хранилище по объекту *
    public Epic setEpic(Epic newEpic) {
        newEpic.setId(nextId());
        newEpic.setStatus(Status.NEW);
        idToEpic.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    /// Внесение подзадачи в хранилище по объекту *
    public Subtask setSubTask(Subtask subtask) {
        int subtaskId = nextId();
        int epicId = subtask.getEpicId();
        Epic epic = idToEpic.get(epicId);
        subtask.setId(subtaskId);
        subtask.setStatus(Status.NEW);
        subtask.setEpicId(epicId);
        epic.addSubtaskId(subtaskId);
        idToSubtask.put(subtaskId, subtask);
        updateEpicsStatus(epicId);
        return subtask;
    }

    /// Получение задачи по ID *
    public Task getTask(int taskID) {
        return idToTask.getOrDefault(taskID, null);
    }

    /// Получение эпика по ID *
    public Epic getEpic(int epicID) {
        return idToEpic.getOrDefault(epicID, null);
    }

    /// Получение подзадачи по ID *
    public Subtask getSubTask(int subTaskID) {
        return idToSubtask.getOrDefault(subTaskID, null);
    }

    /// Получение списка всех задач в виде объекта *
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(idToTask.values());
        return tasks;
    }

    /// Получение списка всех эпиков в виде объекта *
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        epics.addAll(idToEpic.values());
        return epics;
    }

    /// Получение списка всех подзадач в виде объекта *
    public ArrayList<Subtask> getAllSubTasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.addAll(idToSubtask.values());
        return subtasks;
    }

    /// Получение списка всех подзадач эпика в виде объекта *
    public ArrayList<Subtask> getAllSubTasks(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return null;
        }
        Epic epic = idToEpic.get(epicId);
        ArrayList<Subtask> thisSubtasks = new ArrayList<>();
        for (int subtaskId : idToSubtask.keySet()) {
            for (int thisSubtaskId : epic.getSubtaskIds()) {
                if (subtaskId == thisSubtaskId) {
                    thisSubtasks.add(idToSubtask.get(thisSubtaskId));
                }
            }
        }
        return thisSubtasks;
    }

    /// Обновление задачи по объекту *
    public Task updateTask(Task task) {
        int id = task.getId();
        if (task.getId() == null || !(idToTask.containsKey(id))) {
            return null;
        }
        Task oldTask = idToTask.get(id);
        Task newTask = new Task(id, null, null, null);

        if (task.getTitle() == null) {
            newTask.setTitle(oldTask.getTitle());
        } else if (task.getTitle().isEmpty()) {
            newTask.setTitle(null);
        } else {
            newTask.setTitle(task.getTitle());
        }

        if (task.getDescription() == null) {
            newTask.setDescription(oldTask.getDescription());
        } else if (task.getDescription().isEmpty()) {
            newTask.setDescription(null);
        } else {
            newTask.setDescription(task.getDescription());
        }

        if (task.getStatus() == null) {
            newTask.setStatus(oldTask.getStatus());
        } else  {
            newTask.setStatus(task.getStatus());
        }

        idToTask.remove(id);
        idToTask.put(id, newTask);
        return newTask;
    }

    /// Обновление эпика по объекту *
    public Epic updateEpic(Epic epic) {
        int id = epic.getId();
        if (epic.getId() == null || !(idToEpic.containsKey(id))) {
            return null;
        }
        Epic oldEpic = idToEpic.get(id);
        Epic newEpic = new Epic(id, null, null);
        newEpic.setStatus(Status.NEW);

        if (epic.getTitle() == null) {
            newEpic.setTitle(oldEpic.getTitle());
        } else if (epic.getTitle().isEmpty()) {
            newEpic.setTitle(null);
        } else {
            newEpic.setTitle(epic.getTitle());
        }

        if (epic.getDescription() == null) {
            newEpic.setDescription(oldEpic.getDescription());
        } else if (epic.getDescription().isEmpty()) {
            newEpic.setDescription(null);
        } else {
            newEpic.setDescription(epic.getDescription());
        }

        idToEpic.remove(id);
        idToEpic.put(id, newEpic);
        updateEpicsStatus(newEpic.getId());
        return newEpic;
    }

    /// Обновление подзадачи по объекту *
    public Subtask updateSubtask(Subtask subtask) {
        Integer id = subtask.getId();
        if (id == null || !(idToSubtask.containsKey(id))) {
            return null;
        }
        Subtask oldSubtask = idToSubtask.get(id);
        Subtask newSubtask = new Subtask(id, null, null, null, null);

        if (subtask.getTitle() == null) {
            newSubtask.setTitle(oldSubtask.getTitle());
        } else if (subtask.getTitle().isEmpty()) {
            newSubtask.setTitle(null);
        } else {
            newSubtask.setTitle(subtask.getTitle());
        }

        if (subtask.getDescription() == null) {
            newSubtask.setDescription(oldSubtask.getDescription());
        } else if (subtask.getDescription().isEmpty()) {
            newSubtask.setDescription(null);
        } else {
            newSubtask.setDescription(subtask.getDescription());
        }

        if (subtask.getStatus() == null) {
            newSubtask.setStatus(oldSubtask.getStatus());
        } else  {
            newSubtask.setStatus(subtask.getStatus());
        }

        Integer newEpicId = subtask.getEpicId();
        Epic newEpic = idToEpic.get(newEpicId);
        if (newEpicId == null) {
            newSubtask.setEpicId(oldSubtask.getEpicId());
        } else {
            newSubtask.setEpicId(newEpicId);
            newEpic.addSubtaskId(id);
        }

        idToSubtask.remove(id);
        idToSubtask.put(id, newSubtask);
        updateEpicsStatus(newSubtask.getEpicId());
        return newSubtask;
    }

    /// Удаление задачи по ID *
    public String removeTask(int taskId) {
        if (!idToTask.containsKey(taskId)) {
            return null;
        }
        Task task = idToTask.get(taskId);
        String removedTask = task.toString();
        idToTask.remove(taskId);
        return "removed" + removedTask;
    }

    /// Удаление эпика по ID *
    public String removeEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return null;
        }
        Epic epic = idToEpic.get(epicId);
        String removedEpic = epic.toString();
        idToEpic.remove(epicId);
        return "removed" + removedEpic;
    }

    /// Удаление подзадачи по ID
    public String removeSubtask(int subtaskId) {
        if (!idToSubtask.containsKey(subtaskId)) {
            return null;
        }
        Subtask subtask = idToSubtask.get(subtaskId);
        int epicId = subtask.getEpicId();
        Epic epic = idToEpic.get(epicId);
        epic.removeSubtaskId(subtaskId);
        String removedSubtask = subtask.toString();
        idToSubtask.remove(subtaskId);
        return "removed" + removedSubtask;
    }

    /// Удаление всех подзадач эпика по ID эпика
    public void removeAllSubTasks(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return;
        }
        Epic epic = idToEpic.get(epicId);
        epic.removeAllSubTaskIds();
        for (int subtaskId : epic.getSubtaskIds()) {
            idToSubtask.remove(subtaskId);
        }
    }

    /// Удаление всех задач *
    public void removeAllTasks() {
        idToTask.clear();
    }

    /// Удаление всех эпиков *
    public void removeAllEpics() {
        idToEpic.clear();
    }

    /// Удаление всех подзадач *
    public void removeAllSubTasks() {
        idToSubtask.clear();
    }

    /// Обновление статуса эпика
    private void updateEpicsStatus(int epicId) {
        Epic epic = idToEpic.get(epicId);
        if (epic.getStatus() == null) {
            return;
        }

        ArrayList<Status> thisStatuses = new ArrayList<>();
        for (Subtask subtask : idToSubtask.values()) {
            if (subtask.getEpicId() == epicId) {
                thisStatuses.add(subtask.getStatus());
            }
        }

        if (thisStatuses.isEmpty()) {
            return;
        }

        int statusNewCount = 0;
        int statusInProgressCount = 0;
        int statusDoneCount = 0;
        for (Status status : thisStatuses) {
            switch (status) {
                case Status.NEW -> statusNewCount++;
                case Status.IN_PROGRESS -> statusInProgressCount++;
                case Status.DONE -> statusDoneCount++;
            }
        }

        if (statusNewCount == thisStatuses.size()) {
            epic.setStatus(Status.NEW);
        } else if (statusDoneCount == thisStatuses.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
