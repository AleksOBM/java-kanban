package manager;

import data.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {

    private static InMemoryTaskManager instance;

    /// Хранилище задач
    protected final Map<Integer, Task> idToTask = new HashMap<>();

    /// Хранилище эпиков
    protected final Map<Integer, Epic> idToEpic = new HashMap<>();

    /// Хранилище подзадач
    protected final Map<Integer, Subtask> idToSubtask = new HashMap<>();

    /// Менеджер истории просмотров
    protected final HistoryManager historyManager = Managers.getHistoryManager();

    /// Хранилище всех тасок в порядке приоритета
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    /// Счетчик ID
    protected int counter = 1;

    /// Генерация ID
    private int generateNewId() {
        return counter++;
    }

    /// Конструктор
    public InMemoryTaskManager() {
    }

    /// Получить синглтон класса
    public static InMemoryTaskManager getInstance() {
        if (instance == null) {
            instance = new InMemoryTaskManager();
        }
        return instance;
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
        return null;
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
        return null;
    }

    @Override
    public Type getType(int id) {
        Task taskObject = getWithoutHistory(id);
        if (taskObject == null) {
            return null;
        }
        return taskObject.getType();
    }

    public TreeSet<Task> getAll() {
        if (counter == 1) {
            return new TreeSet<>();
        }

        List<? extends Task> all = Stream.of(getAllTasks(), getAllEpics(), getAllSubtasks())
                .flatMap(Collection::stream)
                .toList();

        return new TreeSet<>(all);
    }

    @Override
    public Task addTask(Task newTask) {
        if (newTask.getType() != Type.TASK) {
            System.out.println("Ошибка добавления новой задачи - неверные входные данные.");
            return null;
        }

        if (!findIntersections(newTask).isEmpty()) {
            System.out.println("Ошибка добавления новой задачи - задача пересекается по срокам с уже существующими.");
            return null;
        }
        newTask.setId(generateNewId());

        if (newTask.getDuration() == null) {
            newTask.setDuration(Duration.ZERO);
        }

        Task savedTask = newTask.getCopy();
        idToTask.put(savedTask.getId(), savedTask);
        if (savedTask.getStartTime() != null) {
            prioritizedTasks.add(savedTask);
        }

        return newTask;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        if (newEpic.getType() != Type.EPIC) {
            System.out.println("Ошибка добавления нового эпика - неверные входные данные.");
            return null;
        }

        newEpic.setId(generateNewId());
        newEpic.setStatus(Status.NEW);

        if (newEpic.getDuration() == null) {
            newEpic.setDuration(Duration.ZERO);
        }

        Epic savedEpic = newEpic.getCopy();
        idToEpic.put(savedEpic.getId(), savedEpic);

        return newEpic;
    }

    @Override
    public Subtask addSubtask(Subtask newSubtask) {
        Integer epicId = newSubtask.getEpicId();
        if (epicId == null || idToEpic.get(epicId) == null || newSubtask.getType() != Type.SUBTASK) {
            System.out.println("Ошибка добавления новой подзадачи - неверные входные данные.");
            return null;
        }

        if (!findIntersections(newSubtask).isEmpty()) {
            System.out.println("Ошибка добавления новой подзадачи - подзадача пересекается по срокам с уже существующими.");
            return null;
        }

        Integer subtaskId = generateNewId();
        newSubtask.setId(subtaskId);

        if (newSubtask.getDuration() == null) {
            newSubtask.setDuration(Duration.ZERO);
        }

        Subtask savedSubtask = newSubtask.getCopy();

        Epic epic = idToEpic.get(epicId);
        epic.addSubtaskId(subtaskId);
        idToSubtask.put(subtaskId, savedSubtask);
        updateEpicTime(epic);
        updateEpicsStatus(epic.getId());
        if (savedSubtask.getStartTime() != null) {
            prioritizedTasks.add(savedSubtask);
        }

        return newSubtask;
    }

    @Override
    public Task getTask(int taskID) {
        Task newTask = copyOfTask(taskID);
        if (newTask == null) {
            return null;
        }
        historyManager.add(newTask);
        return newTask;
    }

    @Override
    public Epic getEpic(int epicID) {
        Epic newEpic = copyOfEpic(epicID);
        if (newEpic == null) {
            return null;
        }
        historyManager.add(newEpic);
        return newEpic;
    }

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
        return idToTask.get(taskId).getCopy();
    }

    /// Получение копии эпика по ID
    private Epic copyOfEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return null;
        }
        return idToEpic.get(epicId).getCopy();
    }

    /// Получение копии подзадачи по ID
    private Subtask copyOfSubtask(int subtaskId) {
        if (!idToSubtask.containsKey(subtaskId)) {
            return null;
        }
        return idToSubtask.get(subtaskId).getCopy();
    }

    @Override
    public List<Task> getAllTasks() {
        return idToTask.values().stream().sorted(Task::compareTo).toList();
    }


    @Override
    public List<Epic> getAllEpics() {
        return idToEpic.values().stream().sorted(Task::compareTo).toList();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return idToSubtask.values().stream().sorted(Task::compareTo).toList();
    }

    @Override
    public List<Subtask> getAllSubTasksByEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return null;
        }

        return idToSubtask.values().stream()
                .filter(subtask -> idToEpic.get(epicId).getSubtaskIds().contains(subtask.getId()))
                .toList();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task updateTask(Task newTask) {
        Integer id = newTask.getId();
        if (id == null || !(idToTask.containsKey(id))) {
            System.out.println("Ошибка обновления задачи - неверные входные данные.");
            return null;
        }

        Task oldTask = idToTask.get(id);

        Task testTask = newTask.getCopy();

        if (testTask.getDuration() == null) {
            testTask.setDuration(oldTask.getDuration());
        }

        if (testTask.getStartTime() == null) {
            testTask.setStartTime(oldTask.getStartTime());
        }

        if (testTask.getEndTime() != null && !findIntersections(testTask).isEmpty()) {
            System.out.println("Ошибка обновления задачи - это время уже занято.");
            return null;
        }


        if (newTask.getTitle() != null) {
            oldTask.setTitle(newTask.getTitle());
        }

        if (newTask.getDescription() != null) {
            oldTask.setDescription(newTask.getDescription());
        }

        if (newTask.getStatus() != null) {
            oldTask.setStatus(newTask.getStatus());
        }

        if (newTask.getDuration() != null) {
            oldTask.setDuration(newTask.getDuration());
        }

        if (newTask.getStartTime() != null) {
            oldTask.setStartTime(newTask.getStartTime());
            prioritizedTasks.add(oldTask);
        }

        newTask = oldTask.getCopy();

        return newTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Integer id = newEpic.getId();
        if (id == null || !(idToEpic.containsKey(id))) {
            System.out.println("Ошибка обновления эпика - неверные входные данные.");
            return null;
        }

        Epic oldEpic = idToEpic.get(id);

        if (newEpic.getTitle() != null) {
            oldEpic.setTitle(newEpic.getTitle());
        }

        if (newEpic.getDescription() != null) {
            oldEpic.setDescription(newEpic.getDescription());
        }

        newEpic = oldEpic.getCopy();

        return newEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Integer subtaskId = newSubtask.getId();
        if (subtaskId == null || !(idToSubtask.containsKey(subtaskId))) {
            System.out.println("Ошибка обновления подзадачи - неверные входные данные.");
            return null;
        }

        Subtask oldSubtask = idToSubtask.get(subtaskId);

        Subtask testSubtask = newSubtask.getCopy();

        if (testSubtask.getDuration() == null) {
            testSubtask.setDuration(oldSubtask.getDuration());
        }

        if (testSubtask.getStartTime() == null) {
            testSubtask.setStartTime(oldSubtask.getStartTime());
        }

        if (testSubtask.getStartTime() != null && !findIntersections(testSubtask).isEmpty()) {
            System.out.println("Ошибка обновления подзадачи - это время уже занято.");
            return null;
        }

        if (newSubtask.getTitle() != null) {
            oldSubtask.setTitle(newSubtask.getTitle());
        }

        if (newSubtask.getDescription() != null) {
            oldSubtask.setDescription(newSubtask.getDescription());
        }

        if (newSubtask.getStatus() != null) {
            oldSubtask.setStatus(newSubtask.getStatus());
        }

        if (newSubtask.getDuration() != null) {
            oldSubtask.setDuration(newSubtask.getDuration());
        }

        if (newSubtask.getStartTime() != null) {
            oldSubtask.setStartTime(newSubtask.getStartTime());
            prioritizedTasks.add(oldSubtask);
        }

        newSubtask = oldSubtask.getCopy();

        int epicId = oldSubtask.getEpicId();
        Epic epic = idToEpic.get(epicId);
        epic.addSubtaskId(subtaskId);
        updateEpicsStatus(epicId);
        updateEpicTime(epic);

        return newSubtask;
    }

    /// Обновление продолжительности эпика
    protected void updateEpicTime(Epic epic) {
        if (epic.getId() == null) {
            return;
        }

        clearEpicTime(epic);

        List<Subtask> subtasks = getAllSubTasksByEpic(epic.getId());

        if (subtasks == null) {
            return;
        }

        List<Subtask> subtasksWithDateTimeUnits = subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null || subtask.getDuration() != null)
                .toList();

        if (subtasksWithDateTimeUnits.isEmpty()) {
            return;
        }

        Optional<Duration> sumDuration = Optional.ofNullable(subtasksWithDateTimeUnits.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus));

        epic.setDuration(sumDuration.orElse(Duration.ZERO));

        List<Subtask> prioritizedSubtasks = subtasksWithDateTimeUnits.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .toList();

        if (prioritizedSubtasks.isEmpty()) {
            return;
        }

        if (prioritizedSubtasks.size() == 1) {
            LocalDateTime time = subtasksWithDateTimeUnits.getFirst().getStartTime();
            epic.setStartTime(time);
            epic.setEndTime(time);
            epic.setDuration(sumDuration.orElse(Duration.ZERO));
            return;
        }

        Subtask firstSubtask = prioritizedSubtasks.stream()
                .min(Comparator.comparing(Subtask::getStartTime))
                .get();

        Subtask lastSubtask = prioritizedSubtasks.stream()
                .max(Comparator.comparing(Subtask::getStartTime))
                .get();

        epic.setStartTime(firstSubtask.getStartTime());
        epic.setEndTime(lastSubtask.getEndTime());
    }

    /// Обновление статуса эпика
    protected void updateEpicsStatus(int epicId) {
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

    /// Обнуление полей времени эпика
    protected void clearEpicTime(Epic epic) {
        epic.setStartTime(null);
        epic.setEndTime(null);
        epic.setDuration(Duration.ZERO);
    }

    ///  Поиск пересекающихся тасок
    public List<Task> findIntersections(Task taskObject) {
        if (prioritizedTasks.isEmpty() || taskObject.getStartTime() == null) {
            return new ArrayList<>();
        }

        return prioritizedTasks.stream()
                .filter(task -> !Objects.equals(task.getId(), taskObject.getId()))
                .filter(task ->
                        (taskObject.getStartTime().isBefore(task.getEndTime()) &&
                                task.getStartTime().isBefore(taskObject.getEndTime()))
                )
                .toList();
    }

    @Override
    public boolean removeTask(int taskId) {
        if (!idToTask.containsKey(taskId)) {
            return false;
        }
        prioritizedTasks.removeIf(s -> s.getId() == taskId);
        idToTask.remove(taskId);
        historyManager.remove(taskId);
        return true;
    }

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

    @Override
    public boolean removeSubtask(int subtaskId) {
        if (!idToSubtask.containsKey(subtaskId)) {
            return false;
        }
        Subtask subtask = idToSubtask.get(subtaskId);
        int epicId = subtask.getEpicId();
        Epic epic = idToEpic.get(epicId);
        prioritizedTasks.removeIf(s -> s.getId() == subtaskId);
        idToSubtask.remove(subtaskId);
        epic.removeSubtaskId(subtaskId);
        updateEpicsStatus(epicId);
        updateEpicTime(epic);
        historyManager.remove(subtaskId);
        return true;
    }

    @Override
    public void removeAllSubtasksByEpic(int epicId) {
        if (!idToEpic.containsKey(epicId)) {
            return;
        }
        Epic epic = idToEpic.get(epicId);
        for (int subtaskId : epic.getSubtaskIds()) {
            idToSubtask.remove(subtaskId);
            historyManager.remove(subtaskId);
            prioritizedTasks.removeIf(s -> s.getId() == subtaskId);
        }
        epic.removeAllSubtaskIds();
        updateEpicsStatus(epicId);
        clearEpicTime(epic);
    }

    @Override
    public void removeAllTasks() {
        for (int taskId : idToTask.keySet()) {
            historyManager.remove(taskId);
            prioritizedTasks.removeIf(task -> task.getId() == taskId);
        }
        idToTask.clear();
    }

    @Override
    public void removeAllEpics() {
        for (int epicId : idToEpic.keySet()) {
            historyManager.remove(epicId);
        }
        idToEpic.clear();

        for (int subtaskId : idToSubtask.keySet()) {
            historyManager.remove(subtaskId);
            prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
        }
        idToSubtask.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (int subtaskId : idToSubtask.keySet()) {
            historyManager.remove(subtaskId);
            prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
        }
        idToSubtask.clear();

        for (Epic epic : idToEpic.values()) {
            epic.removeAllSubtaskIds();
            updateEpicsStatus(epic.getId());
            clearEpicTime(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
