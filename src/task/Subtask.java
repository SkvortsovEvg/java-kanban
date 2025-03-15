package task;

import enums.TaskStatus.Status;

public class Subtask extends Task {
    private final int epicID;

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public Subtask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "Subtask{"
                + "id=" + getId()
                + ", epicID=" + epicID
                + ", name=\"" + getName()
                + "\", description=\"" + getDescription()
                + "\", status=" + getStatus()
                + "}";
    }
}
