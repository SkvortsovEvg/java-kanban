package task;

import enums.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    @Test
    public void SubtasksWithEqualIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(5, "Купить хлеб", "В Радеже", Status.NEW, 5);
        Subtask subtask2 = new Subtask(5, "Купить молоко", "В Пятерочке", Status.DONE, 5);
        assertEquals(subtask1, subtask2,
                "Наследники класса Task должны быть равны друг другу, если равен их id;");
    }
}
