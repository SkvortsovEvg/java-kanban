package service;

import managers.HistoryManager.HistoryManager;
import task.Task;
import enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("HistoryManagerTest")
abstract class HistoryManagerTest<T extends HistoryManager> {
    protected T historyManager;

    public abstract T createManager();

    private static int getIdCounter = 0;

    private int generateId() {
        return ++getIdCounter;
    }

    protected Task newTask() {
        return new Task("Test addNewTask", TaskStatus.NEW, "Test addNewTask description");
    }


    @BeforeEach
    void beforeEach() {
        getIdCounter = 0;
        historyManager = createManager();
    }

    @Test
    void add() {
        Task task = newTask();
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void addTasksToHistoryTest() {
        Task task1 = newTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = newTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = newTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(List.of(task1, task2, task3), historyManager.getHistory());
    }

    @Test
    public void removeTaskTest() {
        Task task1 = newTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = newTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = newTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        assertEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    public void removeOnlyOneTaskTest() {
        Task task = newTask();
        int newTaskId = generateId();
        task.setId(newTaskId);
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertEquals(Collections.EMPTY_LIST, historyManager.getHistory());
    }

    @Test
    public void historyIsEmptyTest() {
        Task task1 = newTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = newTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = newTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        historyManager.remove(task3.getId());
        assertEquals(Collections.EMPTY_LIST, historyManager.getHistory());
    }

    @Test
    public void notRemoveTaskWithBadIdTest() {
        Task task = newTask();
        int newTaskId = generateId();
        task.setId(newTaskId);
        historyManager.add(task);
        historyManager.remove(-1);
        assertEquals(List.of(task), historyManager.getHistory());
    }
}