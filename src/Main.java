import manager.Managers;
import manager.TaskManager.FileBackedTaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.nio.file.Path;

public class Main {

    private static final Path PATH = Path.of("data.csv");
    private static final File FILE = new File(PATH.toString());
    private static final FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), FILE);

    public static void main(String[] args) {

        Task cleanFloor = new Task("Пропылесосить полы", "Попробовать робота-пылесоса");
        manager.addTask(cleanFloor);

        Task cleanWindows = new Task("Помыть окна", "Чище-чище)))");
        manager.addTask(cleanWindows);

        Epic roomRenovation = new Epic("Сделать ремонт в комнате", "Управиться за 10 дней");
        manager.addEpic(roomRenovation);

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

        manager.addSubtask(roomRenovationSubtask1);
        manager.addSubtask(roomRenovationSubtask2);
        manager.addSubtask(roomRenovationSubtask3);


        Epic paintWalls = new Epic("Покрасить стены на улице", "В серый цвет");
        manager.addEpic(paintWalls);

        //loading("Произошла автоматическая запись в файл.\nТеперь произойдет чтение из файла:");
        manager.loadFromFile(FILE);
        String uploadedFile = manager.getUploadedFile();
        String[] params = uploadedFile.split("\n");
        for (String line : params) {
            Task task = manager.fromString(line);
            manager.addTaskToManagerSave(task);
        }
        System.out.println("\n==============================\n");

        manager.deleteTaskById(cleanFloor.getId());
        manager.deleteEpicById(paintWalls.getId());
        manager.deleteSubtaskById(roomRenovationSubtask1.getId());
        loading("Удалили задачи и посмотрим на файл");
        int x = 0;
    }

    public static void loading(String message) {
        System.out.println(message);
        System.out.println("\nЗадачи из файла:");
        System.out.println(manager.getAllTasks());
        System.out.println("\nЭпики из файла:");
        System.out.println(manager.getAllEpics());
        System.out.println("\nПодзадачи из файла:");
        System.out.println(manager.getAllSubtasks());
    }
}