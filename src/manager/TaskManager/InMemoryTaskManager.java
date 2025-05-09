package manager.TaskManager;

import enums.TaskStatus.Status;
import manager.HistoryManager.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int nextID = 1;

    @Override
    public int getNextID() {
        return nextID++;
    }

    @Override
    public void addTask(Task task) {
        task.setId(getNextID());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNextID());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(getNextID());
        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
    }

    @Override
    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (taskID == null || !tasks.containsKey(taskID)) {
            return null;
        }
        tasks.replace(taskID, task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicID = epic.getId();
        if (epicID == null || !epics.containsKey(epicID)) {
            return null;
        }
        Epic oldEpic = epics.get(epicID);
        ArrayList<Subtask> oldEpicSubtaskList = oldEpic.getSubtaskList();
        if (!oldEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : oldEpicSubtaskList) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.replace(epicID, epic);

        ArrayList<Subtask> newEpicSubtaskList = epic.getSubtaskList();
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
    public Task deleteTaskById(int id) {
        historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpicById(int id) {
        ArrayList<Subtask> epicSubtasks = epics.get(id).getSubtaskList();
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
        return epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            int deletedKey = epic.getSubtaskList().indexOf(subtask);
            if (deletedKey != -1) {
                epic.getSubtaskList().remove(deletedKey);
                updateEpicStatus(epic);
                subtasks.remove(id);
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
        int epicID = subtask.getEpicID();
        Subtask oldSubtask = subtasks.get(subtaskID);
        subtasks.replace(subtaskID, subtask);
        Epic epic = epics.get(epicID);
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
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
    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getTasksSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskList().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    private void updateEpicStatus(Epic epic) {
        int countDone = 0;
        int countNew = 0;
        ArrayList<Subtask> list = epic.getSubtaskList();

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
}
