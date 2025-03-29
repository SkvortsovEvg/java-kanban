package task;

import enums.TaskStatus.Status;
import enums.TaskType.Type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    public static final LocalDateTime START_TIME
            = LocalDateTime.of(2000, 1, 1, 0, 0);
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private int id = 1;
    private final String name;
    private final String description;
    private Status status = Status.NEW;
    private LocalDateTime startTime = START_TIME;
    private long duration = 0L;

    public Task(String name, String description, Status status, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Type getTaskType() {
        return Type.TASK;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return id == otherTask.id;
    }

    @Override
    public int hashCode() {
        int hash = 97;
        if (name != null) {
            hash += name.hashCode();
        }
        hash *= 31;
        if (description != null) {
            hash += description.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "Task{id=" + id
                + ", name=\"" + name
                + "\", description=\"" + description
                + "\", status=" + status
                + ", startTime=" + startTime.format(formatter)
                + ", duration=" + duration
                + "}";
    }
}
