import data.*;
import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import manager.TaskManagerType;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static TaskManager manager;
    private static final Scanner scanner = new Scanner(System.in);
    private static final String SEPARATOR = ("\n" + "-".repeat(20) + "\n");

    public static void main(String[] args) {
        manager = Managers.getTaskManager(TaskManagerType.FILE_BACKED);
        consoleStart();
    }

    /// Запустить консольную версию
    static void consoleStart() {
        System.out.println(SEPARATOR);

        while (true) {
            System.out.println("Введите команду или help");
            System.out.print("--> ");

            try {
                switch (scanner.nextLine().trim()) {
                    case "help" -> consolePrintHelp();
                    case "update" -> consoleUpdateFromFile();
                    case "print" -> printListAll();
                    case "history" -> consoleHistory();
                    case "find" -> consoleFind();
                    case "add-task" -> consoleAddTask();
                    case "add-epic" -> consoleAddEpic();
                    case "add-sub" -> consoleAddSubtask();
                    case "set-status" -> consoleSetStatus();
                    case "set-time" -> consoleSetTime();
                    case "set-dur" -> consoleSetDuration();
                    case "remove" -> consoleRemove();
                    case "exit" -> {
                        System.out.println("\n" + "-".repeat(20));
                        return;
                    }
                    default -> {
                        System.out.println("Такой команды нет");
                        System.out.println(SEPARATOR);
                    }
                }

            } catch (Exception exception) {
                System.out.println(SEPARATOR);
                System.out.println("Что то пошло не так");
                System.out.println(exception.getMessage());
                System.out.println(SEPARATOR);
            }
        }
    }

    static void consolePrintHelp() {
        System.out.println(SEPARATOR);
        System.out.println("Обновить данные --> update");
        System.out.println("Поиск по id --> find");
        System.out.println("Вывести весь список --> print");
        System.out.println("Добавить задачу --> add-task");
        System.out.println("Добавить эпик --> add-epic");
        System.out.println("Добавить подзадачу --> add-sub");
        System.out.println("Удалить --> remove");
        System.out.println("Изменить статус --> set-status");
        System.out.println("Изменить дату старта --> set-time");
        System.out.println("Изменить срок (в минутах) --> set-dur");
        System.out.println("Показать историю --> history");
        System.out.println("Вывести инструкцию --> help");
        System.out.println("Выход --> exit");
        System.out.println(SEPARATOR);
    }

    static void consoleAddTask() {
        System.out.println(SEPARATOR);
        System.out.print("Заголовок -> ");
        String title = readText();
        System.out.print("Описание -> ");
        String description = readText();
        Task newTsk = new Task(null, title, description, null);
        manager.addTask(newTsk);
        System.out.println(SEPARATOR);
        System.out.println("Задача добавлена");
        System.out.println(newTsk);
        System.out.println(SEPARATOR);
    }

    static void consoleAddEpic() {
        System.out.println(SEPARATOR);
        System.out.print("Заголовок -> ");
        String epicTitle = scanner.nextLine().trim();
        System.out.print("Описание -> ");
        String epicDescription = scanner.nextLine().trim();
        Epic newEp = new Epic(null, epicTitle, epicDescription);
        manager.addEpic(newEp);
        System.out.println(SEPARATOR);
        System.out.println("Эпик добавлен");
        System.out.println(newEp);
        System.out.println(SEPARATOR);
    }

    static void consoleAddSubtask() {
        while (true) {
            System.out.println(SEPARATOR);
            System.out.println("Выберите id эпика из списка");
            System.out.println("Эпики:");
            System.out.println(getAllEpicsListing());

            int id = readId();
            if (id == 0) {
                System.out.println(SEPARATOR);
                return;
            }

            Task taskObject = manager.getWithoutHistory(id);

            if (taskObject == null || taskObject.getType() != Type.EPIC) {
                System.out.println("Такого id эпика нет");
                continue;
            }

            System.out.println(SEPARATOR);
            System.out.print("Заголовок -> ");
            String title = readText();
            System.out.print("Описание -> ");
            String description = readText();
            Subtask subtask = new Subtask(
                    null, title, description, null, id, null, null
            );
            manager.addSubtask(subtask);
            System.out.println("Подзадача добавлена");
            System.out.println(subtask);
            System.out.println(SEPARATOR);
            return;
        }
    }

    static void consoleFind() {

        while (true) {
            System.out.println(SEPARATOR);
            System.out.println("Поиск по id:");

            int id = readId();
            if (id == 0) {
                System.out.println(SEPARATOR);
                return;
            }

            Task taskObject = manager.get(id);

            if (taskObject == null) {
                System.out.println("Такого id не существует.");
                continue;
            }

            System.out.println(SEPARATOR);
            System.out.println(taskObject);
        }
    }

    static void consoleSetStatus() {

        Task taskObject;

        while (true) {
            System.out.println(SEPARATOR);
            System.out.println("Установить статус:");

            int id = readId();
            if (id == 0) {
                System.out.println(SEPARATOR);
                return;
            }

            taskObject = manager.getWithoutHistory(id);

            if (taskObject == null) {
                System.out.println("id " + id + " не существует");
                System.out.println(SEPARATOR);
                continue;
            }

            if (manager.getType(id) == Type.EPIC) {
                System.out.println("Это id эпика - статус автоматический");
                System.out.println(SEPARATOR);
                continue;
            }

            break;
        }

        while (true) {
            System.out.println(SEPARATOR);
            System.out.println("NEW -> 1");
            System.out.println("IN_PROGRESS -> 2");
            System.out.println("DONE -> 3");
            System.out.print("Новый статус № -> ");

            int command = readInt();

            switch (command) {
                case 1:
                    setStatus(taskObject, Status.NEW);
                    System.out.println(SEPARATOR);
                    System.out.println("Статус -> " + Status.NEW);
                    System.out.println(taskObject);
                    break;
                case 2:
                    setStatus(taskObject, Status.IN_PROGRESS);
                    System.out.println(SEPARATOR);
                    System.out.println("Статус -> " + Status.IN_PROGRESS);
                    System.out.println(taskObject);
                    break;
                case 3:
                    setStatus(taskObject, Status.DONE);
                    System.out.println(SEPARATOR);
                    System.out.println("Статус -> " + Status.DONE);
                    System.out.println(taskObject);
                    break;
                default:
                    System.out.println(command + " - Неверный номер");
                    continue;
            }

            System.out.println(SEPARATOR);
            break;
        }
    }

    static void consoleSetTime() {
        while (true) {
            System.out.println(SEPARATOR);
            System.out.println("Установить время:");

            int id = readId();
            if (id == 0) {
                System.out.println(SEPARATOR);
                return;
            }

            Task taskObject = manager.getWithoutHistory(id);
            Type type = manager.getType(id);

            if (taskObject == null) {
                System.out.println("Такого id не существует");
                continue;
            } else if (type == Type.EPIC) {
                System.out.println("Это id эпика - время автоматическое");
                continue;
            }

            System.out.print("Часы -> ");
            int hours = readInt();
            System.out.print("Минуты -> ");
            int minutes = readInt();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hours, minutes);
            System.out.println(SEPARATOR);

            switch (type) {
                case TASK -> System.out.println(manager.updateTask(new Task(
                        id, null, null, null, dateTime, null)
                ));
                case SUBTASK -> System.out.println(manager.updateSubtask((new Subtask(
                        id, null, null, null, null, dateTime, null)
                )));
            }
            System.out.println(SEPARATOR);
            break;
        }

    }

    static void consoleSetDuration() {
        while (true) {
            System.out.println(SEPARATOR);
            System.out.println("Установить продолжительность:");

            int id = readId();
            if (id == 0) {
                System.out.println(SEPARATOR);
                return;
            }

            Task taskObject = manager.getWithoutHistory(id);
            Type type = manager.getType(id);

            if (taskObject == null) {
                System.out.println("Такого id не существует");
                continue;
            } else if (type == Type.EPIC) {
                System.out.println("Это id эпика - время автоматическое");
                continue;
            }

            System.out.print("Минуты -> ");
            int dur = readInt();

            Duration duration = Duration.ZERO;
            if (dur > 0) {
                duration = Duration.ofMinutes(dur);
            }
            System.out.println(SEPARATOR);

            switch (type) {
                case TASK -> System.out.println(manager.updateTask(new Task(
                        id, null, null, null, null, duration)));
                case SUBTASK -> System.out.println(manager.updateSubtask((new Subtask(
                        id, null, null, null, null, null, duration))));
            }
            System.out.println(SEPARATOR);
            break;
        }
    }

    static void consoleRemove() {
        while (true) {
            System.out.println(SEPARATOR);
            System.out.println("Удаление по id:");

            int id = readId();
            if (id == 0) {
                System.out.println(SEPARATOR);
                return;
            }

            if (manager.getWithoutHistory(id) == null) {
                System.out.println("Такого id не существует");
                continue;
            }
            System.out.println(SEPARATOR);

            Type type = manager.getType(id);
            switch (type) {
                case TASK -> System.out.println(removeTask(id));
                case EPIC -> System.out.println(removeEpic(id));
                case SUBTASK -> System.out.println(removeSubtask(id));
            }
            System.out.println(SEPARATOR);
            break;
        }
    }

    static void consoleUpdateFromFile() {
        System.out.println(SEPARATOR);
        FileBackedTaskManager manager1 = FileBackedTaskManager
                .getInstance(new File("src/autosave/data.csv"));

        manager1.updateDataFromFile();
        System.out.println("Данные загружены.");
        System.out.println(SEPARATOR);
    }

    static void consoleHistory() {
        System.out.println(SEPARATOR);
        StringBuilder stringBuilder;
        System.out.println("История:");
        int i = 1;
        for (Task task : manager.getHistory()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(i).append(". ").append(task);
            System.out.println(stringBuilder);
            i++;
        }
        System.out.println(SEPARATOR);
    }

    /// Установить статус
    private static void setStatus(Task taskObject, Status status) {
        Type type = manager.getType(taskObject.getId());
        taskObject.setStatus(status);
        switch (type) {
            case TASK:
                manager.updateTask(taskObject);
                break;
            case EPIC:
                manager.updateEpic((Epic) taskObject);
                break;
            case SUBTASK:
                manager.updateSubtask((Subtask) taskObject);
                break;
            default:
                break;
        }
    }

    /// Прочитать id из консоли
    private static int readId() {
        int id;
        while (true) {
            System.out.println("Назад -> 0");
            System.out.print("id -> ");

            try {
                id = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception exception) {
                System.out.println("id должен быть числом");
                System.out.println(SEPARATOR);
                continue;
            }
            break;
        }
        return id;
    }

    /// Прочитать текст из консоли
    private static String readText() {
        return scanner.nextLine().trim();
    }

    /// Прочитать int из консоли
    private static int readInt() {
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception exception) {
            return -1;
        }
        return id;
    }

    /// Вывести все данные
    private static void printListAll() {
        System.out.println(SEPARATOR);
        System.out.println("Вывести все что есть:" + "\n");
        StringBuilder list = new StringBuilder();
        list.append("Задачи:").append("\n").append(getAllTasksListing()).append("\n");
        List<Epic> epics = manager.getAllEpics();
        int epicIndex = 1;
        for (Epic epic : epics) {
            list.append("\n").append("Эпик ").append(epicIndex).append(":\n");
            list.append(epic).append("\n");
            List<Subtask> subtasks = manager.getAllSubTasksByEpic(epic.getId());
            int subtaskIndex = 1;
            list.append("Подзадачи:").append("\n");
            for (Subtask subtask : subtasks) {
                list.append(subtaskIndex).append(". ").append(subtask).append("\n");
                subtaskIndex++;
            }
            epicIndex++;
        }
        System.out.println(list);
        System.out.println(SEPARATOR);
    }

    /// Получение списка всех задач
    private static String getAllTasksListing() {
        StringBuilder taskList = new StringBuilder();
        List<Task> tasks = manager.getAllTasks();
        int i = 1;
        for (Task task : tasks) {
            if (i == 1) {
                taskList.append(i).append(". ").append(task);

            } else {
                taskList.append("\n").append(i).append(". ").append(task);
            }
            i++;
        }
        return taskList.toString();

    }

    /// Получение списка всех эпиков
    private static String getAllEpicsListing() {
        return manager.getAllEpics().stream().map(Epic::toString)
                .collect(Collectors.joining("\n"));
    }

    /// Удаление задачи по идентификатору
    private static String removeTask(int taskId) {
        Task task = manager.getTask(taskId);
        String removedTask = task.toString();
        manager.removeTask(taskId);
        return "removed" + removedTask;
    }

    /// Удаление подзадачи по идентификатору
    private static String removeSubtask(int subtaskId) {
        Subtask subtask = manager.getSubtask(subtaskId);
        String removedSubtask = subtask.toString();
        manager.removeSubtask(subtaskId);
        return "removed" + removedSubtask;
    }

    /// Удаление эпика по идентификатору
    private static String removeEpic(int epicId) {
        Epic epic = manager.getEpic(epicId);
        String removedEpic = epic.toString();
        manager.removeEpic(epicId);
        return "removed" + removedEpic;
    }

    /// Получение эпика со списком его подзадач
    private static StringBuilder getEpicWithSubtasksList(int epicId) {
        StringBuilder list = new StringBuilder();
        Epic epic = manager.getEpic(epicId);
        list.append("Эпик").append(":\n");
        list.append(epic).append("\n");
        List<Subtask> subtasks = manager.getAllSubTasksByEpic(epic.getId());
        int subtaskIndex = 1;
        list.append("Подзадачи:").append("\n");
        for (Subtask subtask : subtasks) {
            list.append(subtaskIndex).append(". ").append(subtask).append("\n");
            subtaskIndex++;
        }
        return list;
    }

    /// Удаление эпика с его подзадачами
    private static StringBuilder removeEpicWithSubtasks(int epicId) {
        StringBuilder list = new StringBuilder();
        Epic epic = manager.getEpic(epicId);
        String removedEpic = epic.toString();
        List<Subtask> removedSudtasks = manager.getAllSubTasksByEpic(epicId);
        manager.removeEpic(epicId);
        manager.removeAllSubtasksByEpic(epicId);

        list.append("removed").append(removedEpic).append("\n");
        for (Subtask subtask : removedSudtasks) {
            list.append("removed").append(subtask).append("\n");
        }

        return list;
    }

    /// Удаление всех подзадач эпика
    private static StringBuilder removeAllEpicSubtasks(int epicId) {
        StringBuilder list = new StringBuilder();
        List<Subtask> removedSudtasks = manager.getAllSubTasksByEpic(epicId);
        manager.removeAllSubtasksByEpic(epicId);

        for (Subtask subtask : removedSudtasks) {
            list.append("removed").append(subtask).append("\n");
        }

        return list;
    }

    /// Получение списка всех подзадач
    private static StringBuilder getAllSubtasksList() {
        StringBuilder subtaskList = new StringBuilder();
        List<Subtask> subtasks = manager.getAllSubtasks();
        int i = 1;
        for (Subtask subtask : subtasks) {
            if (i == 1) {
                subtaskList.append(i).append(". ").append(subtask);

            } else {
                subtaskList.append("\n").append(i).append(". ").append(subtask);
            }
            i++;
        }
        return subtaskList;
    }

    /// Получение списка всех подзадач эпика
    private static StringBuilder getAllSubtasksList(int epicId) {
        Epic epic = manager.getEpic(epicId);
        if (epic == null) {
            return null;
        }
        StringBuilder subtaskList = new StringBuilder();
        List<Subtask> thisSubtasks = manager.getAllSubTasksByEpic(epic.getId());
        int i = 1;
        for (Subtask subtask : thisSubtasks) {
            if (i == 1) {
                subtaskList.append(i).append(". ").append(subtask);

            } else {
                subtaskList.append("\n").append(i).append(". ").append(subtask);
            }
            i++;
        }
        return subtaskList;
    }

}