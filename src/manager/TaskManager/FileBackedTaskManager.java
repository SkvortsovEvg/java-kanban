package manager.TaskManager;

import enums.TaskStatus.Status;
import enums.TaskType.Type;
import manager.HistoryManager.HistoryManager;
import exceptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import http.adapters.LocalDateAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final File FILE = new File("./resources/data.csv");
    private static final String FIRST_LINE = "id,type,name,status,description,startTime,endTime,duration,epic";
    private static final DateTimeFormatter formatter = LocalDateAdapter.FORMATTER;

    public FileBackedTaskManager() {
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager();
        Map<Integer, Task> fileHistory = new HashMap<>();
        List<Integer> idsHistory = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> lines = reader.lines().toList();
            for (int i = 1; i < lines.size(); i++) {
                if (lines.get(i).isEmpty() && !lines.get(i + 1).isEmpty()) {
                    idsHistory = historyFromString(lines.get(i + 1));
                    break;
                }
                String[] line = lines.get(i).split(",");
                Task task = fromString(line);
                fileHistory.put(task.getId(), task);

                switch (task.getTaskType()) {
                    case TASK:
                        fileManager.tasks.put(task.getId(), task);
                        fileManager.prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        fileManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileManager.subtasks.put(task.getId(), (Subtask) task);
                        //добавляем подзадачу в список идентификаторов подзадач в эпике
                        int epicId = ((Subtask) task).getEpicID();
                        List<Integer> subtaskIds = fileManager.epics.get(epicId).getSubtaskList()
                                .stream()
                                .map(Subtask::getId)
                                .collect(Collectors.toList());
                        subtaskIds.add(task.getId());
                        Epic epic = fileManager.epics.get(epicId);
                        fileManager.updateEpicStatus(epic);
                        fileManager.prioritizedTasks.add(task);
                        break;
                }

                if (task.getId() > fileManager.getNextID()) {
                    fileManager.setNextID(task.getId());
                }

            }
            for (Integer id : idsHistory) {
                fileManager.historyManager.add(fileHistory.get(id));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }

        return fileManager;
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> idsHistory = new ArrayList<>();
        String[] line = value.split(",");
        for (String id : line) {
            idsHistory.add(Integer.valueOf(id));
        }
        return idsHistory;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE, StandardCharsets.UTF_8))) {
            writer.write(FIRST_LINE);
            writer.newLine();
            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
            writer.newLine();
            List<String> ids = new ArrayList<>(); //сохраняем историю просмотров
            for (Task task : getHistory()) {
                ids.add(String.valueOf(task.getId()));
            }
            writer.write(String.join(",", ids));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл", e);
        }
    }

    private String toString(Task task) {
        String[] toJoin = {
                Integer.toString(task.getId()),
                task.getTaskType().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                task.getStartTime().format(formatter),
                task.getEndTime().format(formatter),
                Long.toString(task.getDuration()),
                getParentId(task),
        };
        return String.join(",", toJoin);
    }

    private String getParentId(Task task) {
        if (task.getTaskType() == Type.SUBTASK) {
            return Integer.toString(((Subtask) task).getEpicID());
        }
        return "";
    }

    public static Task fromString(String[] value) {
        if (value[1].equals("EPIC")) {
            return new Epic(
                    Integer.parseInt(value[0]),
                    value[2],
                    value[4],
                    Status.valueOf(value[3].toUpperCase()),
                    LocalDateTime.parse(value[5], formatter),
                    Long.parseLong(value[7]),
                    LocalDateTime.parse(value[6], formatter)
            );
        } else if (value[1].equals("SUBTASK")) {
            return new Subtask(
                    Integer.parseInt(value[0]),
                    value[2],
                    value[4],
                    Integer.parseInt(value[8]),
                    Status.valueOf(value[3].toUpperCase()),
                    LocalDateTime.parse(value[5], formatter),
                    Long.parseLong(value[7])
            );
        } else {
            return new Task(
                    Integer.parseInt(value[0]),
                    value[2],
                    value[4],
                    Status.valueOf(value[3].toUpperCase()),
                    LocalDateTime.parse(value[5], formatter),
                    Long.parseLong(value[7])
            );
        }
    }

    public void addTaskToManagerSave(Task task) {
        if (task instanceof Epic epic) {
            addEpic(epic);
            return;
        }
        if (task instanceof Subtask subtask) {
            addSubtask(subtask);
            updateEpicStatus(getEpicById(subtask.getEpicID()));
        } else {
            addTask(task);
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
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();

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

    @Override
    public void setEpicDateTime(int epicId) {
        super.setEpicDateTime(epicId);
        save();
    }
}
