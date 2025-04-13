package task;

import enums.Status;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public String toString() {
        return "Epic{"
                + "id=" + getId()
                + ", name=\"" + getName()
                + "\", description=\"" + getDescription()
                + "\",\nsubtaskList=" + subtaskList
                + ",\nstatus=" + getStatus()
                + "}";
    }
}
