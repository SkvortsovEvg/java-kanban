package model;

import task.*;
import enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("SubTask")
class SubTaskTest {
    @Test
    @DisplayName("Экземпляры равны при равных id")
    void shouldEqualsIfIdEquals() {
        SubTask task = new SubTask("name", TaskStatus.NEW, "desc");
        SubTask taskExpected = new SubTask("name1", TaskStatus.NEW, "desc");
        task.setId(1);
        taskExpected.setId(1);
        assertEquals(taskExpected, task, "Сабтаски должны совпадать");
    }

    @Test
    void fromString() {
        Task from = new SubTask("Test", TaskStatus.NEW, "Test description");
        Task to = Task.fromString(from.toCSV());
        assertInstanceOf(SubTask.class, to, "не является экземпляром класса SubTask");
        assertEquals(from, to, "не является копией исходника");
    }
}