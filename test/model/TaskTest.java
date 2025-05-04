package model;

import task.Task;
import enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("task")
class TaskTest {

    @Test
    @DisplayName("Экземпляры равны при равных id")
    void shouldEqualsIfIdEquals() {
        Task task = new Task("name", TaskStatus.NEW, "desc");
        Task taskExpected = new Task("name1", TaskStatus.NEW, "desc");
        task.setId(1);
        taskExpected.setId(1);
        assertEquals(taskExpected, task, "Таски должны совпадать");
    }

    @Test
    void fromString() {
        Task original = new Task("Test", TaskStatus.NEW, "Test description");
        original.setId(1); // ID важен для equals

        String csv = original.toCSV();
        Task parsed = Task.fromString(csv);

        assertInstanceOf(Task.class, parsed, "не является экземпляром Task");
        assertEquals(original, parsed, "не восстановлен корректно из строки");
    }
}