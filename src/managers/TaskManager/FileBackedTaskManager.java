package managers.TaskManager;

import exception.ManagerSaveException;
import managers.HistoryManager.HistoryManager;
import managers.Managers;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String FILENAME_CSV = "tasks.csv";
    private final File file;

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile();
        return manager;
    }

    public String getFilePath() {
        return file.getAbsolutePath();
    }

    public FileBackedTaskManager() {
        this(Managers.getDefaultHistory());
    }

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistory(), file);
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        this(historyManager, new File(FILENAME_CSV));
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    @Override
    public int addNewSubTask(SubTask subtask) {
        int ret = super.addNewSubTask(subtask);
        save();
        return ret;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public int addNewTask(Task task) {
        int ret = super.addNewTask(task);
        save();
        return ret;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        int ret = super.addNewEpic(epic);
        save();
        return ret;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("id")) continue; // пропускаем заголовки/пустые строки

                Task task;
                try {
                    task = Task.fromString(line);


                    int id = task.getId();
                    if (task instanceof SubTask subtask) {
                        subtasks.put(id, subtask);
                    } else if (task instanceof Epic epic) {
                        epics.put(id, epic);
                    } else {
                        tasks.put(id, task);
                    }

                    if (id > seq) {
                        seq = id;
                    }
                } catch (Exception e) {
                    throw new ManagerSaveException("Ошибка при чтении строки: \"" + line + "\"", e);
                }
            }

            for (SubTask subtask : subtasks.values()) {
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubTask(subtask);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, UTF_8))) {
            for (Task task : tasks.values()) {
                writer.write(task.toCSV());
                writer.newLine();
            }
            for (SubTask subtask : subtasks.values()) {
                writer.write(subtask.toCSV());
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(epic.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка: " + e.getMessage() + " в файле: " + file.getAbsolutePath());
        }
    }
}
