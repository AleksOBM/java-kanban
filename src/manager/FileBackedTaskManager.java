package manager;

import data.*;
import exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;
    private final Charset charset = StandardCharsets.UTF_8;
    private final String delimiter = ",";
    private final String quote = "\"";

    FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile(file);
    }

    private void loadFromFile(File file) {

        if (!file.exists()) {
            File dir = new File(file.getParent());
            try {
                dir.mkdir();
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.append("id,type,name,status,description,epic\n");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        try (
                FileReader reader = new FileReader(file.getPath(), charset);
                BufferedReader br = new BufferedReader(reader)
        ) {
            br.readLine();

            while (br.ready()) {
                Task taskObject = getTaskObjectFromString(br.readLine());
                if (taskObject == null) {
                    return;
                }

                if (counter < taskObject.getId()) {
                    counter++;
                }

                if (taskObject instanceof Epic) {
                    super.setEpic((Epic) taskObject);
                } else if (taskObject instanceof Subtask) {
                    super.setSubtask((Subtask) taskObject);
                } else {
                    super.setTask(taskObject);
                }
            }
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Файл не прочитан");
            } catch (ManagerSaveException ex) {
                System.out.println(ex.getMessage());
            }
        }
        save();
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task taskObject : getAll()) {
                writer.append(getStringToFileSave(taskObject)).append("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getStringToFileSave(Task taskObject) {

        Integer id = taskObject.getId();
        Type type = getType(id);

        String title = taskObject.getTitle() != null && taskObject.getTitle().contains(delimiter)
                ? quote + taskObject.getTitle() + quote
                : taskObject.getTitle();

        String description = taskObject.getDescription() != null && taskObject.getDescription().contains(delimiter)
                ? quote + taskObject.getDescription() + quote
                : taskObject.getDescription();

        String status = taskObject.getStatus() != null ? taskObject.getStatus().toString() : "null";

        String epic = type.equals(Type.SUBTASK) ? String.valueOf(((Subtask) taskObject).getEpicId()) : "";

        return id + delimiter +
                type + delimiter +
                title + delimiter +
                status + delimiter +
                description + delimiter +
                epic;
    }

    private Task getTaskObjectFromString(String stringFromFile) {
        try {
            if (stringFromFile.isEmpty()) {
                throw new ManagerSaveException("Невозможно создать задачу из пустой строки");
            }

            String[] splitStr = stringFromFile.split(delimiter + "(?!\\s)");
            if (splitStr.length < 5) {
                throw new ManagerSaveException("Не поддерживаемый формат строки");
            }

            Integer id = Integer.parseInt(splitStr[0]);
            Type type = Type.valueOf(splitStr[1]);
            String title = splitStr[2].replace(quote, "");
            String description = splitStr[4].replace(quote, "");
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
    public Type getType(int id) {
        return super.getType(id);
    }

    @Override
    public Task get(int id) {
        return super.get(id);
    }

    @Override
    public Task getWithoutHistory(int id) {
        return super.getWithoutHistory(id);
    }

    @Override
    public List<Task> getAll() {
        return super.getAll();
    }

    @Override
    public Task setTask(Task newTask) {
        Task result = super.setTask(newTask);
        save();
        return result;
    }

    @Override
    public Epic setEpic(Epic newEpic) {
        Epic result = super.setEpic(newEpic);
        save();
        return result;
    }

    @Override
    public Subtask setSubtask(Subtask newSubtask) {
        Subtask result = super.setSubtask(newSubtask);
        save();
        return result;
    }

    @Override
    public Task getTask(int taskID) {
        return super.getTask(taskID);
    }

    @Override
    public Epic getEpic(int epicID) {
        return super.getEpic(epicID);
    }

    @Override
    public Subtask getSubtask(int subTaskID) {
        return super.getSubtask(subTaskID);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public ArrayList<Subtask> getAllSubTasksByEpic(int epicId) {
        return super.getAllSubTasksByEpic(epicId);
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

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
