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

    /// Текущая строка таблицы
    private int lineNumber = 0;

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
            System.out.println("Создан новый файл: " + file.getPath());
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
        readFileToList();

        lineNumber = 1;
        while (lineNumber < fileToList.size()) {
            Task taskObject = getDataObjectFromString(fileToList.get(lineNumber));
            if (taskObject == null) {
                return;
            }

            counter = taskObject.getId();

            if (taskObject instanceof Epic) {
                super.setEpic((Epic) taskObject);
            } else if (taskObject instanceof Subtask) {
                super.setSubtask((Subtask) taskObject);
            } else {
                super.setTask(taskObject);
            }

            lineNumber++;
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
        lineNumber = 1;
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
            lineNumber = fileToList.size();

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

    /// Перезагрузить данные из файла если файл был изменен извне
    private void checkAndReloadFileData() throws ManagerSaveException {
        if (fileToList == null) {
            return;
        }

        List<String> readedList;
        try {
            readedList = Files.readAllLines(file.toPath(), CHARSET);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла");
        }

        for (int i = 0; i < readedList.size(); i++) {
            String str1 = readedList.get(i);
            String str2 = fileToList.get(i);
            if (!str1.equals(str2)) {
                System.out.println("Файл был изменен извне.");
                updateDataFromFile();
                System.out.println("Новые данные загружены.");
            }
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
                    int epicId = Integer.parseInt(splitStr[5]);
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
        Task result = null;
        try {
           checkAndReloadFileData();
            result = super.setTask(newTask);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при добавлении задачи");
        }
        return result;
    }

    @Override
    public Epic setEpic(Epic newEpic) {
        Epic result = null;
        try {
            checkAndReloadFileData();
            result = super.setEpic(newEpic);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при добавлении эпика");
        }
        return result;
    }

    @Override
    public Subtask setSubtask(Subtask newSubtask) {
        Subtask result = null;
        try {
            checkAndReloadFileData();
            result = super.setSubtask(newSubtask);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при добавлении подзадачи");
        }
        return result;
    }

    @Override
    public Task updateTask(Task newTask) {
        Task result = null;
        try {
            checkAndReloadFileData();
            result = super.updateTask(newTask);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при изменении задачи");
        }
        return result;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic result = null;
        try {
            checkAndReloadFileData();
            result = super.updateEpic(newEpic);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при изменении эпика");
        }
        return result;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask result = null;
        try {
            checkAndReloadFileData();
            result = super.updateSubtask(newSubtask);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при изменении подзадачи");
        }
        return result;
    }

    @Override
    public boolean removeTask(int taskId) {
        boolean result = false;
        try {
            checkAndReloadFileData();
            result = super.removeTask(taskId);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при удалении задачи");
        }
        return result;
    }

    @Override
    public boolean removeEpic(int epicId) {
        boolean result = false;
        try {
           checkAndReloadFileData();
            result = super.removeEpic(epicId);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при удалении эпика");
        }
        return result;
    }

    @Override
    public boolean removeSubtask(int subtaskId) {
        boolean result = false;
        try {
            checkAndReloadFileData();
            result = super.removeSubtask(subtaskId);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при удалении подзадачи");
        }
        return result;
    }

    @Override
    public void removeAllSubtasksByEpic(int epicId) {
        try {
            checkAndReloadFileData();
            super.removeAllSubtasksByEpic(epicId);
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при удалении всех подзадач эпика");
        }
    }

    @Override
    public void removeAllTasks() {
        try {
            checkAndReloadFileData();
            super.removeAllTasks();
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при удалении всех задач");
        }
    }

    @Override
    public void removeAllEpics() {
        try {
            checkAndReloadFileData();
            super.removeAllEpics();
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при удалении всех эпиков");
        }
    }

    @Override
    public void removeAllSubTasks() {
        try {
            checkAndReloadFileData();
            super.removeAllSubTasks();
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage() + " при удалении всех подзадач");
        }
    }
}
