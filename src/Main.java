import com.sun.net.httpserver.HttpServer;
import enums.TaskStatus.Status;
import http.HttpTaskServer;
import manager.Managers;
import manager.TaskManager.FileBackedTaskManager;
import manager.TaskManager.InMemoryTaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    private static final FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory());

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpServer = new HttpTaskServer(new InMemoryTaskManager());
        httpServer.start();
        //Task task1 = new Task(1, "Задача", "description1", Status.NEW,
        //        LocalDateTime.of(2023, 1, 1, 0, 0), 1000L);
        //Epic epic2 = new Epic(2, "Эпик", "description2", Status.NEW);
        //Subtask subtask3 = new Subtask("Подзадача", "description3", Status.NEW, epic2.getId(),
        //        LocalDateTime.of(2023, 1, 2, 0, 0), 1000L);

        //manager.addTask(task1);
        //manager.addEpic(epic2);
        //manager.addSubtask(subtask3);

        manager.getEpicById(2);
        manager.getTaskById(1);
    }
}