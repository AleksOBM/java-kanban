package manager;

import data.*;
import exception.ManagerSaveException;

import java.util.*;

class InMemoryTaskManager implements TaskManager {

    /// Хранилище задач
    private final Map<Integer, Task> idToTask = new HashMap<>();

    /// Хранилище эпиков
    private final Map<Integer, Epic> idToEpic = new HashMap<>();

    /// Хранилище подзадач
    private final Map<Integer, Subtask> idToSubtask = new HashMap<>();

    /// Менеджер истории просмотров
    private final HistoryManager historyManager = Managers.getHistoryManager();

    /// Счетчик ID
    protected int counter = 1;

    /// Генерация ID
    private int generateNewId() {
        return counter++;
    }

    @Override
    public Task get(int id) {
        if (idToTask.containsKey(id)) {
            Task newTask = copyOfTask(id);
            historyManager.add(newTask);
            return newTask;
        } else if (idToEpic.containsKey(id)) {
            Epic newEpic = copyOfEpic(id);
            historyManager.add(newEpic);
            return newEpic;
        } else if (idToSubtask.containsKey(id)) {
            Subtask newSubtask = copyOfSubtask(id);
            historyManager.add(newSubtask);
            return newSubtask;
        }
        try {
            throw new ManagerSaveException("Попытка получения несуществующей таски");
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Task getWithoutHistory(int id) {
        if (idToTask.containsKey(id)) {
            return copyOfTask(id);
        } else if (idToEpic.containsKey(id)) {
            return copyOfEpic(id);
        } else if (idToSubtask.containsKey(id)) {
            return copyOfSubtask(id);
        }
        try {
            throw new ManagerSaveException("Попытка получения несуществующей таски");
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Type getType(int id) {
        Task taskObject = getWithoutHistory(id);
        if (taskObject == null) {
            return null;
        }
        return taskObject.getType();
    }

    public List<Task> getAll() {
        if (counter == 1) {
            return new ArrayList<>();
        }
        TreeSet<Task> allTasks = new TreeSet<>();
        if (!idToTask.isEmpty()) {
            allTasks.addAll(getAllTasks());
        }
        if (!idToEpic.isEmpty()) {
            allTasks.addAll(getAllEpics());
        }
        if (!idToSubtask.isEmpty()) {
            allTasks.addAll(getAllSubtasks());
        }

        return new ArrayList<>(allTasks);
    }

    /// Внесение задачи в хранилище по объекту
    @Override
    public Task setTask(Task newTask) {
        newTask.setId(generateNewId());
        Task savedTask = newTask.getCopy();
        idToTask.put(savedTask.getId(), savedTask);

        return newTask;
    }

    /// Внесение эпика в хранилище по объекту
    @Override
    public Epic setEpic(Epic newEpic) {
        newEpic.setId(generateNewId());
        newEpic.setStatus(Status.NEW);
        Epic savedEpic = new Epic(newEpic.getId(), newEpic.getTitle(), newEpic.getDescription());
        idToEpic.put(savedEpic.getId(), savedEpic);
        savedEpic.setStatus(Status.NEW);

        return newEpic;
    }

    /// Внесение подзадачи в хранилище по объекту
    @Override
    public Subtask setSubtask(Subtask newSubtask) {
        Integer epicId = newSubtask.getEpicId();
        if (epicId == null || idToEpic.get(epicId) == null) {
            try {
                throw new ManagerSaveException("Попытка добавить подзадачу в несуществующий эпик - не удалась.");
            } catch (ManagerSaveException e) {
                System.out.println(e.getMessage());
            }
        }

        Integer subtaskId = generateNewId();
        newSubtask.setId(subtaskId);

        Subtask savedTask = newSubtask.getCopy();

        Epic epic = idToEpic.get(epicId);
        epic.addSubtaskId(subtaskId);

        idToSubtask.put(subtaskId, savedTask);
        updateEpicsStatus(epic.getId());

        return newSubtask;
    }

    /// Получение задачи по ID
    @Override
    public Task getTask(int taskID) {
        Task newTask = copyOfTask(taskID);
        if (newTask == null) {
            return null;
        }
        historyManager.add(newTask);
        return newTask;
    }

    /// Получение эпика по ID
    @Override
    public Epic getEpic(int epicID) {
        Epic newEpic = copyOfEpic(epicID);
        if (newEpic == null) {
            return null;
        }
        historyManager.add(newEpic);
        return newEpic;
    }

    /// Получение подзадачи по ID
    @Override
    public Subtask getSubtask(int subTaskID) {
        Subtask newSubtask = copyOfSubtask(subTaskID);
        if (newSubtask == null) {
            return null;
        }
        historyManager.add(newSubtask);
        return newSubtask;
    }

    /// Получение копии задачи по ID
    private Task copyOfTask(int taskId) {
        if (!idToTask.containsKey(taskId)) {
            return null;
        }
        Task task = idToTask.get(taskId);
        return task.getCopy();
    }

    /// Получение копии эпика по ID
    private Epic copyOfEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return null;
        }
        Epic epic = idToEpic.get(epicId);
        Epic newEpic = epic.getCopy();
        for (int subtaskId : epic.getSubtaskIds()) {
            newEpic.addSubtaskId(subtaskId);
        }
        return newEpic;
    }

    /// Получение копии подзадачи по ID
    private Subtask copyOfSubtask(int subtaskId) {
        if (!idToSubtask.containsKey(subtaskId)) {
            return null;
        }
        Subtask subtask = idToSubtask.get(subtaskId);
        return subtask.getCopy();
    }

    /// Получение списка всех задач в виде объекта
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(idToTask.values());
    }

    /// Получение списка всех эпиков в виде объекта
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(idToEpic.values());
    }

    /// Получение списка всех подзадач в виде объекта
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(idToSubtask.values());
    }

    /// Получение списка всех подзадач эпика в виде объекта
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

    /// Обновление задачи по объекту
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

    /// Обновление эпика по объекту
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

    /// Обновление подзадачи по объекту
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

    /// Удаление задачи по ID
    @Override
    public boolean removeTask(int taskId) {
        if (!idToTask.containsKey(taskId)) {
            return false;
        }
        idToTask.remove(taskId);
        historyManager.remove(taskId);
        return true;
    }

    /// Удаление эпика по ID
    @Override
    public boolean removeEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return false;
        }
        removeAllSubtasksByEpic(epicId);
        idToEpic.remove(epicId);
        historyManager.remove(epicId);
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
        historyManager.remove(subtaskId);
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
            historyManager.remove(subtaskId);
        }
        epic.removeAllSubtaskIds();
        updateEpicsStatus(epicId);
    }

    /// Удаление всех задач
    @Override
    public void removeAllTasks() {
        for (int taskId : idToTask.keySet()) {
            historyManager.remove(taskId);
        }
        idToTask.clear();
    }

    /// Удаление всех эпиков
    @Override
    public void removeAllEpics() {
        for (int epicId : idToEpic.keySet()) {
            historyManager.remove(epicId);
        }
        idToEpic.clear();

        for (int subtaskId : idToSubtask.keySet()) {
            historyManager.remove(subtaskId);
        }
        idToSubtask.clear();
    }

    /// Удаление всех подзадач
    @Override
    public void removeAllSubTasks() {
        for (int subtaskId : idToSubtask.keySet()) {
            historyManager.remove(subtaskId);
        }
        idToSubtask.clear();

        for (Epic epic : idToEpic.values()) {
            epic.removeAllSubtaskIds();
            updateEpicsStatus(epic.getId());
        }
    }

    ///  Получить список с историей
    @Override
    public List<Task> getHistory() {
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
