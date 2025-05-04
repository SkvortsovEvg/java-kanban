package managers.TaskManager;

import exception.ManagerOverlappingException;
import managers.HistoryManager.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final TreeSet<Task> prioritizedTasks;
    private final HistoryManager historyManager;

    protected int seq = 0;

    protected int generateId() {
        return ++seq;
    }

    public InMemoryTaskManager(HistoryManager history) {
        this.historyManager = history;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(((task1, task2) -> {
            if (task1.getEndTime().isBefore(task2.getStartTime())) {
                return -1;
            } else if (task1.getStartTime().isAfter(task2.getEndTime())) {
                return 1;
            } else {
                return 0;
            }
        }));
    }

    public boolean isNotOverlapping(Task task) {
        if (task.getDuration() == Duration.ZERO) {
            return true;
        }
        int sizeBefore = prioritizedTasks.size();
        prioritizedTasks.add(task);
        return prioritizedTasks.size() != sizeBefore;
    }

    @Override
    public int addNewTask(Task task) {
        int id = generateId();
        task.setId(id);
        if (isNotOverlapping(task)) {
            tasks.put(id, task);
            return id;
        } else {
            throw new ManagerOverlappingException(task.getName() + " пересекается");
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public int addNewEpic(Epic task) {
        int id = generateId();
        task.setId(id);
        if (isNotOverlapping(task)) {
            epics.put(id, task);
            return id;
        } else {
            throw new ManagerOverlappingException(task.getName() + " пересекается");
        }
    }

    @Override
    public Epic getEpic(int id) {
        Epic task = epics.get(id);
        if (task != null) {
            historyManager.add(task);
        }

        return task;
    }

    @Override
    public void deleteEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic == null) {
            return;
        }

        ArrayList<Subtask> sTasks = removedEpic.getSubTasks();
        sTasks.forEach((v) -> {
            subtasks.remove(v.getId());
        });

    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        epic.calculateEpicStatus();
    }

    @Override
    public ArrayList<Subtask> getEpicSubTasks(int id) {
        return epics.get(id).getSubTasks();
    }

    @Override
    public int addNewSubTask(Subtask task) {
        int id = generateId();
        task.setId(id);

        if (isNotOverlapping(task)) {
            subtasks.put(id, task);
            int epicId = task.getEpicId();
            if (epicId != 0) {
                epics.get(epicId).addSubTask(task);
            }
            return id;
        } else {
            throw new ManagerOverlappingException(task.getName() + " пересекается");
        }
    }

    @Override
    public Subtask getSubTask(int id) {
        Subtask task = subtasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void updateSubTask(Subtask subTask) {
        subtasks.put(subTask.getId(), subTask);
        Integer epicId = subTask.getEpicId();
        Epic savedEpic = epics.get(epicId);

        updateEpic(savedEpic);
    }

    @Override
    public void deleteSubTask(int id) {
        Subtask removedSubtask = subtasks.remove(id);
        if (removedSubtask == null) {
            return;
        }

        int epicId = removedSubtask.getEpicId();

        Epic epicSaved = epics.get(epicId);
        epicSaved.removeTask(removedSubtask);
    }

    @Override
    public void deleteAll() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        epics.forEach((i, epic) -> {
            ArrayList<Subtask> sTasks = epic.getSubTasks();
            sTasks.forEach((v) -> {
                subtasks.remove(v.getId());
            });
        });
        epics.clear();
    }

    @Override
    public void deleteAllSubTask() {
        subtasks.forEach((i, st) -> {
            int epicId = st.getEpicId();
            Epic epicSaved = epics.get(epicId);
            epicSaved.removeTask(st);
            epicSaved.calculateEpicStatus();
        });
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" + "tasks=" + tasks + ", epics=" + epics + ", subtasks=" + subtasks + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TaskManager manager = (TaskManager) o;
        return tasks.equals(manager.getTasks()) && epics.equals(manager.getEpics()) && subtasks.equals(manager.getSubTasks());
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getSubTasks() {
        return subtasks;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }
}