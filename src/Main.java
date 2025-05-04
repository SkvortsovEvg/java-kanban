import task.Epic;
import task.Subtask;
import task.Task;
import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager.TaskManager;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaults();

        int taskId = taskManager.addNewTask(new Task("Новая задача", TaskStatus.NEW, "описание"));
        int taskId2 = taskManager.addNewTask(new Task("Новая задача2", TaskStatus.NEW, "описание 2"));

        Epic epic = new Epic("Новый эпик ", TaskStatus.NEW, "описание");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("подзадача1", TaskStatus.IN_PROGRESS, "sub1", epic.getId());
        taskManager.addNewSubTask(subtask);

        taskManager.addNewSubTask(new Subtask("подзадача2", TaskStatus.NEW, "sub2", epicId));
        taskManager.updateEpic(epic);

        Epic epic2 = new Epic("Новый эпик 2", TaskStatus.NEW, "описание");
        int epicId2 = taskManager.addNewEpic(epic2);

        Subtask subtask2 = new Subtask("Новый subtask", TaskStatus.NEW, "описание", epicId2);
        int subtaskId2 = taskManager.addNewSubTask(subtask2);
        Task taskUseless = taskManager.getTask(taskId); // task@1 in history
        Task epicUseless = taskManager.getEpic(epicId2); // Epic{subTasks=[SubTask@7], status=NEW} in history
        Task subtaskUseless = taskManager.getSubTask(subtaskId2); // SubTask@7 in history
        taskManager.updateEpic(epic2);


        taskManager.deleteTask(taskId);
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Map.Entry<Integer, Task> task : manager.getTasks().entrySet()) {
            System.out.println("\t" + task);
        }
        System.out.println("\nЭпики:");
        for (Map.Entry<Integer, Epic> epic : manager.getEpics().entrySet()) {
            System.out.println("\t" + epic);

            for (Task task : epic.getValue().getSubTasks()) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\nПодзадачи:");
        for (Map.Entry<Integer, Subtask> subtask : manager.getSubTasks().entrySet()) {
            System.out.println("\t" + subtask);
        }

        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println("\t" + task);
        }
    }
}
