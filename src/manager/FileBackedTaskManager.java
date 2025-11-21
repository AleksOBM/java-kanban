package manager;

import data.*;
import exception.ManagerSaveException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements Property {

    private static File file;
    private static FileBackedTaskManager instance;

    /// Параметры CSV файла
    //                                          0   1    2     3         4        5    6      7     8
    private static final String TABLE_HEADER = "id,type,name,status,description,start,end,duration,epic";
    private static final String DELIMITER = ",";
    private static final String QUOTE = "\"";

    /// Максимальный id
    private int maxId = 0;

    /// Буфер чтения строк
    private List<String> fileToList;

    /// Буфер чтения объектов
    private final TreeSet<Task> objectToList = new TreeSet<>(Task::compareTo);

    /// Конструктор
    FileBackedTaskManager(File newFile) {
        try {
            loadFile(newFile);
            updateDataFromFile();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    /// Получить синглтон класса
    public static FileBackedTaskManager getInstance(File newFile) {
        if (instance == null || !newFile.getPath().equals(file.getPath())) {
            instance = new FileBackedTaskManager(newFile);
        }
        return instance;
    }

    /// Подгрузить файл
    private void loadFile(File newFile) throws ManagerSaveException {
        if (!newFile.exists()) {
            try {
                Files.createFile(Path.of(newFile.getPath()));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла");
            }
            System.out.println("Создан новый файл: " + newFile.getPath());
        }
        file = newFile;

        readFileToList();
        if (fileToList.isEmpty()) {
            resetFile();
            readFileToList();
        }

        String header = fileToList.getFirst();
        if (header == null || !header.equals(TABLE_HEADER)) {
            resetFile();
            readFileToList();
        }
    }

    /// Загрузить объекты из буфера чтения в буфер объектов
    private void loadObjectBuffer() throws ManagerSaveException {
        int rowNumber = 1;
        while (rowNumber < fileToList.size()) {
            Task taskObject = getDataObjectFromString(fileToList.get(rowNumber));

            if (taskObject == null) {
                throw new ManagerSaveException("Ошибка преобразования при считывании из файла");
            }

            int taskId = taskObject.getId();
            if (maxId < taskId) {
                maxId = taskId;
            }

            objectToList.add(taskObject);

            rowNumber++;
        }
    }

    /// Перезагрузить данные из файла2
    public void updateDataFromFile() throws ManagerSaveException {
        loadObjectBuffer();

        super.removeAllTasks();
        super.removeAllEpics();

        if (objectToList.isEmpty()) {
            return;
        }

        prioritizedTasks.addAll(objectToList.stream()
                .filter(task -> task.getType() != Type.EPIC)
                .filter(task -> task.getStartTime() != null).toList()
        );

        for (Task taskObject : objectToList) {

            switch (taskObject.getType()) {
                case TASK -> idToTask.put(taskObject.getId(), taskObject);
                case EPIC -> idToEpic.put(taskObject.getId(), (Epic) taskObject);
                case SUBTASK -> {
                    Subtask subtask = (Subtask) taskObject;
                    int subtaskId = subtask.getId();
                    int epicId = subtask.getEpicId();

                    if (epicId == 0) {
                        System.out.println("Подзадача c id = " + subtaskId + " не привязана к эпику. Epic id = 0.");
                        continue;
                    }

                    Epic epic = idToEpic.get(epicId);

                    if (epic == null) {
                        System.out.println("У подзадачи c id = " + subtask.getId()
                                + " отсутствует эпик c id = " + epicId + ".");
                        continue;
                    }

                    epic.addSubtaskId(subtaskId);

                    idToSubtask.put(subtaskId, subtask);
                    updateEpicsStatus(epicId);
                    updateEpicTime(epic);
                }
                default -> throw new ManagerSaveException("Не верный тип задачи в таблице");
            }

        }

        if (maxId != 0) {
            if (counter > maxId) {
                maxId = counter;
            } else {
                counter = maxId + 1;
            }
        }
    }

    /// Очистить содержимое файла, добавить шапку таблицы
    private void resetFile() throws ManagerSaveException {
        fileToList.clear();
        fileToList.add(TABLE_HEADER);

        try {
            Files.write(file.toPath(), fileToList);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сброса файла");
        }
    }

    /// Выгрузить все данные в файл
    private void save() throws ManagerSaveException {
        List<? extends Task> all = getAll().stream()
                .sorted(Comparator.comparing(Task::getStartTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        if (all.isEmpty()) {
            resetFile();
            readFileToList();
            return;
        }

        try {
            fileToList.clear();
            fileToList.add(TABLE_HEADER);
            for (Task taskObject : all) {
                fileToList.add(getStringToFileSave(taskObject));
            }

            Files.write(file.toPath(), fileToList);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }
    }

    /// Прочитать содержимое файла в буфер
    private void readFileToList() throws ManagerSaveException {
        try {
            fileToList = Files.readAllLines(file.toPath(), CHARSET);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла");
        }
    }

    /// Получить строку для записи в файл из объекта
    private String getStringToFileSave(Task taskObject) {

        String startTime = taskObject.getStartTime() != null ?
                taskObject.getStartTime().format(DATE_TIME_FORMATTER) : "null";

        String endTime = taskObject.getEndTime() != null ?
                taskObject.getEndTime().format(DATE_TIME_FORMATTER) : "null";

        long duration = taskObject.getDuration().toMinutes();

        Integer id = taskObject.getId();
        Type type = getType(id);

        String title = taskObject.getTitle() != null && taskObject.getTitle().contains(DELIMITER)
                ? QUOTE + taskObject.getTitle() + QUOTE
                : taskObject.getTitle();

        String description = taskObject.getDescription() != null && taskObject.getDescription().contains(DELIMITER)
                ? QUOTE + taskObject.getDescription() + QUOTE
                : taskObject.getDescription();

        String status = taskObject.getStatus() != null ? taskObject.getStatus().toString() : "null";

        String epicId = type.equals(Type.SUBTASK) ? String.valueOf(((Subtask) taskObject).getEpicId()) : "";

        return id + DELIMITER +
                type + DELIMITER +
                title + DELIMITER +
                status + DELIMITER +
                description + DELIMITER +
                startTime + DELIMITER +
                endTime + DELIMITER +
                duration + DELIMITER +
                epicId;
    }

    /// Получить объект данных из строки
    private Task getDataObjectFromString(String stringFromFile) {
        try {
            if (stringFromFile.isEmpty()) {
                throw new ManagerSaveException("Невозможно создать задачу из пустой строки");
            }

            String[] splitStr = stringFromFile.split(DELIMITER + "(?!\\s)");
            if (splitStr.length < 8 || splitStr.length > 9) {
                throw new ManagerSaveException("Не поддерживаемый формат строки");
            }

            Integer id = Integer.parseInt(splitStr[0]);
            Type type = Type.valueOf(splitStr[1]);
            String title = splitStr[2].replace(QUOTE, "");
            Status status = !splitStr[3].equals("null") ? Status.valueOf(splitStr[3]) : null;
            String description = splitStr[4].replace(QUOTE, "");
            LocalDateTime startTime = splitStr[5].equals("null") ? null :
                    LocalDateTime.parse(splitStr[5], DATE_TIME_FORMATTER);
            // endTime здесь не нужен - пропускаем.
            Duration duration = Duration.ofMinutes(Integer.parseInt(splitStr[7]));

            switch (type) {
                case TASK:
                    return new Task(id, title, description, status, startTime, duration);
                case EPIC:
                    Epic newEpic = new Epic(id, title, description);
                    newEpic.setStartTime(startTime);
                    newEpic.setStatus(status);
                    newEpic.setDuration(duration);
                    return newEpic;
                case SUBTASK:
                    int epicId = 0;
                    try {
                        epicId = Integer.parseInt(splitStr[8]);
                    } catch (IndexOutOfBoundsException e) {
                        // epicId == 0
                    }
                    return new Subtask(id, title, description, status, epicId, startTime, duration);
                default:
                    return null;
            }
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Task addTask(Task newTask) {
        Task result = super.addTask(newTask);
        save();
        return result;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        Epic result = super.addEpic(newEpic);
        save();
        return result;
    }

    @Override
    public Subtask addSubtask(Subtask newSubtask) {
        Subtask result = super.addSubtask(newSubtask);
        save();
        return result;
    }

    @Override
    public Task updateTask(Task newTask) {
        Task result = super.updateTask(newTask);
        save();
        return result;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic result = super.updateEpic(newEpic);
        save();
        return result;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask result = super.updateSubtask(newSubtask);
        save();
        return result;
    }

    @Override
    public boolean removeTask(int taskId) {
        boolean result = super.removeTask(taskId);
        save();
        return result;
    }

    @Override
    public boolean removeEpic(int epicId) {
        boolean result = super.removeEpic(epicId);
        save();
        return result;
    }

    @Override
    public boolean removeSubtask(int subtaskId) {
        boolean result = super.removeSubtask(subtaskId);
        save();
        return result;
    }

    @Override
    public void removeAllSubtasksByEpic(int epicId) {
        super.removeAllSubtasksByEpic(epicId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }
}
