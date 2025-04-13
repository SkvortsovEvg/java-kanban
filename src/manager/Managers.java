package manager;

import manager.HistoryManager.HistoryManager;
import manager.HistoryManager.InMemoryHistoryManager;
import manager.TaskManager.InMemoryTaskManager;

public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
