package model;

import task.*;
import enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("SubTask")
class SubtaskTest {
    @Test
    @DisplayName("Экземпляры равны при равных id")
    void shouldEqualsIfIdEquals() {
        Subtask task = new Subtask("name", TaskStatus.NEW, "desc");
        Subtask taskExpected = new Subtask("name1", TaskStatus.NEW, "desc");
        task.setId(1);
        taskExpected.setId(1);
        assertEquals(taskExpected, task, "Сабтаски должны совпадать");
    }

    @Test
    void fromString() {
        Task from = new Subtask("Test", TaskStatus.NEW, "Test description");
        Task to = Task.fromString(from.toCSV());
        assertInstanceOf(Subtask.class, to, "не является экземпляром класса SubTask");
        assertEquals(from, to, "не является копией исходника");
    }
}