package task;

import enums.TaskStatus.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    @Test
    public void EpicsWithEqualIdShouldBeEqual() {
        Epic epic1 =
                new Epic(5, "Первое важное дело", "Не стоит забывать об этом деле", Status.NEW);
        Epic epic2 =
                new Epic(5, "Второе важное дело", "Наполеон делал и то больше", Status.IN_PROGRESS);
        assertEquals(epic1, epic2, "Наследники класса Task должны быть равны друг другу по id!");
    }
}
