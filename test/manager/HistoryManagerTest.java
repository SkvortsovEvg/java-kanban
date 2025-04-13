package manager;

import enums.TaskStatus.Status;
import manager.HistoryManager.HistoryManager;
import manager.HistoryManager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private Subtask subtask2;
    final LocalDateTime DATE = LocalDateTime.of(2025, 1, 1, 0, 0);

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(1, "Задача", "description1", Status.NEW, DATE, 1000L);
        epic = new Epic(2, "Эпик", "description3", Status.NEW);
        subtask = new Subtask(3, "Подзадача", "description3", 2, Status.NEW,
                DATE.plusDays(1), 1000L);
        subtask2 = new Subtask(4, "Подзадача", "description3", 2, Status.NEW,
                DATE.plusDays(2), 1000L);
    }

    @Test
    void add() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории отсутствует");
        assertEquals(1, history.size(), "История пустая");
        assertEquals(1, task.getId(), "История сохранена неверно");
    }

    @Test
    void getHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории отсутствует");
        assertTrue(history.isEmpty(), "История не пустая");

        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не сохранена");

        historyManager.remove(1);
        history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не пустая");

        historyManager.add(task);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История сохранена неверно");
        assertEquals(1, task.getId(), "История сохранена неверно");
    }

    @Test
    void remove() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории отсутствует");
        assertEquals(3, history.size(), "История сохранена неверно");
        //удаление из начала истории
        historyManager.remove(1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История сохранена неверно");
        assertEquals(2, history.get(0).getId(), "История сохранена неверно");
        assertEquals(3, history.get(1).getId(), "История сохранена неверно");
        //удаление из середины истории
        historyManager.add(subtask2);
        historyManager.remove(3);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История сохранена неверно");
        assertEquals(2, history.get(0).getId(), "История сохранена неверно");
        assertEquals(4, history.get(1).getId(), "История сохранена неверно");
        //удаление с конца истории
        historyManager.remove(4);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История сохранена неверно");
        assertEquals(2, history.get(0).getId(), "История сохранена неверно");
    }
}
