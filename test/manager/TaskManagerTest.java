package manager;

import enums.TaskStatus.Status;
import manager.TaskManager.TaskManager;
import manager.TaskManager.exceptions.CollisionTaskException;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected final LocalDateTime DATE = LocalDateTime.of(2025, 1, 1, 0, 0);
    protected final int EPIC_ID = 2;
    protected Task task1;
    protected Epic epic2;
    protected Subtask subtask3;
    protected Subtask subtask4;

    protected void initTasks() {
        task1 = new Task(1, "Задача", "description1", Status.NEW, DATE, 1000L);
        taskManager.addTask(task1);
        epic2 = new Epic("Эпик", "description2");
        taskManager.addEpic(epic2);
        subtask3 = new Subtask(3,
                "Подзадача",
                "description3",
                EPIC_ID,
                Status.NEW,
                DATE.plusDays(1),
                1000L);
        taskManager.addSubtask(subtask3);
        subtask4 = new Subtask(4,
                "Подзадача",
                "description4",
                EPIC_ID,
                Status.NEW,
                DATE.plusDays(2),
                1000L);
        taskManager.addSubtask(subtask4);
    }

    @Test
    void addTask() {
        Task expectedTask = taskManager.getTaskById(1);
        assertNotNull(expectedTask, "Задача не найдена.");
        assertNotNull(taskManager.getAllTasks(), "Задачи на возвращаются.");
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(1, expectedTask.getId(), "Идентификаторы задач не совпадают");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(taskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(taskPriority, expectedTask, "В список приоритизации добавлена неверная задача");
    }

    @Test
    void addEpic() {
        Epic expectedEpic = taskManager.getEpicById(2);
        assertNotNull(expectedEpic, "Задача не найдена.");
        assertNotNull(taskManager.getAllEpics(), "Задачи на возвращаются.");
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество задач.");
        assertNotNull(expectedEpic.getSubtaskList(), "Список подзадач не создан.");
        assertEquals(Status.NEW, expectedEpic.getStatus(), "Статус не NEW");
        assertEquals(2, expectedEpic.getId(), "Идентификаторы задач не совпадают");
    }

    @Test
    void addSubtask() {
        Epic expectedEpicOfSubtask = taskManager.getEpicById(EPIC_ID);
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не null");
        Subtask expectedSubtask = taskManager.getSubtaskById(3);
        assertNotNull(expectedSubtask, "Задача не найдена.");
        assertNotNull(taskManager.getAllSubtasks(), "Задачи на возвращаются.");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Неверное количество задач.");
        assertNotNull(expectedEpicOfSubtask, "Эпик подзадачи не найден");
        assertNotNull(taskManager.getEpicById(EPIC_ID).getSubtaskList(), "Список подзадач не обновился");
        assertEquals(DATE.plusDays(1), expectedEpicOfSubtask.getStartTime(), "Время эпика не обновилось");
        assertEquals(Status.NEW, expectedEpicOfSubtask.getStatus(), "Статус не NEW");
        assertEquals(3, expectedSubtask.getId(), "Идентификаторы задач не совпадают");
        assertEquals(expectedEpicOfSubtask, epic2, "Эпик подзадачи неверный");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(subtaskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(subtaskPriority, expectedSubtask, "В список приоритизации добавлена неверная задача");
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не изменилось");
    }

    @Test
    void checkEpicStatus() {
        Epic expectedEpicOfSubtask = taskManager.getEpicById(EPIC_ID);
        //проверить статус эпика ин прогресс если сабтаски новые и дан
        Subtask updateSubtask4 = new Subtask(4, "Подзадача", "description4", EPIC_ID, Status.DONE,
                DATE.plusDays(2), 1000L);
        taskManager.updateSubtask(updateSubtask4);
        assertEquals(Status.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
        //проверить статус эпика ин прогресс если сабтаски ин прогресс
        Subtask updateSubtask3 = new Subtask(3, "Подзадача", "description3", EPIC_ID, Status.DONE,
                DATE.plusDays(1), 1000L);
        Subtask update2Subtask4 = new Subtask(4, "Подзадача", "description4", EPIC_ID, Status.DONE,
                DATE.plusDays(2), 1000L);
        taskManager.updateSubtask(updateSubtask3);
        taskManager.updateSubtask(update2Subtask4);
        assertEquals(Status.DONE, expectedEpicOfSubtask.getStatus(), "Статус не DONE");
        //проверить статус эпика дан если сабтаски дан
        Subtask update2Subtask3 = new Subtask(3, "Подзадача", "description3",
                EPIC_ID, Status.IN_PROGRESS, DATE.plusDays(1), 1000L);
        Subtask update3Subtask4 = new Subtask(4, "Подзадача", "description4",
                EPIC_ID, Status.IN_PROGRESS, DATE.plusDays(2), 1000L);
        taskManager.updateSubtask(update2Subtask3);
        taskManager.updateSubtask(update3Subtask4);
        assertEquals(Status.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
        //проверить дюрейшен epica 2000
        assertEquals(2000L, expectedEpicOfSubtask.getDuration(),
                "Продолжительность эпика не обновилась");
    }

    @Test
    void getHistory() {
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(4);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);
        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size(), "Список истории сформирован неверно");
        assertEquals(2, history.get(0).getId(), "Задача 2 не добавлена в список истории");
        assertEquals(4, history.get(1).getId(), "Задача 4 не добавлена в список истории");
        assertEquals(3, history.get(2).getId(), "Задача 3 не добавлена в список истории");
        assertEquals(1, history.get(3).getId(), "Задача 1 не добавлена в список истории");
    }

    @Test
    void getPrioritizedTasks() {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritizedTasks.get(0).getId(), "Задача 1 не приоритизирована");
        assertEquals(3, prioritizedTasks.get(1).getId(), "Задача 3 не приоритизирована");
        assertEquals(4, prioritizedTasks.get(2).getId(), "Задача 4 не приоритизирована");
    }

    @Test
    void removeTaskById() {
        assertNotNull(taskManager.getAllTasks(), "Список задач не заполнен");
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1), "Задача не удалена");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNull(taskPriority, "Задача не удалена из списка приоритизации");
    }

    @Test
    void removeSubtaskById() {
        assertNotNull(taskManager.getAllSubtasks(), "Список подзадач не заполнен");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Неверное количество задач.");
        taskManager.deleteSubtaskById(3);
        assertNull(taskManager.getSubtaskById(3), "Подзадача не удалена");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNull(subtaskPriority, "Задача не удалена из списка приоритизации");
        assertEquals(DATE.plusDays(2), taskManager.getEpicById(EPIC_ID).getStartTime(),
                "Время эпика не изменилось");
    }

    @Test
    void removeEpicById() {
        assertNotNull(taskManager.getAllEpics(), "Список эпиков не заполнен");
        taskManager.deleteEpicById(2);
        assertNull(taskManager.getEpicById(2), "Эпик не удален");
    }

    @Test
    void validate() {
        Task task1 = new Task(1, "Задача1", "description1", Status.NEW, DATE, 1000L);
        Task task2 = new Task(2, "Задача2", "description2", Status.NEW, DATE, 1000L);

        CollisionTaskException exception = assertThrows(CollisionTaskException.class,
                () -> {
                    taskManager.addTask(task1);
                    taskManager.addTask(task2);
                });
        assertEquals(
                "Время выполнения задачи пересекается со временем уже существующей задачи. " +
                        "Выберите другую дату.",
                exception.getMessage());
    }
}