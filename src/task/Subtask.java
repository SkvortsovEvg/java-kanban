package task;

import enums.TaskStatus.Status;
import enums.TaskType.Type;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicID;

    public Subtask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }

    public Subtask(int id,
                   String name,
                   String description,
                   int epicID,
                   Status status,
                   LocalDateTime startTime,
                   long duration
    ) {
        super(id, name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(String name,
                   String description,
                   Status status,
                   int epicID,
                   LocalDateTime startTime,
                   long duration
    ) {
        super(name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public Type getTaskType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{"
                + "id=" + getId()
                + ", epicID=" + epicID
                + ", name=\"" + getName()
                + "\", description=\"" + getDescription()
                + "\", status=" + getStatus()
                + ", startTime=" + getStartTime().format(formatter)
                + ", duration=" + getDuration()
                + "}";
    }
}
