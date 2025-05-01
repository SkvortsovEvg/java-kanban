package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapters.InstantAdapter;
import http.adapters.LocalDateAdapter;
import manager.HistoryManager.HistoryManager;
import manager.HistoryManager.InMemoryHistoryManager;
import manager.TaskManager.InMemoryTaskManager;

import java.time.Instant;
import java.time.LocalDateTime;

public class Managers {
    public static InMemoryTaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();
    }
}
