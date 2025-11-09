package manager;

import data.Epic;
import data.Status;
import data.Subtask;
import data.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    static TaskManager manager;

    static Task task;
    static int taskId;
    static Task savedTask;
    static List<Task> tasks;

    static Epic epic;
    static int epicId;
    static Epic savedEpic;
    static List<Epic> epics;

    static List<Subtask> subtasks;

    void updateTaskHistory(List<Task> history) {
        manager.getTask(taskId);
        history.clear();
        history.addAll(manager.getHistory());
    }

    @BeforeEach
    void beforeEach() {
        // Создать новый менеджер задач
        manager = Managers.getTaskManager(TaskManagerType.IN_MEMORY);

        // Создать новую задачу
        task = new Task(
                777,
                "Test addNewTask title",
                "Test addNewTask description",
                null,
                LocalDateTime.of(2025, 11, 11, 11, 11),
                Duration.ofMinutes(30)
        );

        // Добавить задачу в базу и получить идентификатор добавленной задачи
        assertNotNull(manager);
        taskId = manager.addTask(task).getId();

        // Получить задачу из базы по идентификатору
        savedTask = manager.getTask(taskId);

        // Получить список всех задач
        tasks = manager.getAllTasks();

        // Изменить созданную задачу
        manager.updateTask(new Task(
                taskId,
                null,
                null,
                Status.IN_PROGRESS
        ));

        // Создать новый эпик
        epic = new Epic(
                777,
                "Test addNewEpic title",
                "Test addNewEpic description"
        );

        // Добавить эпик в базу и получить идентификатор добавленного эпика
        epicId = manager.addEpic(epic).getId();

        // Добавить первую подзадачу в созданный эпик
        manager.addSubtask(new Subtask(
                777,
                "This is first Subtask",
                "This is description of first Subtask",
                Status.IN_PROGRESS,
                epicId,
                null,
                null
        ));

        // Добавить вторую подзадачу в созданный эпик
        manager.addSubtask(new Subtask(
                null,
                "This is third Subtask",
                "This is description of third Subtask",
                Status.DONE,
                epicId,
                null,
                null
        ));

        // Получить все подзадачи
        subtasks = manager.getAllSubtasks();

        // Получить эпик из базы по идентификатору
        savedEpic = manager.getEpic(epicId);

        // Получить список всех эпиков
        epics = manager.getAllEpics();

        // Получить подзадачу по ID
        manager.getSubtask(4);

    }

    @Test
    void shouldBeNotNullAfterGet() {
        assertNotNull(savedTask, "Задача не найдена.");
    }

    @Test
    void shouldBeEqualsCreatedAndSavedTasks() {
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void shouldBeNotNullListAllTasks() {
        assertNotNull(tasks, "Задачи не возвращаются.");
    }

    @Test
    void shouldBeOneElementOfListAllTasks() {
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void shouldBeEqualsCreatedTaskAndTaskOfListAllTasks() {
        assertEquals(task, tasks.getFirst(), "Созданная задача не соответствует задаче из списка.");
    }

    @Test
    void shouldBeOneElementOfHistoryList() {
        List<Task> history = new ArrayList<>(manager.getHistory());
        assertEquals(3, history.size(), "Неверное количество задач в истории.");
    }

    @Test
    void shouldBeEqualsCreatedTaskAndTaskOfHistoryList() {
        List<Task> history = new ArrayList<>(manager.getHistory());
        assertEquals(task, history.getFirst(), "Созданная задача не соответствует задаче из истории");
    }

    @Test
    void shouldBeTwoElementOfHistoryListAfterUpdateAndGet() {
        List<Task> history = new ArrayList<>(manager.getHistory());
        updateTaskHistory(history);
        assertEquals(3, history.size(), "Неверное количество задач в истории.");
    }

    @Test
    void shouldBeStatusEqualsOfCreatedTaskAndFirstTaskOfHistoryListAfterUpdateAndGet() {
        manager.updateTask(new Task(1, null, null, Status.NEW));
        List<Task> history = new ArrayList<>(manager.getHistory());
        updateTaskHistory(history);
        Task testedTask = manager.getTask(1);
        assertEquals(testedTask.getStatus(), history.getLast().getStatus(),
                "Статус созданной задачи не соответствует статусу последней задачи из истории."
        );
    }

    @Test
    void shouldBeStatusEqualsOfSavedTaskAndLastTaskOfHistoryListAfterUpdateAndGet() {
        List<Task> history = new ArrayList<>(manager.getHistory());
        updateTaskHistory(history);
        assertEquals(manager.getTask(1).getStatus(), history.getLast().getStatus(),
                "Статус сохраненной задачи не соответствует статусу последней задачи из истории."
        );
    }

    @Test
    void shouldBeNotNullAfterGetEpic() {
        assertNotNull(savedEpic, "Эпик не найден.");
    }

    @Test
    void shouldBeEqualsCreatedAndReceivedEpics() {
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void shouldBeNotNullListAllEpics() {
        assertNotNull(epics, "Эпики не возвращаются.");
    }

    @Test
    void shouldBeOneElementOfListAllEpics() {
        assertEquals(1, epics.size(), "Получено неверное количество эпиков.");
    }

    @Test
    void shouldBeEqualsCreatedEpicAndTaskOfListAllEpics() {
        assertEquals(epic, epics.getFirst(), "Созданный эпик не соответствует эпику из списка.");
    }

    @Test
    void shouldUpdateEpicTitleWhenUpdateEpic() {
        manager.updateEpic(new Epic(2, "HelloWorld!", null));
        assertEquals("HelloWorld!", manager.getEpic(2).getTitle(),
                "Менеджер неправильно обновляет эпик.");
    }

    @Test
    void shouldReturnTwoAfterSetNewIdOfCashedEpic() {
        manager.getEpic(2).setId(3);
        int testEpicId = manager.getAllEpics().getLast().getId();
        assertEquals(2, testEpicId,
                "Изменение эпика через сеттер влияет на эпик хранящийся в менеджере");
    }

    @Test
    void shouldBeNotNullListSubtasksOfSavedEpic() {
        assertNotNull(savedEpic.getSubtaskIds(), "Сохраненный эпик не возвращает идентификаторы подзадач.");
    }

    @Test
    void shouldBeTwoSubtaskIdsContainsOfSavedEpicAfterRemoveSubtask() {
        manager.removeSubtask(3);
        Epic testedEpic = manager.getEpic(2);
        assertEquals(1, testedEpic.getSubtaskIds().size(),
                "Сохраненный эпик возвращает неверное количество идентификаторов подзадач " +
                        "после удаления подзадачи.");
    }

    @Test
    void shouldBeIsEmptySubtaskListAfterRemoveAllSubtasks() {
        manager.removeAllSubtasksByEpic(epicId);
        Epic testedEpic = manager.getEpic(epicId);
        assertEquals("[]", testedEpic.getSubtaskIds().toString(),
                "Список идентификаторов подзадач эпика должен быть пуст после удаления всех подзадач эпика.");
    }

    @Test
    void shouldContainsTwoElementsInSubtasksList() {
        assertEquals(2, subtasks.size(), "Список всех подзадач возвращает не верное количество.");
    }

    @Test
    void shouldReturnStatusNewWhenAllSubtasksInStatusesNull() {
        manager.addSubtask(new Subtask(
                null,
                null,
                null,
                null,
                2,
                null,
                null
        ));
        manager.addSubtask(new Subtask(
                null,
                null,
                null,
                null,
                2,
                null,
                null
        ));
        manager.removeSubtask(3);
        manager.removeSubtask(4);
        assertEquals(Status.NEW, manager.getEpic(2).getStatus(),
                "Эпик не выставляет статус NEW когда все подзадачи в статусе null");
    }

    @Test
    void shouldReturnNullWhenAddingSubtaskWithMissingId() {
        try {
            manager.addSubtask(new Subtask(
                    null,
                    "Hello",
                    "World",
                    null,
                    777,
                    null,
                    null
            ));
        } catch (NullPointerException e) {
            System.out.print("");
        }
        assertEquals(2, subtasks.size(), "Подзадача добавляется в несуществующий эпик.");
    }

    @Test
    void shouldEqualsTwoWhenRemoveAllSubtaskWithMissingEpicId() {
        manager.removeAllSubtasksByEpic(777);
        assertEquals(2, subtasks.size(),
                "Менеджер удаляет подзадачи несуществующего эпика"
        );
    }

    @Test
    void shouldTwoWhenGetAllSubtasksByEpic() {
        assertEquals(2, manager.getAllSubTasksByEpic(2).size(),
                "Менеджер возвращает не верное количество подзадач при вызове всех подзадач эпика");
    }

    @Test
    void shouldReturnThreeAndFourWhenGetAllSubtasksIdsByEpic() {
        Epic testEpic = manager.getEpic(2);
        assertEquals(3, testEpic.getSubtaskIds().getFirst());
        assertEquals(4, testEpic.getSubtaskIds().getLast(),
                "Внутри эпиков остаются неактуальные id подзадач.");
    }

    @Test
    void shouldReturnNullWhenTryingToGetAllSubtasks() {
        assertNull( manager.getAllSubTasksByEpic(100),
                "Менеджер не возвращает null при попытке получения всех подзадач несуществующего эпика");
    }

    @Test
    void shouldUpdateEpicStatusWhenAllHimSubtasksIsDone() {
        manager.updateSubtask(new Subtask(
                3,
                null,
                null,
                Status.DONE,
                null,
                null,
                null
        ));
        assertEquals(Status.DONE, manager.getAllEpics().getLast().getStatus(),
                "Статус эпика не обновляется когда все его подзадачи завершены");
    }

    @Test
    void shouldReturnNullWhenGettingRemovedTask() {
        manager.removeTask(1);
        assertNull(manager.getTask(1), "Менеджер не удаляет задачу.");
    }

    @Test
    void shouldReturnNullWhenGettingRemovedSubtask() {
        manager.removeSubtask(4);
        assertNull(manager.getSubtask(4), "Менеджер не удаляет подзадачу.");
    }

    @Test
    void shouldReturnNullWhenGettingRemovedEpic() {
        manager.removeEpic(2);
        assertNull(manager.getEpic(2), "Менеджер не удаляет эпик.");
    }

    @Test
    void shouldReturnEmptyAfterRemoveAllSubtasks() {
        manager.removeAllSubTasks();
        assertEquals("[]", manager.getAllSubtasks().toString(),
                "Менеджер не очищает все подзадачи.");
    }

    @Test
    void shouldReturnEpicStatusNewAfterRemoveAllSubtasks() {
        manager.removeAllSubTasks();
        Epic testedEpic = manager.getEpic(2);
        assertEquals(Status.NEW, testedEpic.getStatus(),
                "Менеджер не присваивает эпикам статус NEW после очистки главного хранилища подзадач.");
    }

    @Test
    void shouldReturnNullAfterRemoveAllEpics() {
        manager.removeAllEpics();
        assertEquals("[]", manager.getAllSubtasks().toString(),
                "Менеджер не очищает хранилище подзадач после очистки хранилища эпиков.");
    }

    @Test
    void shouldReturnTwoWhenAllTasksIsDeleted() {
        manager.removeAllTasks();
        assertEquals(2, manager.getHistory().getFirst().getId(),
                "При удалении всех задач они не удаляются из истории"
                );
    }

    @Test
    void shouldReturnListWithOneElementWhenAllEpicsIsDeleted() {
        manager.removeAllEpics();
        assertEquals(1, manager.getHistory().size(),
                "При удалении всех эпиков эпики не удаляются из истории с их подзадачами"
        );
    }

    @Test
    void shouldReturnTwoWhenAllSubtasksIsDeleted() {
        manager.removeAllSubTasks();
        assertEquals(2, manager.getHistory().size(),
                "При удалении всех подзадач они не удаляются из истории"
        );
    }

    @Test
    void shouldReturnTwoWhenAllSubtasksByEpicIsDeleted() {
        manager.addEpic(new Epic(null, null, null));
        manager.addSubtask(new Subtask(
                null,
                null,
                null,
                null,
                5,
                null,
                null
        ));
        manager.getSubtask(6);
        manager.removeAllSubtasksByEpic(2);
        assertEquals(3, manager.getHistory().size(),
                "При удалении всех подзадач эпика они не удаляются из истории"
        );
    }

    @Test
    void shouldReturnNullWhenAddIntersectionTasks() {
        assertNull(manager.addTask(new Task(null, null, null, null,
                LocalDateTime.parse("2025-11-11T10:30"), Duration.ofMinutes(50))),
                "Менеджер добавляет пересекающиеся задачи по верхней границе.");

        assertNull(manager.addTask(new Task(null, null, null, null,
                LocalDateTime.parse("2025-11-11T11:15"), Duration.ofMinutes(10))),
                "Менеджер добавляет пересекающиеся задачи внутрь временного контура.");

        assertNull(manager.addTask(new Task(null, null, null, null,
                LocalDateTime.parse("2025-11-11T11:30"), Duration.ofMinutes(50))),
                "Менеджер добавляет пересекающиеся задачи по нижней границе.");
    }

}
