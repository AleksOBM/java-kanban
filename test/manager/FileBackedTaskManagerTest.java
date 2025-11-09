package manager;

import data.Epic;
import data.Status;
import data.Subtask;
import data.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FileBackedTaskManagerTest extends TaskManagerTest {

    static TaskManager manager2;

    static File file;

    @AfterEach
    void afterEach() {
        file.deleteOnExit();
    }

    @Override
    @BeforeEach
    void beforeEach() {
        // Создать новый темп-файл
        try {
            file = File.createTempFile(".test", ".csv", null);
        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }

        // Создать новый менеджер задач
        manager = new FileBackedTaskManager(file);

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
    void shouldReturnFourWhenGetAllTaskCountFromFile() {
        manager2 = new FileBackedTaskManager(file);
        assertEquals(4, manager2.getAll().size(),
                "Менеджер загрузил не верное количество задач из файла");
    }

    @Test
    void shouldReturnActualStatusesAfterUpdateDataFromFile() {
        manager.removeTask(1);
        manager.addTask(new Task(null, "LastTask", "eee", Status.NEW));
        manager2 = new FileBackedTaskManager(file);
        StringBuilder statuses = new StringBuilder();
        for (Task taskObject : manager2.getAll()) {
            statuses.append(taskObject.getStatus()).append(",");
        }
        statuses.deleteCharAt(statuses.length() - 1);

        assertEquals("IN_PROGRESS,IN_PROGRESS,DONE,NEW", statuses.toString(),
                "Менеджер неправильно загружает статусы из файла"
        );
    }

    @Test
    void shouldReturnNullWhenGetFakeTaskAfterThisAdding() {
        // Фэйковая задача
        Task subtask = new Subtask(
                null,
                "last subtask",
                null,
                null,
                2,
                null,
                null
        );
        manager.addTask(subtask);
        assertNull(manager.get(5), "Менеджер сохранил фэйковую задачу.");
    }

}