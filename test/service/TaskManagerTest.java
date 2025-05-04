package service;

import exception.ManagerOverlappingException;
import managers.TaskManager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("TaskManagerTest")
abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    public abstract T createManager();

    @BeforeEach
    void setup() {
        taskManager = createManager();

        Task task = newTask();
        taskManager.addNewTask(task);

        Epic epic = newEpic();
        taskManager.addNewEpic(epic);

        Subtask subTask = newSubTask(epic);
        taskManager.addNewSubTask(subTask);
    }

    protected Task newTask() {
        return new Task("Test", TaskStatus.NEW, "Test description");
    }

    protected Epic newEpic() {
        return new Epic("Test", TaskStatus.NEW, "Test description");
    }

    protected Subtask newSubTask(Epic epic) {
        return new Subtask("Test", TaskStatus.NEW, "Test description", epic.getId());
    }

    @Test
    void addNewTask() {
        Task task = newTask();
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskId), "Задачи не совпадают.");
    }

    @Test
    void addNewSubTask() {
        Epic epic = newEpic();
        int epicId = taskManager.addNewEpic(epic);

        Subtask task = newSubTask(epic);
        final int taskId = taskManager.addNewSubTask(task);

        final Task savedTask = taskManager.getSubTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Subtask> tasks = taskManager.getSubTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskId), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic task = newEpic();
        final int taskId = taskManager.addNewEpic(task);

        final Epic savedTask = taskManager.getEpic(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Epic> tasks = taskManager.getEpics();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskId), "Задачи не совпадают.");
    }

    private void assertManagerMaps(int taskExpected, int subtaskExpected, int epicExpected) {
        final HashMap<Integer, Task> tasks = taskManager.getTasks();
        final HashMap<Integer, Subtask> subTasks = taskManager.getSubTasks();
        final HashMap<Integer, Epic> epics = taskManager.getEpics();

        assertEquals(taskExpected, tasks.size(), "Неверное количество tasks.");
        assertEquals(subtaskExpected, subTasks.size(), "Неверное количество subTasks.");
        assertEquals(epicExpected, epics.size(), "Неверное количество epics.");
    }

    @Test
    void deleteAll() {
        taskManager.deleteAll();

        assertManagerMaps(0, 0, 0);
    }

    @Test
    void deleteAllTask() {
        taskManager.deleteAllTask();

        assertManagerMaps(0, 1, 1);
    }

    @Test
    void deleteAllEpic() {
        taskManager.deleteAllEpic();

        final HashMap<Integer, Task> tasks = taskManager.getTasks();
        assertManagerMaps(1, 0, 0);
    }

    @Test
    void deleteAllSubTask() {
        taskManager.deleteAllSubTask();

        final HashMap<Integer, Task> tasks = taskManager.getTasks();
        assertManagerMaps(1, 0, 1);
    }

    @Test
    void isOverlapping() {
        Task task = newTask();
        task.setDuration(Duration.ofMinutes(15));
        taskManager.addNewTask(task);

        assertThrows(ManagerOverlappingException.class, () -> {
            Task task2 = newTask();
            task2.setDuration(Duration.ofMinutes(15));
            taskManager.addNewTask(task2);
        });
    }

    @Test
    void isNotOverlapping() {
        Task task = newTask();
        task.setStartTime(LocalDateTime.now().plusMinutes(15));
        task.setDuration(Duration.ofMinutes(15));
        taskManager.addNewTask(task);

        assertDoesNotThrow(() -> {
            Task task2 = newTask();
            task2.setDuration(Duration.ofMinutes(14));
            taskManager.addNewTask(task2);
        });
    }
}