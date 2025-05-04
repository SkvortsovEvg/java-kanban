package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapters.InstantAdapter;
import http.adapters.LocalDateTimeAdapter;
import managers.HistoryManager.HistoryManager;
import managers.HistoryManager.InMemoryHistoryManager;
import managers.TaskManager.InMemoryTaskManager;
import managers.TaskManager.TaskManager;

import java.time.Instant;
import java.time.LocalDateTime;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefaults() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}