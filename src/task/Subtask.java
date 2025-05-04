package task;

import enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, TaskStatus status, String description) {
        super(name, status, description);
    }

    public SubTask(String name, TaskStatus status, String description, LocalDateTime startTime, Duration duration) {
        super(name, status, description, startTime, duration);
    }

    public SubTask(String name, TaskStatus status, String description, int epicId) {
        super(name, status, description);
        this.epicId = epicId;
    }

    public SubTask(String name, TaskStatus status, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, status, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public SubTask clone() {
        SubTask task = new SubTask(getName(), getStatus(), getDescription(), epicId);
        task.setId(getId());
        return task;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", epicID=" + getEpicId() +
                '}';
    }

    @Override
    public String toCSV() {
        return "SUBTASK," +
                getId() + "," +
                getName() + "," +
                getDescription() + "," +
                getStatus() + "," +
                getStartTime() + "," +
                getDuration().toMinutes() + "," +
                getEpicId();
}
}
