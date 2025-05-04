package service;

import managers.TaskManager.FileBackedTaskManager;
import task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static managers.TaskManager.FileBackedTaskManager.FILENAME_CSV;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    public FileBackedTaskManager createManager() {
        try {
            return new FileBackedTaskManager(File.createTempFile(FILENAME_CSV, ".temp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadFromCSVWithEmptyFile() {
        try {
            FileBackedTaskManager.loadFromFile(File.createTempFile(FILENAME_CSV, ".temp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadFromCSVWithBreakFile() throws IOException {
        File tempFile = File.createTempFile(FILENAME_CSV, ".temp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("IM BREAK"); // Явно некорректный CSV
        }

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager.loadFromFile(tempFile);
        });

        assertTrue(exception.getMessage().contains("Ошибка при чтении файла")
                        || exception.getCause() instanceof ArrayIndexOutOfBoundsException
                        || exception.getCause() instanceof IllegalArgumentException,
                "loadFromFile должен выбросить осмысленное исключение при некорректном CSV"
        );
    }

    @Test
    void loadFromCSVWithTasks() {
        Task t = newTask();
        t.setDescription("ЮТФ8"); // utf8 support check
        taskManager.addNewTask(t);
        FileBackedTaskManager m = FileBackedTaskManager.loadFromFile(new File(taskManager.getFilePath()));
        Assertions.assertEquals(taskManager, m, "loadFromFile не воссоздает копию");
    }

    @Test
    void saveCSVWithDeletedTasks() {
        taskManager.deleteAll();
        int id = taskManager.addNewTask(newTask());
        Task task = taskManager.getTask(id);
        try {
            Assertions.assertEquals(task.toCSV(), Files.readString(Paths.get(taskManager.getFilePath())).trim());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}