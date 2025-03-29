package manager;

import manager.TaskManager.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    void setUp() {
        file = new File("./resources/data.csv");
        super.taskManager = new FileBackedTaskManager();
        initTasks();
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(4);
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, fileManager.getTasks().size(),
                "Количество задач после выгрузки не совпадает");
        assertEquals(taskManager.getAllTasks(), fileManager.getAllTasks(),
                "Список задач после выгрузки не совпададает");
        assertEquals(1, fileManager.getEpics().size(),
                "Количество эпиков после выгрузки не совпадает");
        assertEquals(taskManager.getAllEpics(), fileManager.getAllEpics(),
                "Список эпиков после выгрузки не совпадает");
        assertEquals(2, fileManager.getSubtasks().size(),
                "Количество подзадач после выгрузки не совпадает");
        assertEquals(taskManager.getAllTasks(), fileManager.getAllTasks(),
                "Список подзадач после выгрузки не совпадает");
        List<Task> history = taskManager.getHistory();
        List<Task> historyFromFile = fileManager.getHistory();
        assertEquals(4, historyFromFile.size(), "Список истории сформирован неверно");
        assertEquals(history, historyFromFile, "Список истории после выгрузки не совпадает");
        assertEquals(taskManager.getPrioritizedTasks(), fileManager.getPrioritizedTasks(),
                "Отсортированный список после выгрузки не совпадает");
        assertEquals(5, taskManager.getNextID(),
                "Идентификатор последней добавленной задачи после выгрузки не совпадает");
    }
}