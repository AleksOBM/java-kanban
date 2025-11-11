import data.*;
import exception.ManagerSaveException;
import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import manager.TaskManagerType;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    static TaskManager manager = Managers.getTaskManager(TaskManagerType.FILE_BACKED);
    static Scanner scanner = new Scanner(System.in);
    static final int MS = 1500;

    public static void main(String[] args) {

        start();
    }

    /// Ввести исходные данные
    static void insertStartData() {
        Task task1 = manager.addTask(new Task(
                null,
                "Познакомиться с девушкой",
                "Она должна понравиться моей маме",
                null,
                LocalDateTime.of(2025, 11, 07, 12, 0),
                Duration.ofMinutes(135)
        ));
        waitSecond(MS);

        Task task2 = manager.addTask(new Task(
                null,
                "Сводить девушку на свидание",
                "По правилам этикета нужно заплатить за нее в ресторане"
        ));
        waitSecond(MS);

        Task task3 = manager.addTask(new Task(
                null,
                "Подарить девушке цветы",
                "Не дарить хризантемы"
        ));
        waitSecond(MS);

        Epic epic1 = manager.addEpic(new Epic(
                null,
                "Найти девушку для знакомства",
                "Использовать различные способы поиска, для ускорения процесса"
        ));
        waitSecond(MS);

        Subtask subtask1 = manager.addSubtask(new Subtask(
                null,
                "Поиск на сайтах знакомств",
                null,
                null,
                epic1.getId(),
                null,
                null
        ));
        waitSecond(MS);

        Subtask subtask2 = manager.addSubtask(new Subtask(
                null,
                "Поиск через знакомых",
                null,
                null,
                epic1.getId(),
                null,
                null
        ));
        waitSecond(MS);

        Subtask subtask3 = manager.addSubtask(new Subtask(
                null,
                "Поиск в литературном клубе",
                null,
                null,
                epic1.getId(),
                null,
                null
        ));
        waitSecond(MS);

        Epic epic2 = manager.addEpic(new Epic(
                null,
                "Оптимизировать поиск девушки",
                "Получить результат быстрее и с меньшими затратами"
        ));
        waitSecond(MS);

        Subtask subtask4 = manager.addSubtask(new Subtask(
                null,
                "Выбрать наиболее оптимальный способ поиска",
                null,
                null,
                epic2.getId(),
                null,
                null
        ));
        waitSecond(MS);

        Epic epic3 = manager.addEpic(new Epic(
                null,
                "Создать систему оценки способов поиска",
                "Без статистики не обойтись"
        ));
        waitSecond(MS);

        Subtask subtask5 = manager.addSubtask(new Subtask(
                null,
                "Задать параметры системы оценки",
                "Возможно бальная система",
                null,
                epic3.getId(),
                null,
                null
        ));
        waitSecond(MS);

        Subtask subtask6 = manager.addSubtask(new Subtask(
                null,
                "Собрать статистику способов",
                null,
                null,
                epic3.getId(),
                null,
                null
        ));
        waitSecond(MS);

        Subtask subtask7 = manager.addSubtask(new Subtask(
                null,
                "Провести статистическое исследование",
                null,
                null,
                epic3.getId(),
                null,
                null
        ));
    }

    /// Вывести историю
    static void printHistory() {
        StringBuilder stringBuilder;
        System.out.println("История:");
        int i = 1;
        for (Task task : manager.getHistory()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(i).append(". ").append(task);
            System.out.println(stringBuilder);
            i++;
        }
        System.out.println("\n" + "-".repeat(20) + "\n");
    }

    /// Вывести все данные
    static void printListAll() {
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Вывести все что есть:" + "\n");
        System.out.println(getListAll());
        System.out.println("\n" + "-".repeat(20) + "\n");
    }

    /// Протестировать задачи
    static void testTasks() {
        System.out.println("Изменение статуса задачи:" + "\n");
        System.out.println(manager.updateTask(new Task(
                1,
                null,
                null,
                Status.IN_PROGRESS
        )));
        System.out.println("\n" + "-".repeat(20) + "\n");
        waitSecond(MS);

        System.out.println("Удаление задачи по идентификатору:" + "\n");
        System.out.println(removeTask(2));
        System.out.println("\n" + "-".repeat(20) + "\n");
        waitSecond(MS);

        System.out.println("Получение списка всех задач:" + "\n");
        System.out.println(getAllTasksList());
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Получение задачи по идентификатору:" + "\n");
        System.out.println(manager.getTask(3));
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Получение задачи по идентификатору:" + "\n");
        System.out.println(manager.getTask(1));
        System.out.println("\n" + "-".repeat(20) + "\n");
    }

    /// Протестировать подзадачи
    static void testSubtasks() {
        System.out.println("Изменение статуса подзадачи:" + "\n");
        System.out.println(manager.updateSubtask(new Subtask(
                5,
                null,
                null,
                Status.DONE,
                null,
                null,
                null
        )));
        System.out.println("\n" + "-".repeat(20) + "\n");
        waitSecond(MS);

        System.out.println("Изменение статуса подзадачи:" + "\n");
        System.out.println(manager.updateSubtask(new Subtask(
                9,
                null,
                null,
                Status.IN_PROGRESS,
                null,
                null,
                null
        )));
        System.out.println("\n" + "-".repeat(20) + "\n");
        waitSecond(MS);

        System.out.println("Удаление подзадачи по идентификатору:" + "\n");
        System.out.println(removeSubtask(6));
        System.out.println("\n" + "-".repeat(20) + "\n");
        waitSecond(MS);

        System.out.println("Получение списка всех подзадач:" + "\n");
        System.out.println(getAllSubtasksList());
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Получение подзадачи по идентификатору:" + "\n");
        System.out.println(manager.getSubtask(9));
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Получение подзадачи по идентификатору:" + "\n");
        System.out.println(manager.getSubtask(5));
        System.out.println("\n" + "-".repeat(20) + "\n");
    }

    /// Протестировать эпики
    static void testEpics() {
        System.out.println("Удаление описания эпика" + "\n");
        System.out.println(manager.updateEpic(new Epic(
                8,
                null,
                ""
        )));
        System.out.println("\n" + "-".repeat(20) + "\n");
        waitSecond(MS);

        System.out.println("Удаление эпика по идентификатору:" + "\n");
        System.out.println(removeEpic(8));
        System.out.println("\n" + "-".repeat(20) + "\n");
        waitSecond(MS);

        System.out.println("Получение эпика со списком подзадач:" + "\n");
        System.out.println(getEpicWithSubtasksList(10));
        System.out.println("-".repeat(20) + "\n");

        System.out.println("Получение списка всех эпиков:" + "\n");
        System.out.println(getAllEpicsList());
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Получение эпика по идентификатору:" + "\n");
        System.out.println(manager.getEpic(10));
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Получение списка всех подзадач определённого эпика:" + "\n");
        System.out.println(getAllSubtasksList(10));
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Удаление всех подзадач определённого эпика по идентификатору:" + "\n");
        System.out.println(removeAllEpicSubtasks(10));
        System.out.println("\n" + "-".repeat(20) + "\n");
    }

    /// Получение списка всего, что есть
    static StringBuilder getListAll() {
        StringBuilder list = new StringBuilder();
        list.append("Задачи:").append("\n").append(getAllTasksList()).append("\n");
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
        return list;
    }

    /// Получение списка всех задач
    static StringBuilder getAllTasksList() {
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
        return taskList;
    }

    /// Получение списка всех эпиков
    static StringBuilder getAllEpicsList() {
        StringBuilder epicList = new StringBuilder();
        List<Epic> epics = manager.getAllEpics();
        int i = 1;
        for (Epic epic : epics) {
            if (i == 1) {
                epicList.append(i).append(". ").append(epic);

            } else {
                epicList.append("\n").append(i).append(". ").append(epic);
            }
            i++;
        }
        return epicList;
    }

    /// Удаление задачи по идентификатору
    static String removeTask(int taskId) {
        Task task = manager.getTask(taskId);
        String removedTask = task.toString();
        manager.removeTask(taskId);
        return "removed" + removedTask;
    }

    /// Удаление подзадачи по идентификатору
    static String removeSubtask(int subtaskId) {
        Subtask subtask = manager.getSubtask(subtaskId);
        String removedSubtask = subtask.toString();
        manager.removeSubtask(subtaskId);
        return "removed" + removedSubtask;
    }

    /// Удаление эпика по идентификатору
    static String removeEpic(int epicId) {
        Epic epic = manager.getEpic(epicId);
        String removedEpic = epic.toString();
        manager.removeEpic(epicId);
        return "removed" + removedEpic;
    }

    /// Получение эпика со списком его подзадач
    static StringBuilder getEpicWithSubtasksList(int epicId) {
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
    static StringBuilder removeEpicWithSubtasks(int epicId) {
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
    static StringBuilder removeAllEpicSubtasks(int epicId) {
        StringBuilder list = new StringBuilder();
        List<Subtask> removedSudtasks = manager.getAllSubTasksByEpic(epicId);
        manager.removeAllSubtasksByEpic(epicId);

        for (Subtask subtask : removedSudtasks) {
            list.append("removed").append(subtask).append("\n");
        }

        return list;
    }

    /// Получение списка всех подзадач
    static StringBuilder getAllSubtasksList() {
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
    static StringBuilder getAllSubtasksList(int epicId) {
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

    static void printHelp() {
        System.out.println("\n" + "-".repeat(20) + "\n");
        System.out.println("Обновить данные --> update");
        System.out.println("Поиск по id --> find");
        System.out.println("Вывести весь список --> print");
        System.out.println("Добавить задачу --> add-task");
        System.out.println("Добавить эпик --> add-epic");
        System.out.println("Добавить подзадачу --> add-sub");
        System.out.println("Удалить --> remove");
        System.out.println("Изменить статус (задача или подзадача) --> set-status");
        System.out.println("Изменить дату старта --> set-time");
        System.out.println("Изменить срок (в минутах) --> set-dur");
        System.out.println("Показать историю --> history");
        System.out.println("Вывести инструкцию --> help");
        System.out.println("Выход --> exit");
        System.out.println("\n" + "-".repeat(20) + "\n");
    }

    static void start() {
        System.out.println("\n" + "-".repeat(20) + "\n");

        while (true) {
            System.out.println("Введите команду или help");
            System.out.print("--> ");

            switch (scanner.next()) {
                case "exit":
                    System.out.println("\n" + "-".repeat(20));
                    return;
                case "help":
                    printHelp();
                    break;
                case "update":
                    FileBackedTaskManager manager1 = FileBackedTaskManager
                            .getInstance(new File("src/autosave/data.csv"));
                    try {
                        manager1.updateDataFromFile();
                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Данные загружены.");
                    break;
                case "print":
                    printListAll();
                    break;
                case "history":
                    System.out.println("\n" + "-".repeat(20) + "\n");
                    printHistory();
                    break;
                case "find":
                    System.out.println("\n" + "-".repeat(20) + "\n");
                    System.out.print("id --> ");
                    int foundedId = Integer.parseInt(scanner.next());
                    Task findedTask = manager.get(foundedId);
                    System.out.println("\n" + "-".repeat(20) + "\n");
                    System.out.println(findedTask);
                    System.out.println("\n" + "-".repeat(20) + "\n");
                    break;
                case "add-task":
                    addTask();
                    break;
                case "add-epic":
                    addEpic();
                    break;
                case "add-sub":
                    addSub();
                    break;
                case "set-status":
                    setSt();
                    break;
                case "set-time":
                    setTime();
                    break;
                case "set-dur":
                    setDur();
                    break;
                case "remove":
                    remove();
                    break;

                default:
                    System.out.println("Такой команды нет");
                    System.out.println("\n" + "-".repeat(20) + "\n");
                    break;
            }
        }
    }

    static void setStatus(Task taskObject, Status status) {
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

    static void setDur() {
        Type type = null;
        while (true) {
            System.out.println("\n" + "-".repeat(20) + "\n");
            System.out.println("Назад --> 0");
            System.out.print("id --> ");
            int id = 0;
            try {
                id = Integer.parseInt(scanner.next());
                scanner.nextLine();
            } catch (NumberFormatException e) {
                System.out.println("Такого id не существует");
                continue;
            }
            if (id == 0) {
                System.out.println("\n" + "-".repeat(20) + "\n");
                break;
            }
            if (manager.getWithoutHistory(id) == null) {
                System.out.println("Такого id не существует");
                continue;
            }
            try {
                type = manager.getType(id);
            } catch (NullPointerException e) {
                System.out.println("Такого id не существует");
                continue;
            }
            if (manager.getType(id) == Type.EPIC) {
                System.out.println("Это id эпика - срок автоматический");
                continue;
            }

            System.out.print("Минуты --> ");
            int dur = Integer.parseInt(scanner.next());
            Duration duration = Duration.ofMinutes(dur);
            System.out.println("\n" + "-".repeat(20) + "\n");

            switch (type) {
                case TASK -> System.out.println(manager.updateTask(new Task(id, null, null, null, null, duration)));
                case SUBTASK ->
                        System.out.println(manager.updateSubtask((new Subtask(id, null, null, null, null, null, duration))));
            }
            System.out.println("\n" + "-".repeat(20) + "\n");
            break;
        }
    }

    static void setTime() {
        Type type = null;
        while (true) {
            System.out.println("\n" + "-".repeat(20) + "\n");
            System.out.println("Назад --> 0");
            System.out.print("id --> ");
            int id;
            try {
                id = Integer.parseInt(scanner.next());
                scanner.nextLine();
            } catch (NumberFormatException e) {
                System.out.println("Такого id не существует");
                continue;
            }
            if (id == 0) {
                System.out.println("\n" + "-".repeat(20) + "\n");
                break;
            }
            if (manager.getWithoutHistory(id) == null) {
                System.out.println("Такого id не существует");
                continue;
            }
            try {
                type = manager.getType(id);
            } catch (NullPointerException e) {
                System.out.println("Такого id не существует");
                continue;
            }
            if (manager.getType(id) == Type.EPIC) {
                System.out.println("Это id эпика - время автоматическиое");
                System.out.println("\n" + "-".repeat(20) + "\n");
                return;
            }
//            System.out.print("Год --> ");
//            int year = Integer.parseInt(scanner.next());
//            System.out.print("Месяц --> ");
//            int month = Integer.parseInt(scanner.next());
//            System.out.print("День --> ");
//            int day = Integer.parseInt(scanner.next());
            System.out.print("Часы --> ");
            int hours = Integer.parseInt(scanner.next());
            System.out.print("Минуты --> ");
            int minutes = Integer.parseInt(scanner.next());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hours, minutes);
            System.out.println("\n" + "-".repeat(20) + "\n");

            switch (type) {
                case TASK -> System.out.println(manager.updateTask(new Task(id, null, null, null, dateTime, null)));
                case SUBTASK ->
                        System.out.println(manager.updateSubtask((new Subtask(id, null, null, null, null, dateTime, null))));
            }
            System.out.println("\n" + "-".repeat(20) + "\n");
            break;
        }

    }

    static void remove() {
        Type rmType = null;
        while (true) {
            System.out.println("\n" + "-".repeat(20) + "\n");
            System.out.println("Назад --> 0");
            System.out.print("id --> ");
            int rmId = Integer.parseInt(scanner.next());
            if (rmId == 0) {
                System.out.println("\n" + "-".repeat(20) + "\n");
                break;
            }
            if (manager.getWithoutHistory(rmId) == null) {
                System.out.println("Такого id не существует");
                continue;
            }
            try {
                rmType = manager.getType(rmId);
            } catch (NullPointerException e) {
                System.out.println("Такого id не существует");
                continue;
            }
            System.out.println("\n" + "-".repeat(20) + "\n");
            switch (rmType) {
                case TASK -> System.out.println(removeTask(rmId));
                case EPIC -> System.out.println(removeEpic(rmId));
                case SUBTASK -> System.out.println(removeSubtask(rmId));
            }
            System.out.println("\n" + "-".repeat(20) + "\n");
            break;
        }
    }

    static void addTask() {
        System.out.println("\n" + "-".repeat(20) + "\n");
        System.out.print("Заголовок (без пробелов) --> ");
        String taskTitle = scanner.next();
        System.out.print("Описание (без пробелов) --> ");
        String taskDescription = scanner.next();
        Task newTsk = new Task(null, taskTitle, taskDescription, null);
        manager.addTask(newTsk);
        System.out.println("Задача добавлена");
        System.out.println(newTsk);
        System.out.println("\n" + "-".repeat(20) + "\n");
    }

    static void addEpic() {
        System.out.println("\n" + "-".repeat(20) + "\n");
        System.out.print("Заголовок (без пробелов) --> ");
        String epicTitle = scanner.next();
        System.out.print("Описание (без пробелов) --> ");
        String epicDescription = scanner.next();
        Epic newEp = new Epic(null, epicTitle, epicDescription);
        manager.addEpic(newEp);
        System.out.println("Эпик добавлен");
        System.out.println(newEp);
        System.out.println("\n" + "-".repeat(20) + "\n");
    }

    static void setSt() {
        System.out.println("\n" + "-".repeat(20) + "\n");
        System.out.print("id --> ");
        int statusesId = Integer.parseInt(scanner.next());
        Task statusesTask = manager.getWithoutHistory(statusesId);
        if (statusesTask == null) {
            System.out.println("id " + statusesId + " не существует");
            System.out.println("\n" + "-".repeat(20) + "\n");
            return;
        }
        if (manager.getType(statusesId) == Type.EPIC) {
            System.out.println("Это id эпика - статус автоматический");
            System.out.println("\n" + "-".repeat(20) + "\n");
            return;
        }
        while (true) {
            System.out.println("\n" + "-".repeat(20) + "\n");
            System.out.println("NEW --> 1");
            System.out.println("IN_PROGRESS --> 2");
            System.out.println("DONE --> 3");
            System.out.print("Новый статус № --> ");
            String statusNumber = scanner.next();
            switch (statusNumber) {
                case "1":
                    setStatus(statusesTask, Status.NEW);
                    System.out.println("Статус --> " + Status.NEW);
                    System.out.println(statusesTask);
                    break;
                case "2":
                    setStatus(statusesTask, Status.IN_PROGRESS);
                    System.out.println("Статус --> " + Status.IN_PROGRESS);
                    System.out.println(statusesTask);
                    break;
                case "3":
                    setStatus(statusesTask, Status.DONE);
                    System.out.println("Статус --> " + Status.DONE);
                    System.out.println(statusesTask);
                    break;
                default:
                    System.out.println(statusNumber + " - Неверный номер");
                    continue;
            }
            System.out.println("\n" + "-".repeat(20) + "\n");
            break;
        }
    }

    static void addSub() {
        int epiccId;
        while (true) {
            System.out.println("\n" + "-".repeat(20) + "\n");
            System.out.println("Эпики:");
            System.out.println(getAllEpicsList());
            System.out.println("Назад --> 0");
            System.out.print("id эпика --> ");
            epiccId = Integer.parseInt(scanner.next());
            if (epiccId == 0) {
                System.out.println("\n" + "-".repeat(20) + "\n");
                return;
            }

            if (manager.getWithoutHistory(epiccId) == null) {
                System.out.println("Такого id эпика нет");
                continue;
            }

            System.out.println("\n" + "-".repeat(20) + "\n");
            System.out.print("Заголовок (без пробелов) --> ");
            String subtaskTitle = scanner.next();
            System.out.print("Описание (без пробелов) --> ");
            String subtaskDescription = scanner.next();
            Subtask newSub = new Subtask(null, subtaskTitle, subtaskDescription, null, epiccId, null, null);
            manager.addSubtask(newSub);
            System.out.println("Подзадача добавлена");
            System.out.println(newSub);
            System.out.println("\n" + "-".repeat(20) + "\n");
            return;
        }
    }

    static void waitSecond(int ms) {
        try {
            Thread.sleep(ms); // Пауза на 1000 миллисекунд (1 секунду)
        } catch (InterruptedException e) {
            // Обработка исключения, которое может возникнуть, если поток будет прерван
            e.printStackTrace();
        }
    }
}