package data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    static Epic epic;

    @BeforeEach
    void addSubtasks() {
        epic = new Epic(1, "This is epic", "This is description of epic");
        epic.addSubtaskId(2);
        epic.addSubtaskId(3);
        epic.addSubtaskId(4);
    }

    @Test
    void shouldNotNullSubtaskList() {
        assertNotNull(epic.getSubtaskIds(),
                "Эпик не возвращает список идентификаторов подзадач.");
    }

    @Test
    void shouldReturnThreeWhenGetSizeOfSubtaskList() {
        assertEquals(3, epic.getSubtaskIds().size(),
                "Эпик возвращает неверное количество подзадач после их внесения");
    }

    @Test
    void shouldReturnTwoWhenOneSubtasksRemoved() {
        epic.removeSubtaskId(3);
        assertEquals(2, epic.getSubtaskIds().size(),
                "Эпик возвращает неверное количество подзадач после удаления одной");
    }

    @Test
    void shouldReturnFalseAfterAddingAndDeletingDuplicate() {
        epic.addSubtaskId(3);
        epic.removeSubtaskId(3);
        assertFalse(epic.getSubtaskIds().contains(3),
                "Эпик сохраняет дубликаты идентификаторов подзадач.");
    }

    @Test
    void shouldReturnFalseAfterOneSubtasksRemovedAndGets() {
        epic.removeSubtaskId(3);
        assertFalse(epic.getSubtaskIds().contains(3),
                "Удаленный идентификатор подзадачи остается в эпике после удаления.");
    }

    @Test
    void shouldBeEmptyAfterRemoveAllSubtasks() {
        epic.removeAllSubTaskIds();
        assertEquals("[]", epic.getSubtaskIds().toString(),
                "Эпик не удаляет все подзадачи одной командой."
                );
    }

    @Test
    void shouldReturnCorrectStringWhenGetToString() {
        assertEquals("Epic{id='1', " +
                        "title='This is epic', " +
                        "description='This is description of epic', " +
                        "status='null'}",
                epic.toString(),
                "Эпик возвращает некорректную строку при вызове toString.");
    }
}