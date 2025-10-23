package manager;

import data.*;
import exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static File file;
    private static FileBackedTaskManager instance;

    /// Параметры CSV файла
    private static final String TABLE_HEADER = "id,type,name,status,description,epic";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String DELIMITER = ",";
    private static final String QUOTE = "\"";

    /// Максимальный id
    private int maxId = 0;

    /// Буфер чтения
    private List<String> fileToList;

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

    /// Перезагрузить данные из файла
    public void updateDataFromFile() throws ManagerSaveException {
        super.removeAllTasks();
        super.removeAllEpics();

        if (fileToList.size() > 1) {
            setMaxId();
        }

        int rowNumber = 1;
        while (rowNumber < fileToList.size()) {
            Task taskObject = getDataObjectFromString(fileToList.get(rowNumber));

            if (taskObject == null) {
                return;
            }

            maxId = taskObject.getId();

            if (taskObject instanceof Epic epic) {
                idToEpic.put(maxId, epic);
            } else if (taskObject instanceof Subtask subtask) {
                int epicId = subtask.getEpicId();
                if (epicId == 0) {
                    System.out.println("Подзадача c id = " + subtask.getId() + " не привязана к эпику. Epic id = 0.");
                    rowNumber++;
                    continue;
                }
                Epic thisEpic = idToEpic.get(epicId);
                if (thisEpic == null) {
                    System.out.println("У подзадачи c id = " + subtask.getId()
                            + " отсутствует эпик c id = " + epicId + ".");
                    rowNumber++;
                    continue;
                }
                thisEpic.addSubtaskId(maxId);

                idToSubtask.put(maxId, subtask);
                updateEpicsStatus(thisEpic.getId());
            } else {
                idToTask.put(maxId, taskObject);
            }

            rowNumber++;
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
        List<Task> all = getAll();
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

    private void setMaxId() {
        Task lsatTaskObject = getDataObjectFromString(fileToList.getLast());
        if (lsatTaskObject != null) {
            maxId = lsatTaskObject.getId();
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

        Integer id = taskObject.getId();
        Type type = getType(id);

        String title = taskObject.getTitle() != null && taskObject.getTitle().contains(DELIMITER)
                ? QUOTE + taskObject.getTitle() + QUOTE
                : taskObject.getTitle();

        String description = taskObject.getDescription() != null && taskObject.getDescription().contains(DELIMITER)
                ? QUOTE + taskObject.getDescription() + QUOTE
                : taskObject.getDescription();

        String status = taskObject.getStatus() != null ? taskObject.getStatus().toString() : "null";

        String epic = type.equals(Type.SUBTASK) ? String.valueOf(((Subtask) taskObject).getEpicId()) : "";

        return id + DELIMITER +
                type + DELIMITER +
                title + DELIMITER +
                status + DELIMITER +
                description + DELIMITER +
                epic;
    }

    /// Получить объект данных из строки
    private Task getDataObjectFromString(String stringFromFile) {
        try {
            if (stringFromFile.isEmpty()) {
                throw new ManagerSaveException("Невозможно создать задачу из пустой строки");
            }

            String[] splitStr = stringFromFile.split(DELIMITER + "(?!\\s)");
            if (splitStr.length < 5) {
                throw new ManagerSaveException("Не поддерживаемый формат строки");
            }

            Integer id = Integer.parseInt(splitStr[0]);
            Type type = Type.valueOf(splitStr[1]);
            String title = splitStr[2].replace(QUOTE, "");
            String description = splitStr[4].replace(QUOTE, "");
            Status status = !splitStr[3].equals("null") ? Status.valueOf(splitStr[3]) : null;

            switch (type) {
                case TASK:
                    return new Task(id, title, description, status);
                case EPIC:
                    Epic newEpic = new Epic(id, title, description);
                    newEpic.setStatus(status);
                    return newEpic;
                case SUBTASK:
                    int epicId = 0;
                    try {
                        epicId = Integer.parseInt(splitStr[5]);
                    } catch (IndexOutOfBoundsException e) {
                        // epicId = 0
                    }
                    return new Subtask(id, title, description, status, epicId);
                default:
                    return null;
            }
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Task setTask(Task newTask) {
        Task result = super.setTask(newTask);
        save();
        setMaxId();
        return result;
    }

    @Override
    public Epic setEpic(Epic newEpic) {
        Epic result = super.setEpic(newEpic);
        save();
        setMaxId();
        return result;
    }

    @Override
    public Subtask setSubtask(Subtask newSubtask) {
        Subtask result = super.setSubtask(newSubtask);
        save();
        setMaxId();
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
