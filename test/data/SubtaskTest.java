package data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    static Subtask subtask;

    @BeforeEach
    void addSubtask() {
        subtask = new Subtask(
                1,
                "this is Subtask",
                "This is Subtask description",
                null,
                null,
                null,
                null
        );
    }

    @Test
    void shouldReturnCorrectStringWhenGetToString() {
        assertEquals("Subtask{" +
                        "id='1', " +
                        "epicId='null', " +
                        "title='this is Subtask', " +
                        "description='This is Subtask description', " +
                        "status='null'" +
                        ", startTime='null'" +
                        ", endTime='null'" +
                        ", duration='0'" +
                        "}",
                subtask.toString(),
                "Подзадача возвращает некорректную строку при вызове toString.");
    }
}
