package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import task.Epic;
import task.Subtask;
import task.Task;
import enums.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import managers.TaskManager.InMemoryTaskManager;
import managers.Managers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpTaskServerTest {


    private static HttpTaskServer taskServer;
    private final HttpClient client = HttpClient.newHttpClient();
    private static InMemoryTaskManager taskManager;
    private static final Gson gson = Managers.getGson();

    private static final String BASE_URL = "http://localhost:8080";
    private static final String TASK_URL = BASE_URL + "/tasks";
    private static final String EPIC_URL = BASE_URL + "/epics";
    private static final String SUBTASK_URL = BASE_URL + "/subtasks";
    private static final String HISTORY_URL = BASE_URL + "/history";
    private static final String PRIORITIZED_URL = BASE_URL + "/prioritized";

    Type taskType = new TypeToken<HashMap<Integer, Task>>() {
    }.getType();

    Type epicType = new TypeToken<HashMap<Integer, Epic>>() {
    }.getType();

    Type subtaskType = new TypeToken<HashMap<Integer, Subtask>>() {
    }.getType();

    Type prioritizedType = new TypeToken<Set<Task>>() {
    }.getType();

    @BeforeEach
    void beforeEach() {
        try {
            taskManager = (InMemoryTaskManager) Managers.getDefaults();
            taskServer = new HttpTaskServer(taskManager);
            taskServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
    }

    private HttpResponse<String> sendPOST(String path, Task task) throws IOException, InterruptedException {
        URI uri = URI.create(path);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 201) {
            try {
                task.setId(Integer.parseInt(res.body()));
            } catch (NumberFormatException e) {
            }
        }
        return res;
    }

    private HttpResponse<String> sendGET(String path, int id) throws IOException, InterruptedException {
        HttpRequest request;
        URI uri = URI.create(id == -1 ? path : path + "/" + id);

        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDELETE(String path, int id) throws IOException, InterruptedException {
        HttpRequest request;
        URI uri = URI.create(id == -1 ? path : path + "/" + id);

        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Task[] send2Tasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", TaskStatus.NEW, "desc1", LocalDateTime.of(2024, 5, 19, 0, 0), Duration.ofMinutes(1));
        Task task2 = new Task("Task2", TaskStatus.IN_PROGRESS, "desc2", LocalDateTime.of(2024, 5, 18, 0, 0), Duration.ofMinutes(2));
        sendPOST(TASK_URL, task1);
        sendPOST(TASK_URL, task2);
        return new Task[]{task1, task2};
    }

    private Task[] sendEpicAnd3subtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("description1", "name1");
        sendPOST(EPIC_URL, epic1);

        Subtask subtask11 = new Subtask("subTask11", TaskStatus.NEW, "desc1", LocalDateTime.of(2024, 5, 17, 0, 0), Duration.ofMinutes(1));
        subtask11.setEpicId(epic1.getId());
        sendPOST(SUBTASK_URL, subtask11);

        Subtask subtask12 = new Subtask("subTask12", TaskStatus.NEW, "desc2", LocalDateTime.of(2024, 5, 16, 0, 0), Duration.ofMinutes(2));
        subtask12.setEpicId(epic1.getId());
        sendPOST(SUBTASK_URL, subtask12);

        Subtask subtask13 = new Subtask("subTask13", TaskStatus.NEW, "desc3", LocalDateTime.of(2024, 5, 15, 0, 0), Duration.ofMinutes(3));
        subtask13.setEpicId(epic1.getId());
        sendPOST(SUBTASK_URL, subtask13);

        return new Task[]{epic1, subtask11, subtask12, subtask13};
    }

    @Test
    void shouldCreateOverlappingTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", TaskStatus.NEW, "desc1", LocalDateTime.of(2024, 5, 19, 0, 0), Duration.ofMinutes(25));
        Task task2 = new Task("Task2", TaskStatus.IN_PROGRESS, "desc2", LocalDateTime.of(2024, 5, 19, 0, 0), Duration.ofMinutes(25));
        task1.setId(1);
        task2.setId(2);
        HttpResponse<String> response1 = sendPOST(TASK_URL, task1);
        assertEquals(201, response1.statusCode());
        HttpResponse<String> response2 = sendPOST(TASK_URL, task2);
        assertEquals(406, response2.statusCode());
    }

    @Test
    void shouldGetTasks() throws IOException, InterruptedException {
        send2Tasks();

        HttpResponse<String> response = sendGET(TASK_URL, -1);
        assertEquals(200, response.statusCode());
        HashMap<Integer, Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task[] tasks = send2Tasks();

        HttpResponse<String> response = sendGET(TASK_URL, 2);
        assertEquals(200, response.statusCode());
        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertEquals(tasks[1].getDescription(), responseTask.getDescription());
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        send2Tasks();

        HttpResponse<String> response = sendGET(TASK_URL, 2);
        assertEquals(200, response.statusCode());
        Task responseTask = gson.fromJson(response.body(), Task.class);
        responseTask.setStatus(TaskStatus.DONE);
        sendPOST(TASK_URL, responseTask);
        HttpResponse<String> responseAfterUpdate = sendGET(TASK_URL, 2);
        Task responseTaskAfterUpdate = gson.fromJson(responseAfterUpdate.body(), Task.class);
        assertEquals(responseTask.getStatus(), responseTaskAfterUpdate.getStatus());
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", TaskStatus.NEW, "desc1", LocalDateTime.of(2024, 5, 19, 0, 0), Duration.ofMinutes(1));
        sendPOST(TASK_URL, task1);
        Task task2 = new Task("Task2", TaskStatus.NEW, "desc2", LocalDateTime.of(2025, 5, 15, 0, 0), Duration.ofMinutes(1));
        sendPOST(TASK_URL, task2);

        HttpResponse<String> response = sendDELETE(TASK_URL, 1);
        assertEquals(200, response.statusCode());
        assertNull(taskManager.getTasks().get(1));
    }


    @Test
    void shouldGetSubtasks() throws IOException, InterruptedException {
        sendEpicAnd3subtasks();

        HttpResponse<String> response = sendGET(SUBTASK_URL, -1);
        assertEquals(200, response.statusCode());
        HashMap<Integer, Epic> actual = gson.fromJson(response.body(), subtaskType);
        assertNotNull(actual);
        assertEquals(3, actual.size());
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        Task[] tasks = sendEpicAnd3subtasks();

        HttpResponse<String> response = sendGET(SUBTASK_URL, 4);
        assertEquals(200, response.statusCode());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(tasks[3], responseSubtask);
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        Task[] tasks = sendEpicAnd3subtasks();

        tasks[3].setDescription("new_desc3");
        tasks[3].setId(4);
        sendPOST(SUBTASK_URL, tasks[3]);

        HttpResponse<String> response = sendGET(SUBTASK_URL, 4);
        assertEquals(200, response.statusCode());
        Subtask updatedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals("new_desc3", updatedSubtask.getDescription());
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        Task[] tasks = sendEpicAnd3subtasks();


        HttpResponse<String> response1 = sendDELETE(SUBTASK_URL, tasks[1].getId());
        assertEquals(200, response1.statusCode());

        HttpResponse<String> response3 = sendDELETE(SUBTASK_URL, tasks[3].getId());
        assertEquals(200, response3.statusCode());

        HttpResponse<String> responseEpic = sendGET(EPIC_URL, tasks[0].getId());
        assertEquals(200, responseEpic.statusCode());
        Epic epic = gson.fromJson(responseEpic.body(), Epic.class);
        assertEquals(1, epic.getSubTasks().size());
        assertEquals(1, taskManager.getSubTasks().size());
    }

    @Test
    void shouldGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("description1", "name1");
        Epic epic2 = new Epic("description2", "name2");
        Subtask subtask11 = new Subtask("subTask11", TaskStatus.NEW, "desc1", LocalDateTime.of(2024, 5, 17, 0, 0), Duration.ofMinutes(1));
        Subtask subtask21 = new Subtask("subTask21", TaskStatus.NEW, "desc2", LocalDateTime.of(2024, 5, 16, 0, 0), Duration.ofMinutes(2));
        sendPOST(EPIC_URL, epic1);
        sendPOST(EPIC_URL, epic2);
        sendPOST(SUBTASK_URL, subtask11);
        sendPOST(SUBTASK_URL, subtask21);

        HttpResponse<String> response = sendGET(EPIC_URL, -1);
        assertEquals(200, response.statusCode());
        HashMap<Integer, Epic> actual = gson.fromJson(response.body(), epicType);
        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("name1", "desc1");
        Epic epic2 = new Epic("name2", "desc2");
        Subtask subtask11 = new Subtask("subTask11", TaskStatus.NEW, "desc1", LocalDateTime.of(2024, 5, 17, 0, 0), Duration.ofMinutes(1));
        Subtask subtask21 = new Subtask("subTask21", TaskStatus.NEW, "desc2", LocalDateTime.of(2024, 5, 16, 0, 0), Duration.ofMinutes(2));
        sendPOST(EPIC_URL, epic1);
        sendPOST(EPIC_URL, epic2);
        sendPOST(SUBTASK_URL, subtask11);
        sendPOST(SUBTASK_URL, subtask21);

        HttpResponse<String> response = sendGET(EPIC_URL, 2);
        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic2, responseEpic);
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("description1", "name1");
        Epic epic2 = new Epic("description2", "name2");
        Epic epic3 = new Epic("description2", "name2");
        sendPOST(EPIC_URL, epic1);
        sendPOST(EPIC_URL, epic2);
        sendPOST(EPIC_URL, epic3);

        HttpResponse<String> response1 = sendDELETE(EPIC_URL, 2);
        assertEquals(200, response1.statusCode());
        assertNull(taskManager.getEpics().get(2));
        assertNotNull(taskManager.getEpics().get(1));

        HttpResponse<String> response2 = sendDELETE(EPIC_URL, 1);
        assertEquals(200, response2.statusCode());
        assertNull(taskManager.getEpics().get(2));
        assertNull(taskManager.getEpics().get(1));
    }


    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        sendEpicAnd3subtasks();

        HttpResponse<String> response = sendGET(HISTORY_URL, 0);
        assertEquals(200, response.statusCode());
        HashMap<Integer, Task> history = gson.fromJson(response.body(), taskType);
        assertNotNull(history, "История не возвращается");
    }

    @Test
    void shouldGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", TaskStatus.NEW, "desc1", LocalDateTime.of(2024, 5, 19, 0, 0), Duration.ofMinutes(1));
        Task task2 = new Task("Task2", TaskStatus.IN_PROGRESS, "desc2", LocalDateTime.of(2024, 5, 18, 0, 0), Duration.ofMinutes(2));
        Task task3 = new Task("Task3", TaskStatus.IN_PROGRESS, "desc2", LocalDateTime.of(2024, 5, 22, 0, 0), Duration.ofMinutes(3));
        sendPOST(TASK_URL, task1);
        sendPOST(TASK_URL, task2);
        sendPOST(TASK_URL, task3);

        HttpResponse<String> response = sendGET(PRIORITIZED_URL, 0);
        assertEquals(200, response.statusCode());

        TreeSet<Task> tasks = new TreeSet<>(((t1, t2) -> {
            if (t1.getEndTime().isBefore(t2.getStartTime())) {
                return -1;
            } else if (t1.getStartTime().isAfter(t2.getEndTime())) {
                return 1;
            } else {
                return 0;
            }
        }));
        tasks.addAll(gson.fromJson(response.body(), prioritizedType));
        assertNotNull(tasks, "задачи не возвращаются");
        assertEquals(3, tasks.size(), "Неверное количество задач");
        assertEquals(task2.getDescription(), tasks.getFirst().getDescription(), "Задачи не совпадают");
    }
}