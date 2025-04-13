import enums.Status;
import manager.Managers;
import manager.TaskManager.InMemoryTaskManager;
import manager.TaskManager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Main {

    private static final InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {
        addTasks();
        printAllTasks();
        printViewHistory();
    }

    private static void addTasks() {

        Task cleanFloor = new Task("Пропылесосить полы", "Попробовать робота-пылесоса");
        inMemoryTaskManager.addTask(cleanFloor);

        Task washFloor = new Task(cleanFloor.getId(),
                "Помыть полы",
                "Используем СУПЕР-средство",
                Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(washFloor);

        inMemoryTaskManager.addTask(new Task("Купить фрукты", "Яблоки - 1 кг, Груши - 1 кг"));

        Epic roomRenovation = new Epic("Сделать ремонт в комнате", "Управиться за 10 дней");
        inMemoryTaskManager.addEpic(roomRenovation);

        Subtask roomRenovationSubtask1 =
                new Subtask("Покрасить стены",
                        "Что-то светлое или немного желтое",
                        roomRenovation.getId());
        Subtask roomRenovationSubtask2 =
                new Subtask("Собрать комод",
                        "Лучше слева от входа",
                        roomRenovation.getId());

        inMemoryTaskManager.addSubtask(roomRenovationSubtask1);
        inMemoryTaskManager.addSubtask(roomRenovationSubtask2);

        roomRenovationSubtask1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(roomRenovationSubtask1);
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : inMemoryTaskManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики:");
        for (Epic epic : inMemoryTaskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : inMemoryTaskManager.getEpicSubtasks(epic)) {
                System.out.println("=====> " + task);
            }
        }

        System.out.println("\nПодзадачи:");
        for (Subtask subtask : inMemoryTaskManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void printViewHistory() {
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getSubtaskById(4);
        inMemoryTaskManager.getSubtaskById(5);
        inMemoryTaskManager.getSubtaskById(4);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getSubtaskById(4);

        System.out.println("\n\n\nВыводим историю просмотров:");
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(task+"\n===============");
        }
    }
}