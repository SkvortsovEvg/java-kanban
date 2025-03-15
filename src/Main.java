import manager.Managers;
import manager.TaskManager.InMemoryTaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Main {

    private static final InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {
        addTasks();
        printAllTasks();
        System.out.println("\n============================================================\n");
        //printAllTasks();
        //printViewHistory();
    }

    private static void addTasks() {

        Task cleanFloor = new Task("Пропылесосить полы", "Попробовать робота-пылесоса");
        inMemoryTaskManager.addTask(cleanFloor);

        Task cleanWindows = new Task("Помыть окна", "Чище-чище)))");
        inMemoryTaskManager.addTask(cleanWindows);

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
        Subtask roomRenovationSubtask3 =
                new Subtask("Сменить входную дверь",
                        "Не знаю какую только",
                        roomRenovation.getId());

        inMemoryTaskManager.addSubtask(roomRenovationSubtask1);
        inMemoryTaskManager.addSubtask(roomRenovationSubtask2);
        inMemoryTaskManager.addSubtask(roomRenovationSubtask3);


        Epic paintWalls = new Epic("Покрасить стены на улице", "В серый цвет, краску не выбрал пока");
        inMemoryTaskManager.addEpic(paintWalls);

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
            System.out.println(task + "\n===============");
        }
    }
}