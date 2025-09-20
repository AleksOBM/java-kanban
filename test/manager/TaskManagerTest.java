package manager;

import data.Epic;
import data.Status;
import data.Subtask;
import data.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest<T extends Task> {

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

    void updateTaskHistory(ArrayList<T> history) {
        manager.getTask(taskId);
        history.clear();
        history.addAll((ArrayList<? extends T>) manager.getHistory());
    }

    @BeforeEach
    void beforeEach() {
        // Создать новый менеджер задач
        manager = Managers.getTaskManager(TaskManagerType.IN_MEMORY);

        // Создать новую задачу
        task = new Task(
                777,
                "Test addNewTask title",
                "Test addNewTask description"
        );

        // Добавить задачу в базу и получить идентификатор добавленной задачи
        taskId = manager.setTask(task).getId();

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
        epicId = manager.setEpic(epic).getId();

        // Добавить первую подзадачу в созданный эпик
        manager.setSubTask(new Subtask(
                777,
                "This is first Subtask",
                "This is description of first Subtask",
                Status.IN_PROGRESS,
                epicId
        ));

        // Добавить вторую подзадачу в созданный эпик
        manager.setSubTask(new Subtask(
                null,
                "This is second Subtask",
                "This is description of second Subtask",
                null,
                null
        ));

        // Добавить третью подзадачу в созданный эпик
        manager.setSubTask(new Subtask(
                null,
                "This is third Subtask",
                "This is description of third Subtask",
                Status.DONE,
                epicId
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
        ArrayList<T> history = (ArrayList<T>) manager.getHistory();
        assertEquals(3, history.size(), "Неверное количество задач в истории.");
    }

    @Test
    void shouldBeEqualsCreatedTaskAndTaskOfHistoryList() {
        ArrayList<T> history = (ArrayList<T>) manager.getHistory();
        assertEquals(task, history.getFirst(), "Созданная задача не соответствует задаче из истории");
    }

    @Test
    void shouldBeTwoElementOfHistoryListAfterUpdateAndGet() {
        ArrayList<T> history = (ArrayList<T>) manager.getHistory();
        updateTaskHistory(history);
        assertEquals(4, history.size(), "Неверное количество задач в истории.");
    }

    @Test
    void shouldBeStatusEqualsOfCreatedTaskAndFirstTaskOfHistoryListAfterUpdateAndGet() {
        ArrayList<T> history = (ArrayList<T>) manager.getHistory();
        updateTaskHistory(history);
        assertEquals(task.getStatus(), history.getFirst().getStatus(),
                "Статус созданной задачи не соответствует статусу первой задачи из истории."
        );
    }

    @Test
    void shouldBeStatusEqualsOfSavedTaskAndLastTaskOfHistoryListAfterUpdateAndGet() {
        ArrayList<T> history = (ArrayList<T>) manager.getHistory();
        updateTaskHistory(history);
        assertEquals(savedTask.getStatus(), history.getLast().getStatus(),
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
        assertEquals("HelloWorld!", savedEpic.getTitle(), "Менеджер неправильно обновляет эпик.");
    }

    @Test
    void shouldBeNotNullListSubtasksOfSavedEpic() {
        assertNotNull(savedEpic.getSubtaskIds(), "Сохраненный эпик не возвращает идентификаторы подзадач.");
    }

    @Test
    void shouldBeTwoSubtaskIdsContainsOfSavedEpicAfterRemoveSubtask() {
        manager.removeSubtask(3);
        assertEquals(1, savedEpic.getSubtaskIds().size(),
                "Сохраненный эпик возвращает неверное количество идентификаторов подзадач " +
                        "после удаления подзадачи.");
    }

    @Test
    void shouldBeIsEmptySubtaskListAfterRemoveAllSubtasks() {
        manager.removeAllSubtasksByEpic(epicId);
        assertEquals("[]", savedEpic.getSubtaskIds().toString(),
                "Список идентификаторов подзадач эпика должен быть пуст после удаления всех подзадач эпика.");
    }

    @Test
    void shouldContainsTwoElementsInSubtasksList() {
        assertEquals(2, subtasks.size(), "Список всех подзадач возвращает не верное количество.");
    }

    @Test
    void shouldReturnNullWhenAddingSubtaskWithMissingId() {
        manager.setSubTask(new Subtask(null, "Hello", "World", null, 777));
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
                "Менеджер возвращает не верное количество задач при вызове ");
    }

    @Test
    void shouldUpdateEpicStatusWhenAllHimSubtasksIsDone() {
        manager.updateSubtask(new Subtask(3, null, null, Status.DONE, null));
        assertEquals(Status.DONE, savedEpic.getStatus(),
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
        assertEquals(Status.NEW, manager.getEpic(2).getStatus(),
                "Менеджер не присваивает эпикам статус NEW после очистки главного хранилища подзадач.");
    }

    @Test
    void shouldReturnNullAfterRemoveAllEpics() {
        manager.removeAllEpics();
        assertEquals("[]", manager.getAllSubtasks().toString(),
                "Менеджер не очищает хранилище подзадач после очистки хранилища эпиков.");
    }
}
