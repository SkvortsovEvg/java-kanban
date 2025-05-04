package service;

import managers.HistoryManager.InMemoryHistoryManager;

public class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @Override
    public InMemoryHistoryManager createManager() {
        return new InMemoryHistoryManager();
    }
}
