package http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import task.Epic;
import managers.TaskManager.TaskManager;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    private void handleGet(HttpExchange httpExchange, String[] path) throws IOException {
        if (path.length == 2) {
            response = gson.toJson(taskManager.getEpics());
            sendText(httpExchange, response, 200);
        } else if (path.length == 3) {
            try {
                int id = Integer.parseInt(path[2]);
                Epic epic = taskManager.getEpic(id);
                if (epic != null) {
                    response = gson.toJson(epic);
                    sendText(httpExchange, response, 200);
                } else {
                    sendNotFound(httpExchange);
                }
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                sendNotFound(httpExchange);
            }
        } else if (path.length == 4 && path[3].equals("subtasks")) {
            try {
                int id = Integer.parseInt(path[2]);
                Epic epic = taskManager.getEpic(id);
                if (epic != null) {
                    response = gson.toJson(epic);
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
            Epic epic = gson.fromJson(bodyRequest, Epic.class);
            if (taskManager.getEpic(epic.getId()) != null) {
                sendNotFound(httpExchange);
            } else {
                int epicId = taskManager.addNewEpic(epic);
                sendText(httpExchange, Integer.toString(epicId), 201);
            }
        } catch (JsonSyntaxException e) {
            sendNotFound(httpExchange);
        }
    }

    private void handleDelete(HttpExchange httpExchange, String[] path) throws IOException {
        try {
            int id = Integer.parseInt(path[2]);
            taskManager.deleteEpic(id);
            sendText(httpExchange, "success", 200);
        } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
            sendNotFound(httpExchange);
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