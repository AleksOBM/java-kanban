package manager;

import data.Epic;
import data.Status;
import data.Subtask;
import data.Task;

import java.util.ArrayList;
import java.util.HashMap;

class InMemoryTaskManager implements TaskManager {

    /// Хранилище задач
    private final HashMap<Integer, Task> idToTask = new HashMap<>();

    /// Хранилище эпиков
    private final HashMap<Integer, Epic> idToEpic = new HashMap<>();

    /// Хранилище подзадач
    private final HashMap<Integer, Subtask> idToSubtask = new HashMap<>();

    /// Менеджер истории просмотров
    HistoryManager<? extends Task> historyManager = Managers.getHistoryManager();

    /// Счетчик ID
    int counter = 1;

    /// Генерация ID
    private int generateNewId() {
        return counter++;
    }

    /// Внесение задачи в хранилище по объекту *
    @Override
    public Task setTask(Task newTask) {
        newTask.setId(generateNewId());
        Task savedTask = new Task(newTask.getId(), newTask.getTitle(), newTask.getDescription(), newTask.getStatus());
        idToTask.put(savedTask.getId(), savedTask);

        return newTask;
    }

    /// Внесение эпика в хранилище по объекту *
    @Override
    public Epic setEpic(Epic newEpic) {
        newEpic.setId(generateNewId());
        newEpic.setStatus(Status.NEW);
        Epic savedEpic = new Epic(newEpic.getId(), newEpic.getTitle(), newEpic.getDescription());
        idToEpic.put(savedEpic.getId(), savedEpic);
        savedEpic.setStatus(Status.NEW);

        return newEpic;
    }

    /// Внесение подзадачи в хранилище по объекту *
    @Override
    public Subtask setSubTask(Subtask newSubtask) {
        Integer epicId = newSubtask.getEpicId();
        if (epicId == null || idToEpic.get(epicId) == null) {
            return null;
        }

        Integer subtaskId = generateNewId();
        newSubtask.setId(subtaskId);

        Subtask savedTask = new Subtask(
                newSubtask.getId(),
                newSubtask.getTitle(),
                newSubtask.getDescription(),
                newSubtask.getStatus(),
                newSubtask.getEpicId()
        );

        Epic epic = idToEpic.get(epicId);
        epic.addSubtaskId(subtaskId);

        idToSubtask.put(subtaskId, savedTask);
        updateEpicsStatus(epicId);

        return newSubtask;
    }

    /// Получение задачи по ID *
    @Override
    public Task getTask(int taskID) {
        Task task = idToTask.getOrDefault(taskID, null);
        if (task == null) {
            return null;
        }
        Task newTask = new Task(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
        historyManager.add(newTask);
        return task;
    }

    /// Получение эпика по ID *
    @Override
    public Epic getEpic(int epicID) {
        Epic epic = idToEpic.getOrDefault(epicID, null);
        if (epic == null) {
            return null;
        }
        Epic newEpic = new Epic(
                epic.getId(),
                epic.getTitle(),
                epic.getDescription()
        );
        for (int subtaskId : epic.getSubtaskIds()) {
            newEpic.addSubtaskId(subtaskId);
        }
        historyManager.add(newEpic);
        return epic;
    }

    /// Получение подзадачи по ID *
    @Override
    public Subtask getSubtask(int subTaskID) {
        Subtask subtask = idToSubtask.getOrDefault(subTaskID, null);
        if (subtask == null) {
            return null;
        }
        Subtask newSubtask = new Subtask(
                subtask.getId(),
                subtask.getTitle(),
                subtask.getDescription(),
                subtask.getStatus(),
                subtask.getEpicId()
        );
        historyManager.add(newSubtask);
        return subtask;
    }

    /// Получение списка всех задач в виде объекта *
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(idToTask.values());
    }

    /// Получение списка всех эпиков в виде объекта *
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(idToEpic.values());
    }

    /// Получение списка всех подзадач в виде объекта *
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(idToSubtask.values());
    }

    /// Получение списка всех подзадач эпика в виде объекта *
    @Override
    public ArrayList<Subtask> getAllSubTasksByEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return null;
        }

        Epic epic = idToEpic.get(epicId);
        ArrayList<Subtask> resultSubtasks = new ArrayList<>();

        for (int subtaskId : epic.getSubtaskIds()) {
            if (idToSubtask.containsKey(subtaskId)) {
                Subtask subtask = idToSubtask.get(subtaskId);
                resultSubtasks.add(subtask);
            }
        }

        return resultSubtasks;
    }

    /// Обновление задачи по объекту *
    @Override
    public Task updateTask(Task newTask) {
        Integer id = newTask.getId();
        if (id == null || !(idToTask.containsKey(id))) {
            return null;
        }

        Task oldTask = idToTask.get(id);

        if (newTask.getTitle() != null) {
            oldTask.setTitle(newTask.getTitle());
        } else {
            newTask.setTitle(oldTask.getTitle());
        }

        if (newTask.getDescription() != null) {
            oldTask.setDescription(newTask.getDescription());
        } else {
            newTask.setDescription(oldTask.getDescription());
        }

        if (newTask.getStatus() != null) {
            oldTask.setStatus(newTask.getStatus());
        } else {
            newTask.setStatus(oldTask.getStatus());
        }

        return newTask;
    }

    /// Обновление эпика по объекту *
    @Override
    public Epic updateEpic(Epic newEpic) {
        Integer id = newEpic.getId();
        if (id == null || !(idToEpic.containsKey(id))) {
            return null;
        }
        Epic oldEpic = idToEpic.get(id);

        if (newEpic.getTitle() != null) {
            oldEpic.setTitle(newEpic.getTitle());
        } else {
            newEpic.setTitle(oldEpic.getTitle());
        }

        if (newEpic.getDescription() != null) {
            oldEpic.setDescription(newEpic.getDescription());
        } else {
            newEpic.setDescription(oldEpic.getDescription());
        }

        return oldEpic;
    }

    /// Обновление подзадачи по объекту *
    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Integer id = newSubtask.getId();
        if (id == null || !(idToSubtask.containsKey(id))) {
            return null;
        }
        Subtask oldSubtask = idToSubtask.get(id);

        if (newSubtask.getTitle() != null) {
            oldSubtask.setTitle(newSubtask.getTitle());
        } else {
            newSubtask.setTitle(oldSubtask.getTitle());
        }

        if (newSubtask.getDescription() != null) {
            oldSubtask.setDescription(newSubtask.getDescription());
        } else {
            newSubtask.setDescription(oldSubtask.getDescription());
        }

        if (newSubtask.getStatus() != null) {
            oldSubtask.setStatus(newSubtask.getStatus());
        } else {
            newSubtask.setStatus(oldSubtask.getStatus());
        }

        updateEpicsStatus(oldSubtask.getEpicId());
        return oldSubtask;
    }

    /// Удаление задачи по ID *
    @Override
    public boolean removeTask(int taskId) {
        if (!idToTask.containsKey(taskId)) {
            return false;
        }
        idToTask.remove(taskId);
        return true;
    }

    /// Удаление эпика по ID *
    @Override
    public boolean removeEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return false;
        }
        removeAllSubtasksByEpic(epicId);
        idToEpic.remove(epicId);
        return true;
    }

    /// Удаление подзадачи по ID
    @Override
    public boolean removeSubtask(int subtaskId) {
        if (!idToSubtask.containsKey(subtaskId)) {
            return false;
        }
        Subtask subtask = idToSubtask.get(subtaskId);
        int epicId = subtask.getEpicId();
        Epic epic = idToEpic.get(epicId);
        idToSubtask.remove(subtaskId);
        epic.removeSubtaskId(subtaskId);
        updateEpicsStatus(epicId);
        return true;
    }

    /// Удаление всех подзадач эпика по ID эпика
    @Override
    public void removeAllSubtasksByEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return;
        }
        Epic epic = idToEpic.get(epicId);
        for (int subtaskId : epic.getSubtaskIds()) {
            idToSubtask.remove(subtaskId);
        }
        epic.removeAllSubTaskIds();
        updateEpicsStatus(epicId);
    }

    /// Удаление всех задач *
    @Override
    public void removeAllTasks() {
        idToTask.clear();
    }

    /// Удаление всех эпиков *
    @Override
    public void removeAllEpics() {
        idToEpic.clear();
        idToSubtask.clear();
    }

    /// Удаление всех подзадач *
    @Override
    public void removeAllSubTasks() {
        idToSubtask.clear();
        for (Epic epic : idToEpic.values()) {
            epic.removeAllSubTaskIds();
            updateEpicsStatus(epic.getId());
        }
    }

    ///  Получить последние 10 задач
    @Override
    public ArrayList<? extends Task> getHistory() {
        return historyManager.getHistory();
    }

    /// Обновление статуса эпика
    private void updateEpicsStatus(int epicId) {
        Epic epic = idToEpic.get(epicId);
        if (epic.getStatus() == null) {
            return;
        }

        int statusNullCount = 0;
        ArrayList<Status> epicStatuses = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = idToSubtask.get(subtaskId);
            if (subtask.getEpicId() == epicId && subtask.getStatus() != null) {
                epicStatuses.add(subtask.getStatus());
            } else if (subtask.getEpicId() == epicId && subtask.getStatus() == null) {
                statusNullCount++;
                epicStatuses.add(Status.NEW);
            }
        }

        if (epicStatuses.isEmpty() || statusNullCount == epicStatuses.size()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int statusNewCount = 0;
        int statusDoneCount = 0;
        for (Status status : epicStatuses) {
            switch (status) {
                case Status.NEW -> statusNewCount++;
                case Status.DONE -> statusDoneCount++;
            }
        }

        if (statusNewCount == epicStatuses.size()) {
            epic.setStatus(Status.NEW);
        } else if (statusDoneCount == epicStatuses.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
