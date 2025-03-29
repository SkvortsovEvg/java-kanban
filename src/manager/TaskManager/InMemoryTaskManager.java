package manager.TaskManager;

import enums.TaskStatus.Status;
import manager.HistoryManager.HistoryManager;
import manager.Managers;
import manager.TaskManager.exceptions.CollisionTaskException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    final static Comparator<Task> COMPARATOR = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    protected Set<Task> prioritizedTasks = new TreeSet<>(COMPARATOR);

    public InMemoryTaskManager() {
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int nextID = 0;

    @Override
    public int getNextID() {
        return ++nextID;
    }

    public void setNextID(int nextID) {
        this.nextID = nextID;
    }

    @Override
    public void addTask(Task task) {
        validate(task);
        task.setId(getNextID());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        validate(epic);
        epic.setId(getNextID());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        validate(subtask);
        subtask.setId(getNextID());
        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        setEpicDateTime(epic.getId());
        prioritizedTasks.add(subtask);
    }

    @Override
    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (taskID == null || !tasks.containsKey(taskID)) {
            return null;
        }
        prioritizedTasks.remove(tasks.get(taskID));
        tasks.replace(taskID, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicID = epic.getId();
        if (epicID == null || !epics.containsKey(epicID)) {
            return null;
        }
        Epic oldEpic = epics.get(epicID);
        List<Subtask> oldEpicSubtaskList = oldEpic.getSubtaskList();
        if (!oldEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : oldEpicSubtaskList) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.replace(epicID, epic);

        List<Subtask> newEpicSubtaskList = epic.getSubtaskList();
        if (!newEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : newEpicSubtaskList) {
                subtasks.put(subtask.getId(), subtask);
            }
        }
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Epic deleteEpicById(int id) {
        List<Subtask> epicSubtasks = epics.get(id).getSubtaskList();
        for (Subtask subtask : epicSubtasks) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        historyManager.remove(id);
        return epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            Epic epic = epics.get(subtask.getEpicID());
            int deletedKey = epic.getSubtaskList().indexOf(subtask);
            if (deletedKey != -1) {
                epic.getSubtaskList().remove(deletedKey);
                subtasks.remove(id);
                updateEpicStatus(epic);
                setEpicDateTime(subtask.getEpicID());
                historyManager.remove(id);
            }
        }
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskID = subtask.getId();
        if (subtaskID == null || !subtasks.containsKey(subtaskID)) {
            return null;
        }
        validate(subtask);
        int epicID = subtask.getEpicID();
        Subtask oldSubtask = subtasks.get(subtaskID);
        subtasks.replace(subtaskID, subtask);
        Epic epic = epics.get(epicID);
        List<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(oldSubtask);
        subtaskList.add(subtask);
        epic.setSubtaskList(subtaskList);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtasks.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getTasksSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskList().clear();
            updateEpicStatus(epic);
            setEpicDateTime(epic.getId());
        }
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            int epicId = epic.getId();
            List<Subtask> subtaskList = epics.get(epicId).getSubtaskList();
            for (Subtask subtask : subtaskList) {
                prioritizedTasks.remove(subtasks.get(subtask.getId()));
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }

    public void updateEpicStatus(Epic epic) {
        int countDone = 0;
        int countNew = 0;
        List<Subtask> list = epic.getSubtaskList();

        for (Subtask subtask : list) {
            if (subtask.getStatus().equals(Status.DONE)) {
                countDone++;
            }
            if (subtask.getStatus().equals(Status.NEW)) {
                countNew++;
            }
        }
        if (countDone == list.size()) {
            epic.setStatus(Status.DONE);
        } else if (countNew == list.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void addToHistory(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        } else {
            historyManager.add(tasks.get(id));
        }
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setEpicDateTime(int epicId) {
        List<Subtask> subtaskList = epics.get(epicId).getSubtaskList();
        if (subtaskList.isEmpty()) {
            epics.get(epicId).setDuration(0L);
            epics.get(epicId).setStartTime(LocalDateTime.of(2000, 1, 1, 0, 0));
            epics.get(epicId).setEndTime(LocalDateTime.of(2000, 1, 1, 0, 0));
            return;
        }

        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        long epicDuration = 0L;

        for (Subtask subtask : subtaskList) {
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            if (subtaskStartTime != null) {
                if (epicStartTime == null || subtaskStartTime.isBefore(epicStartTime)) {
                    epicStartTime = subtaskStartTime;
                }
            }
            if (subtaskEndTime != null) {
                if (epicEndTime == null || subtaskEndTime.isAfter(epicEndTime)) {
                    epicEndTime = subtaskEndTime;
                }
            }
            epicDuration += subtasks.get(subtask.getId()).getDuration();
        }

        epics.get(epicId).setStartTime(epicStartTime);
        epics.get(epicId).setEndTime(epicEndTime);
        epics.get(epicId).setDuration(epicDuration);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void validate(Task task) {
        List<Task> prioritizedTasks = getPrioritizedTasks();
        for (Task existingTask : prioritizedTasks) {
            if (Objects.equals(existingTask.getId(), task.getId())) {
                continue;
            }
            if ((!task.getEndTime().isAfter(existingTask.getStartTime()))
                    || (!task.getStartTime().isBefore(existingTask.getEndTime()))) {
                continue;
            }
            throw new CollisionTaskException("Время выполнения задачи пересекается со временем уже существующей " +
                    "задачи. Выберите другую дату.");
        }

    }
}
