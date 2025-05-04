package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager.TaskManager;

import java.io.IOException;

public class PrioritizedTasksHandler extends BaseHttpHandler {

    public PrioritizedTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    private void handleGet(HttpExchange httpExchange, String[] path) throws IOException {
        response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(httpExchange, response, 200);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");


        switch (method) {
            case "GET" -> handleGet(httpExchange, path);
            default -> sendNotFound(httpExchange);
        }
    }
}