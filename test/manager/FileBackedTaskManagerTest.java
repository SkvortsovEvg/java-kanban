package manager;

import enums.TaskStatus.Status;
import manager.TaskManager.FileBackedTaskManager;
import manager.TaskManager.exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static File FILE_FOR_SAVE;
    private static File FILE_FOR_LOAD;
    private static final String HEADER_CSV_FILE_TEXT =
            "id,type,name,status,description,epic\n"
                    + "1,TASK,Пропылесосить полы,NEW,Попробовать робота-пылесоса,\n"
                    + "2,EPIC,Сделать ремонт в комнате,NEW,Управиться за 10 дней,\n"
                    + "3,SUBTASK,Покрасить стены,NEW,Что-то светлое или немного желтое,2\n";

    private FileBackedTaskManager manager;

    @BeforeEach
    public void beforeEachAndShouldCreateEmptyFile() {
        try {
            FILE_FOR_SAVE = File.createTempFile("data_save_", ".csv");
            FILE_FOR_LOAD = File.createTempFile("data_load_", ".csv");
            manager = new FileBackedTaskManager(Managers.getDefaultHistory(), FILE_FOR_SAVE);
        } catch (IOException e) {
            throw new ManagerSaveException("Не могу создать временный файл");
        } finally {
            assertTrue(Files.exists(FILE_FOR_SAVE.toPath()), "Файл не был создан!");
        }
    }

    @Test
    public void shouldLoadEmptyFile() {
        manager.loadFromFile(FILE_FOR_SAVE);
        assertTrue(manager.getAllTasks().isEmpty()
                        && manager.getAllEpics().isEmpty()
                        && manager.getAllSubtasks().isEmpty(),
                "Неправильно читается файл!");
    }

    @Test
    public void shouldSaveSomeTaskToFile() {
        Task cleanFloor = new Task("Пропылесосить полы", "Попробовать робота-пылесоса");
        manager.addTask(cleanFloor);

        Epic roomRenovation = new Epic("Сделать ремонт в комнате", "Управиться за 10 дней");
        manager.addEpic(roomRenovation);

        Subtask roomRenovationSubtask1 =
                new Subtask("Покрасить стены",
                        "Что-то светлое или немного желтое",
                        roomRenovation.getId());
        manager.addSubtask(roomRenovationSubtask1);

        String stringFromFile = loadingFile();
        assertEquals(HEADER_CSV_FILE_TEXT, stringFromFile, "Файл считан неверно!");
    }

    private String loadingFile() {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_FOR_SAVE))) {
            while (reader.ready()) {
                String line = reader.readLine();
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не могу прочитать файл!");
        }
        return builder.toString();
    }

    @Test
    public void shouldLoadSomeTaskFromFile() {
        List<Task> task = new ArrayList<>();
        List<Subtask> subtask = new ArrayList<>();
        List<Epic> epic = new ArrayList<>();

        saveFile();
        manager = new FileBackedTaskManager(Managers.getDefaultHistory(), FILE_FOR_LOAD);
        manager.loadFromFile(FILE_FOR_LOAD);
        String uploadedFile = manager.getUploadedFile();
        String[] params = uploadedFile.split("\n");
        for (String line : params) {
            Task newTasks = manager.fromString(line);
            manager.addTaskToManagerSave(newTasks);
        }
        task.add(new Task(1, "Пропылесосить полы", "Попробовать робота-пылесоса", Status.NEW));
        epic.add(new Epic(2, "Сделать ремонт в комнате", "Управиться за 10 дней", Status.NEW));
        subtask.add(
                new Subtask(3,
                        "Покрасить стены",
                        "Что-то светлое или немного желтое",
                        Status.NEW,
                        2));
        boolean isTaskEquals = task.equals(manager.getAllTasks());
        boolean isEpicEquals = epic.equals(manager.getAllEpics());
        boolean isSubtaskEquals = subtask.equals(manager.getAllSubtasks());
        assertTrue(isTaskEquals
                        && isEpicEquals
                        && isSubtaskEquals,
                "Неверная загрузка нескольких задач!");
    }

    private void saveFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_FOR_LOAD))) {
            writer.write(HEADER_CSV_FILE_TEXT);
        } catch (IOException e) {
            throw new ManagerSaveException("Не могу записать файл!");
        }
    }

    @Test
    public void shouldAddNewTask() {
        Task task = new Task(1, "Пропылесосить полы", "Попробовать робота-пылесоса", Status.NEW);
        manager.addTask(task);
        Task savedTask = manager.getTaskById(task.getId());

        assertNotNull(savedTask, "Данная задача не найдена!");
        assertEquals(task, savedTask, "Данные задачи НЕ совпадают!");
    }

    @Test
    public void shouldAddNewEpic() {
        Epic epic = new Epic(2, "Сделать ремонт в комнате", "Управиться за 10 дней", Status.NEW);
        manager.addEpic(epic);
        Epic savedEpic = manager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Данный эпик не найден!");
        assertEquals(epic, savedEpic, "Данные задачи НЕ совпадают!");
    }

    @Test
    public void shouldAddNewSubtask() {
        Epic epic = new Epic(2, "Сделать ремонт в комнате", "Управиться за 10 дней", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask(3,
                "Покрасить стены",
                "Что-то светлое или немного желтое",
                Status.NEW,
                epic.getId());
        manager.addSubtask(subtask);
        Subtask savedSubtask = manager.getSubtaskById(subtask.getId());

        assertNotNull(savedSubtask, "Данная подзадача не найдена!");
        assertEquals(subtask, savedSubtask, "Данные подзадачи не совпадают!");
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task(1, "Пропылесосить полы", "Попробовать робота-пылесоса", Status.NEW);
        manager.addTask(task);
        Task updatedTask = new Task(task.getId(), "Полы чистые", "Пылесос нам помог", Status.DONE);
        Task actualTask = manager.updateTask(updatedTask);
        assertEquals(actualTask, task, "Вернулись задачи с разными id!");
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = new Epic("Новый эпик", "Новое описание");
        manager.addEpic(epic);
        Epic updatedEpic =
                new Epic(epic.getId(),
                        "Более новый эпик",
                        "Другое описание",
                        Status.DONE);
        Epic actualEpic = manager.updateEpic(updatedEpic);
        assertEquals(actualEpic, epic, "Возвращается эпик с отличным id!");
    }

    @Test
    public void shouldUpdateSubtask() {
        Epic epic = new Epic("Хорошее имя", "Хорошее описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Хорошая ПОДЗАДАЧА", "Хорошее ОПИСАНИЕ", epic.getId());
        manager.addSubtask(subtask);
        Subtask updatedSubtask =
                new Subtask(
                        subtask.getId(),
                        "Обновленная хорошая ПОДЗАДАЧА",
                        "Супер-описание",
                        Status.DONE,
                        epic.getId());
        Subtask actualSubtask = manager.updateSubtask(updatedSubtask);
        assertEquals(actualSubtask, subtask, "Вернулась ПОДЗАДАЧА с другим id!");
    }

    @Test
    public void shouldDeleteTaskByID() {
        Task cleanFloor =
                new Task(1,
                        "Пропылесосить полы",
                        "Попробовать робота-пылесоса",
                        Status.NEW);
        manager.addTask(cleanFloor);

        Task cleanWindows =
                new Task(2,
                        "Помыть окна",
                        "Чище-чище)))",
                        Status.NEW);
        manager.addTask(cleanWindows);

        manager.deleteTaskById(cleanFloor.getId());
        int sizeOfMapTask = manager.getAllTasks().size();
        assertEquals(1, sizeOfMapTask, "deleteTaskByID работает неверно!");
    }

    @Test
    public void shouldDeleteEpicByID() {
        Epic roomRenovation =
                new Epic(1, "Сделать ремонт в комнате", "Управиться за 10 дней", Status.NEW);
        manager.addEpic(roomRenovation);

        Epic paintWalls =
                new Epic(2, "Покрасить стены на улице", "В серый цвет", Status.NEW);
        manager.addEpic(paintWalls);

        manager.deleteEpicById(roomRenovation.getId());
        int sizeOfMapEpic = manager.getAllEpics().size();
        assertEquals(1, sizeOfMapEpic, "deleteEpicByID работает неверно!");
    }

    @Test
    public void shouldDeleteSubtaskByID() {
        Epic roomRenovation =
                new Epic(1, "Сделать ремонт в комнате", "Управиться за 10 дней", Status.NEW);
        manager.addEpic(roomRenovation);

        Subtask roomRenovationSubtask1 =
                new Subtask("Покрасить стены",
                        "Что-то светлое или немного желтое",
                        roomRenovation.getId());
        Subtask roomRenovationSubtask2 =
                new Subtask("Собрать комод",
                        "Лучше слева от входа",
                        roomRenovation.getId());
        Subtask roomRenovationSubtask3 =
                new Subtask("Сменить входную дверь",
                        "Не знаю какую только",
                        roomRenovation.getId());

        manager.addSubtask(roomRenovationSubtask1);
        manager.addSubtask(roomRenovationSubtask2);
        manager.addSubtask(roomRenovationSubtask3);

        manager.deleteSubtaskById(roomRenovationSubtask1.getId());

        int sizeOfMapSubtask = manager.getAllSubtasks().size();

        assertEquals(2, sizeOfMapSubtask, "deleteSubtaskByID работает неверно!");
    }
}
