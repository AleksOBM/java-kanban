package data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    static Task task1 = new Task(1, "This is task1", "This is description of task1");
    static Task task2 = new Task(2, "This is task1", "This is description of task1");
    static Task task3 = new Task(1, "This is task2", "This is description of task2", Status.DONE);

    @Test
    void shouldBeEqualsWhenIdIsSame() {
        assertEquals(task1, task3, "Задачи с одинаковым ID не равны по equals");
    }

    @Test
    void shouldHashcodeBeEqualsWhenIdIsSame() {
        assertEquals(task1.hashCode(), task3.hashCode(), "Задачи с одинаковым ID имеют разный hashCode");
    }

    @Test
    void shouldBeNotEqualsWhenIdIsDifferent() {
        assertNotEquals(task1, task2,"Задачи с разным ID равны по equals");
    }

    @Test
    void shouldHashcodeBeNotEqualsWhenIdIsDifferent() {
        assertNotEquals(task1.hashCode(), task2.hashCode(), "Задачи с разным ID имеют одинаковый hashCode");
    }

    @Test
    void shouldReturnCorrectStringWhenGetToString() {
        assertEquals("Task{id='1', " +
                        "title='This is task2', " +
                        "description='This is description of task2', " +
                        "status=DONE}",
                task3.toString(),
                "Задача возвращает некорректную строку при вызове toString.");
    }

}
