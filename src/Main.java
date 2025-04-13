import enums.TaskStatus.Status;
import manager.Managers;
import manager.TaskManager.FileBackedTaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;

public class Main {
    private static final FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory());

    public static void main(String[] args) {
        Task task1 = new Task(1, "Задача", "description1", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 0, 0), 1000L);
        Epic epic2 = new Epic(2, "Эпик", "description2", Status.NEW);
        Subtask subtask3 = new Subtask("Подзадача", "description3", Status.NEW, epic2.getId(),
                LocalDateTime.of(2023, 1, 2, 0, 0), 1000L);

        manager.addTask(task1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);

        manager.getEpicById(2);
        manager.getTaskById(1);
    }
}