package manager.HistoryManager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int MAX_HISTORY_STORAGE = 10;
    private final List<Task> historyList = new ArrayList<Task>();

    @Override
    public void add(Task task) {
        if (historyList.size() == MAX_HISTORY_STORAGE) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
