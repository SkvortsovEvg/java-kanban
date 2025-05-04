package http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.ManagerOverlappingException;
import task.Subtask;
import managers.TaskManager.TaskManager;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    private void handleGet(HttpExchange httpExchange, String[] path) throws IOException {
        if (path.length == 2) {
            response = gson.toJson(taskManager.getSubTasks());
            sendText(httpExchange, response, 200);
        } else {
            try {
                int id = Integer.parseInt(path[2]);
                Subtask subtask = taskManager.getSubTask(id);
                if (subtask != null) {
                    response = gson.toJson(subtask);
                    sendText(httpExchange, response, 200);
                } else {
                    sendNotFound(httpExchange);
                }
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                sendNotFound(httpExchange);
            }
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String bodyRequest = readText(httpExchange);
        if (bodyRequest.isEmpty()) {
            sendNotFound(httpExchange);
            return;
        }
        try {
            Subtask subtask = gson.fromJson(bodyRequest, Subtask.class);
            if (taskManager.getSubTask(subtask.getId()) != null) {
                taskManager.updateSubTask(subtask);
                sendText(httpExchange, "success", 200);
            } else {
                int subtaskId = taskManager.addNewSubTask(subtask);
                sendText(httpExchange, Integer.toString(subtaskId), 201);
            }
        } catch (ManagerOverlappingException v) {
            sendHasInteractions(httpExchange);
        } catch (JsonSyntaxException e) {
            sendNotFound(httpExchange);
        }
    }

    private void handleDelete(HttpExchange httpExchange, String[] path) throws IOException {
        if (path.length == 2) {
            sendNotFound(httpExchange);
        } else {
            try {
                int id = Integer.parseInt(path[2]);
                taskManager.deleteSubTask(id);
                sendText(httpExchange, "success", 200);
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                sendNotFound(httpExchange);
            }
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");


        switch (method) {
            case "GET" -> handleGet(httpExchange, path);
            case "POST" -> handlePost(httpExchange);
            case "DELETE" -> handleDelete(httpExchange, path);
            default -> sendNotFound(httpExchange);
        }
    }

}