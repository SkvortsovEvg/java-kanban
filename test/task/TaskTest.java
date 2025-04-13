package task;

import enums.TaskStatus.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task(10, "Купить хлеб", "В Радеже", Status.NEW);
        Task task2 = new Task(10, "Купить молоко", "В Пятерочке", Status.DONE);
        assertEquals(task1, task2, "Tasks должны быть равны, т.к. равны их id");
    }
}
