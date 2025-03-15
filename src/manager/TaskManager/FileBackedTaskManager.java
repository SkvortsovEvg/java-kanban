package manager.TaskManager;

import enums.TaskStatus.Status;
import enums.TaskType.Type;
import manager.HistoryManager.HistoryManager;
import manager.TaskManager.exceptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,epic\n";
    private String uploadedFile = null;

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public void loadFromFile(File file) {
        String header_csv = HEADER_CSV_FILE.substring(0, HEADER_CSV_FILE.length() - 1);
        StringBuilder resultSB = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while (reader.ready()) {
                line = reader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                if (line.equals(header_csv)) {
                    continue;
                }
                resultSB.append(line).append("\n");
            }
            uploadedFile = resultSB.toString();
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }
    }

    public void save() {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных");
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_CSV_FILE);

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл", e);
        }
    }

    private String toString(Task task) {
        String[] toJoin = {
                Integer.toString(task.getId()),
                getType(task).toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                getParentId(task),
        };
        return String.join(",", toJoin);
    }

    private Type getType(Task task) {
        if (task instanceof Epic) {
            return Type.EPIC;
        } else if (task instanceof Subtask) {
            return Type.SUBTASK;
        } else {
            return Type.TASK;
        }
    }

    private String getParentId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicID());
        }
        return "";
    }

    public Task fromString(String value) {
        String[] params = value.split(",");
        if (params[1].equals("EPIC")) {
            return new Epic(
                    Integer.parseInt(params[0]),
                    params[2],
                    params[4],
                    Status.valueOf(params[3].toUpperCase())
            );
        } else if (params[1].equals("SUBTASK")) {
            return new Subtask(
                    Integer.parseInt(params[0]),
                    params[2],
                    params[4],
                    Status.valueOf(params[3].toUpperCase()),
                    Integer.parseInt(params[5])
            );
        } else {
            return new Task(
                    Integer.parseInt(params[0]),
                    params[2],
                    params[4],
                    Status.valueOf(params[3].toUpperCase())
            );
        }
    }

    public void addTaskToManagerSave(Task task){
        if (task instanceof Epic epic) {
            addEpic(epic);
            return;
        }
        if (task instanceof Subtask subtask) {
            addSubtask(subtask);
        } else {
            addTask(task);
        }
    }

    public void addTaskToManagerTest(Task task){
        if (task instanceof Epic epic) {
            super.addEpic(epic);
            return;
        } else if (task instanceof Subtask subtask) {
            super.addSubtask(subtask);
        } else {
            super.addTask(task);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic epic = super.deleteEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public Task deleteTaskById(int id) {
        Task task = super.deleteTaskById(id);
        save();
        return task;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask newSubtask = super.updateSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task newTask = super.updateTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic newEpic = super.updateEpic(epic);
        save();
        return newEpic;
    }

    public String getUploadedFile() {
        return uploadedFile;
    }
}
