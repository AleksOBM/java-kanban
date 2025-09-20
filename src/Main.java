import data.Epic;
import data.Status;
import data.Subtask;
import data.Task;
import manager.Managers;
import manager.TaskManager;
import manager.TaskManagerType;

import java.util.ArrayList;

public class Main {

    static TaskManager manager = Managers.getTaskManager(TaskManagerType.IN_MEMORY);

    public static void main(String[] args) {

        insertStartData();

        printListAll();

        testSubtasks();
        testEpics();
        testTasks();

        printListAll();

        printHistory();
    }

    /// Ввести исходные данные
    static void insertStartData() {
        Task task1 = manager.setTask(new Task(
                null,
                "Познакомиться с девушкой",
                "Она должна понравиться моей маме"
        ));

        Task task2 = manager.setTask(new Task(
                null,
                "Сводить девушку на свидание",
                "По правилам этикета нужно заплатить за нее в ресторане"
        ));
        Task task3 = manager.setTask(new Task(
           null,
           "Подарить девушке цветы",
           "Не дарить хризантемы"
        ));

        Epic epic1 = manager.setEpic(new Epic(
                null,
                "Найти девушку для знакомства",
                "Использовать различные способы поиска, для ускорения процесса"
        ));
        Subtask subtask1 = manager.setSubTask(new Subtask(
                null,
                "Поиск на сайтах знакомств",
                null,
                null,
                epic1.getId()
        ));
        Subtask subtask2 = manager.setSubTask(new Subtask(
                null,
                "Поиск через знакомых",
                null,
                null,
                epic1.getId()
                ));
        Subtask subtask3 = manager.setSubTask(new Subtask(
                null,
                "Поиск в литературном клубе",
                null,
                null,
                epic1.getId()
        ));

        Epic epic2 = manager.setEpic(new Epic(
                null,
                "Оптимизировать поиск девушки",
                "Получить результат быстрее и с меньшими затратами"
                ));
        Subtask subtask4 = manager.setSubTask(new Subtask(
                null,
                "Выбрать наиболее оптимальный способ поиска",
                null,
                null,
                epic2.getId()
                ));

        Epic epic3 = manager.setEpic(new Epic(
                null,
                "Создать систему оценки способов поиска",
                "Без статистики не обойтись"
                ));
        Subtask subtask5 = manager.setSubTask(new Subtask(
                null,
                "Задать параметры системы оценки",
                "Возможно бальная система",
                null,
                epic3.getId()
                ));
        Subtask subtask6 = manager.setSubTask(new Subtask(
                null,
                "Собрать статистику способов",
                null,
                null,
                epic3.getId()
        ));
        Subtask subtask7 = manager.setSubTask(new Subtask(
                null,
                "Провести статистическое исследование",
                null,
                null,
                epic3.getId()
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

        System.out.println("Удаление задачи по идентификатору:" + "\n");
        System.out.println(removeTask(2));
        System.out.println("\n" + "-".repeat(20) + "\n");

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
                null
        )));
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Изменение статуса подзадачи:" + "\n");
        System.out.println(manager.updateSubtask(new Subtask(
                9,
                null,
                null,
                Status.IN_PROGRESS,
                null
        )));
        System.out.println("\n" + "-".repeat(20) + "\n");

        System.out.println("Удаление подзадачи по идентификатору:" + "\n");
        System.out.println(removeSubtask(6));
        System.out.println("\n" + "-".repeat(20) + "\n");

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

        System.out.println("Удаление эпика по идентификатору:" + "\n");
        System.out.println(removeEpic(8));
        System.out.println("\n" + "-".repeat(20) + "\n");

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
        ArrayList<Epic> epics = manager.getAllEpics();
        int epicIndex = 1;
        for (Epic epic: epics) {
            list.append("\n").append("Эпик ").append(epicIndex).append(":\n");
            list.append(epic).append("\n");
            ArrayList<Subtask> subtasks = manager.getAllSubTasksByEpic(epic.getId());
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
        ArrayList<Task> tasks = manager.getAllTasks();
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
        ArrayList<Epic> epics = manager.getAllEpics();
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
        ArrayList<Subtask> subtasks = manager.getAllSubTasksByEpic(epic.getId());
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
        ArrayList<Subtask> removedSudtasks = manager.getAllSubTasksByEpic(epicId);
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
        Epic epic = manager.getEpic(epicId);
        ArrayList<Subtask> removedSudtasks = manager.getAllSubTasksByEpic(epicId);
        manager.removeAllSubtasksByEpic(epicId);

        for (Subtask subtask : removedSudtasks) {
            list.append("removed").append(subtask).append("\n");
        }

        return list;
    }

    /// Получение списка всех подзадач
    static StringBuilder getAllSubtasksList() {
        StringBuilder subtaskList = new StringBuilder();
        ArrayList<Subtask> subtasks = manager.getAllSubtasks();
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
            return  null;
        }
        StringBuilder subtaskList = new StringBuilder();
        ArrayList<Subtask> thisSubtasks = manager.getAllSubTasksByEpic(epic.getId());
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