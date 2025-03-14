package manager;

import enums.Status;
import manager.TaskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public class InMemoryTaskManagerTest {
    private static TaskManager taskManager;
    private static final int COUNT_OF_TASKS = 1;
    private static final int COUNT_OF_EPICS = 1;
    private static final int COUNT_OF_SUBTASKS = 3;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void addNewTask() {
        Task task = taskManager.addTask(
                new Task("Новое задание", "Новое описание к данному заданию"));

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Данная задача не найдена!");
        assertEquals(task, savedTask, "Данные задачи НЕ совпадают!");

        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются в список задач!");
        assertEquals(COUNT_OF_TASKS, tasks.size(), "Количество задач не совпадает!");
        assertEquals(task, savedTask, "Не совпадают сами задачи!");
    }

    @Test
    public void addNewEpicAndSubtasks() {
        Epic roomRenovation = taskManager.addEpic(
                new Epic("Сделать ремонт в комнате", "Управиться за 10 дней"));
        Subtask roomRenovationSubtask1 = taskManager.addSubtask(
                new Subtask("Покрасить стены",
                        "Что-то светлое или немного желтое",
                        roomRenovation.getId()));
        Subtask roomRenovationSubtask2 = taskManager.addSubtask(
                new Subtask("Собрать комод",
                        "Лучше слева от входа",
                        roomRenovation.getId()));
        Subtask roomRenovationSubtask3 = taskManager.addSubtask(
                new Subtask("Купить кресла",
                        "Лучше посветлее, посмотри в каталоге",
                        roomRenovation.getId()));

        Epic savedEpic = taskManager.getEpicById(roomRenovation.getId());
        Subtask savedRoomRenovationSubtask1 = taskManager.getSubtaskById(roomRenovationSubtask1.getId());
        Subtask savedRoomRenovationSubtask2 = taskManager.getSubtaskById(roomRenovationSubtask2.getId());
        Subtask savedRoomRenovationSubtask3 = taskManager.getSubtaskById(roomRenovationSubtask3.getId());

        assertNotNull(savedEpic, "Данный эпик не найден!");
        assertNotNull(savedRoomRenovationSubtask3, "Подзадача не найдена!");
        assertEquals(roomRenovation, savedEpic, "Эпики не совпадают!");
        assertEquals(roomRenovationSubtask1, savedRoomRenovationSubtask1, "Подзадачи не совпадают!");
        assertEquals(roomRenovationSubtask2, savedRoomRenovationSubtask2, "Подзадачи не совпадают!");
        assertEquals(roomRenovationSubtask3, savedRoomRenovationSubtask3, "Подзадачи не совпадают!");

        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются!");
        assertEquals(COUNT_OF_EPICS, epics.size(), "Неверное количество эпиков");
        assertEquals(roomRenovation, epics.getFirst(), "Эпики не совпадают!");

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются!");
        assertEquals(COUNT_OF_SUBTASKS, subtasks.size(), "Неверное количество подзадач!");
        assertEquals(roomRenovationSubtask1, subtasks.getFirst(), "Подзадачи не совпадают!");
    }

    @Test
    public void updateTaskShouldReturnTaskWithTheSameId() {
        Task task = new Task("Новая задача", "Новая задача с новым описанием");
        taskManager.addTask(task);
        Task updatedTask =
                new Task(task.getId(),
                        "Более новая задача",
                        "Такого описания никто не видел",
                        Status.DONE);
        Task actualTask = taskManager.updateTask(updatedTask);
        assertEquals(actualTask, task, "Вернулись задачи с разными id!");
    }

    @Test
    public void updateEpicShouldReturnEpicWithTheSameId() {
        Epic epic = new Epic("Новый эпик", "Новое описание");
        taskManager.addEpic(epic);
        Epic updatedEpic =
                new Epic(epic.getId(),
                        "Более новый эпик",
                        "Другое описание",
                        Status.DONE);
        Epic actualEpic = taskManager.updateEpic(updatedEpic);
        assertEquals(actualEpic, epic, "Возвращается эпик с отличным id!");
    }

    @Test
    public void updateSubtaskShouldReturnSubtaskWithTheSameId() {
        Epic epic = new Epic("Хорошее имя", "Хорошее описание");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Хорошая ПОДЗАДАЧА", "Хорошее ОПИСАНИЕ", epic.getId());
        taskManager.addSubtask(subtask);
        Subtask updatedSubtask =
                new Subtask(
                        subtask.getId(),
                        "Обновленная хорошая ПОДЗАДАЧА",
                        "Супер-описание",
                        Status.DONE,
                        epic.getId());
        Subtask actualSubtask = taskManager.updateSubtask(updatedSubtask);
        assertEquals(actualSubtask, subtask, "Вернулась ПОДЗАДАЧА с другим id!");
    }

    @Test
    public void deleteTasksShouldReturnEmptyList() {
        taskManager.addTask(new Task("Приготовить еду", "Смотри в меню"));
        taskManager.addTask(new Task("Собрать шкаф", "Не забудь посмотреть в инструкцию"));
        taskManager.deleteTasks();
        List<Task> tasks = taskManager.getTasks();
        assertTrue(tasks.isEmpty(), "После удаления задач список должен быть пуст!");
    }

    @Test
    public void deleteEpicsShouldReturnEmptyList() {
        taskManager.addEpic(new Epic("Сделать ремонт в комнате", "Управиться за 10 дней"));
        taskManager.deleteEpics();
        List<Epic> epics = taskManager.getEpics();
        assertTrue(epics.isEmpty(), "Список эпиков должен быть пуст!");
    }

    @Test
    public void deleteSubtasksShouldReturnEmptyList() {
        Epic roomRenovation = new Epic("Сделать ремонт в комнате", "Управиться за 10 дней");
        taskManager.addEpic(roomRenovation);
        taskManager.addSubtask(
                new Subtask("Покрасить стены",
                        "Что-то светлое или немного желтое",
                        roomRenovation.getId()));
        taskManager.addSubtask(
                new Subtask("Собрать комод",
                        "Лучше слева от входа",
                        roomRenovation.getId()));
        taskManager.deleteSubtasks();
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertTrue(subtasks.isEmpty(), "После удаления подзадач список должен быть пуст!");
    }

    @Test
    public void deleteTaskByIdShouldReturnNullIfKeyIsMissing() {
        taskManager.addTask(
                new Task(1,
                        "Замечательная история",
                        "Замечательное описание",
                        Status.NEW));
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void deleteEpicByIdShouldReturnNullIfKeyIsMissing() {
        taskManager.addEpic(new Epic(1,
                "Сделать ремонт в комнате",
                "Управиться за 10 дней",
                Status.IN_PROGRESS));
        taskManager.deleteEpicById(1);
        assertNull(taskManager.deleteTaskById(1));
        int i = 0;
    }

    @Test
    public void deleteSubtaskByIdShouldReturnNullIfKeyIsMissing() {
        Epic roomRenovation = new Epic("Сделать ремонт в комнате", "Управиться за 10 дней");
        taskManager.addEpic(roomRenovation);
        taskManager.addSubtask(
                new Subtask("Покрасить стены",
                        "Что-то светлое или немного желтое",
                        roomRenovation.getId()));
        taskManager.addSubtask(
                new Subtask("Собрать комод",
                        "Лучше слева от входа",
                        roomRenovation.getId()));
        assertNull(taskManager.deleteTaskById(10));
    }

    @Test
    public void taskCreatedAndTaskAddedShouldHaveSameVariables() {
        Task task = new Task(1, "Скоро будут чистые полы", "Не знаю что писать", Status.DONE);
        taskManager.addTask(task);
        List<Task> list = taskManager.getTasks();
        Task actual = list.getFirst();
        assertEquals(actual.getId(), task.getId());
        assertEquals(actual.getName(), task.getName());
        assertEquals(actual.getDescription(), task.getDescription());
        assertEquals(actual.getStatus(), task.getStatus());
    }

}
