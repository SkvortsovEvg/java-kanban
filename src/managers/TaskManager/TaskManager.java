package managers.TaskManager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {


    int addNewTask(Task task);

    Task getTask(int id);

    void deleteTask(int id);

    void updateTask(Task task);

    int addNewEpic(Epic epic);

    Epic getEpic(int id);

    void deleteEpic(int id);

    void updateEpic(Epic epic);

    ArrayList<SubTask> getEpicSubTasks(int id);

    int addNewSubTask(SubTask task);

    SubTask getSubTask(int id);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    void deleteAll();

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubTask();

    List<Task> getHistory();

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, SubTask> getSubTasks();

    TreeSet<Task> getPrioritizedTasks();
}
