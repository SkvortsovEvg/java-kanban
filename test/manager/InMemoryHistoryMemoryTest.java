package manager;

import enums.Status;
import manager.TaskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InMemoryHistoryMemoryTest {
    private TaskManager taskManager;
    private final static int MAX_HISTORY_STORAGE = 10;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void getHistoryShouldReturnListOf10Tasks() {
        for (int i = 0; i < 20; i++) {
            taskManager.addTask(new Task("Some name " + (i + 1), "Some description " + (i + 1)));
        }
        List<Task> tasks = taskManager.getTasks();
        for (Task task : tasks) {
            taskManager.getTaskById(task.getId());
        }
        List<Task> history = taskManager.getHistory();
        assertEquals(MAX_HISTORY_STORAGE, history.size(),
                "Помните, что история должна содержать максимум " + MAX_HISTORY_STORAGE + " задач.");
    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate(){
        Task cleanFloor = new Task("Пропылесосить полы", "Попробовать робота-пылесоса");
        taskManager.addTask(cleanFloor);
        taskManager.getTaskById(cleanFloor.getId());
        taskManager.updateTask(
                new Task(cleanFloor.getId(),
                        "Очень важно!!!",
                        "Поменять щетки на пылесосе",
                        Status.IN_PROGRESS));

        List<Task> history = taskManager.getHistory();
        Task oldTask = history.getFirst();
        assertEquals(cleanFloor.getName(), oldTask.getName(), "В истории не сохранилась старая версия задачи");

        assertEquals(cleanFloor.getDescription(),
                oldTask.getDescription(),
                "В истории не сохранилась старая версия задачи");
    }

    @Test
    public void getHistoryShouldReturnOldEpicAfterUpdate(){
        Epic roomRenovation = new Epic("Сделать ремонт в комнате", "Управиться за 10 дней");
        taskManager.addEpic(roomRenovation);
        taskManager.getEpicById(roomRenovation.getId());
        taskManager.updateEpic(
                new Epic(roomRenovation.getId(),
                "Изменили на новое имя",
                "Изменили описание",
                Status.IN_PROGRESS));
        List<Task> epicHistory = taskManager.getHistory();
        Epic oldEpic = (Epic) epicHistory.getFirst();
        assertEquals(roomRenovation.getName(), oldEpic.getName(),
                "В истории не сохранилась старая версия эпика");
        assertEquals(roomRenovation.getDescription(), oldEpic.getDescription(),
                "В истории не сохранилась старая версия эпика");

    }

    @Test
    public void getHistoryShouldReturnOldSubtaskAfterUpdate(){
        Epic roomRenovation = new Epic("Сделать ремонт в комнате", "Управиться за 10 дней");
        taskManager.addEpic(roomRenovation);
        Subtask roomRenovationSubtask1 =
                new Subtask("Покрасить стены",
                        "Что-то светлое или немного желтое",
                        roomRenovation.getId());
        taskManager.addSubtask(roomRenovationSubtask1);
        taskManager.getSubtaskById(roomRenovationSubtask1.getId());
        taskManager.updateSubtask(
                new Subtask(roomRenovationSubtask1.getId(),
                        "Резко изменили название",
                        "Да и описание было не особо то",
                        Status.IN_PROGRESS,
                        roomRenovation.getId()));
        List<Task> historySubtasks = taskManager.getHistory();
        Subtask oldSubtask = (Subtask) historySubtasks.getFirst();
        assertEquals(roomRenovationSubtask1.getName(), oldSubtask.getName(),
                "В истории не сохранилась старая версия эпика");
        assertEquals(roomRenovationSubtask1.getDescription(), oldSubtask.getDescription(),
                "В истории не сохранилась старая версия эпика");
    }
}
