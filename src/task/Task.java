package task;

import java.time.Duration;
import java.time.LocalDateTime;

import enums.TaskStatus;
import enums.TaskType;
import http.adapters.LocalDateTimeAdapter;

public class Task {

    private int id;
    private String name;
    private TaskStatus status;
    private String description;
    private LocalDateTime startTime; // LocalDateTime
    private long duration;
    private LocalDateTime endTime;

    public Task(String name, TaskStatus status, String description) {
        this(name, status, description, LocalDateTime.now(), Duration.ZERO);
    }

    public Task(String name, TaskStatus status, String description, LocalDateTime startTime, long duration) {
        this(name, status, description, startTime, Duration.ofMinutes(duration));
    }

    public Task(String name, TaskStatus status, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.status = status;
        this.description = description;

        this.startTime = startTime;
        this.duration = duration.toMinutes();
        this.endTime = startTime.plus(duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public Task clone() {
        Task task = new Task(name, status, description, startTime, Duration.ofMinutes(duration));
        task.setId(id);
        return task;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", name=\"" + getName() + "\"" +
                ", description=\"" + getDescription() + "\"" +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime().format(LocalDateTimeAdapter.formatter) +
                ", duration=" + getDuration().toMinutes() +
                ", endTime=" + getEndTime().format(LocalDateTimeAdapter.formatter) +
                '}';
    }

    public String toCSV() {
        return "TASK," + getId() + "," + getName() + "," + getDescription() + "," + getStatus() + "," +
                startTime + "," + duration;
    }

    public static Task fromString(String value) {
        final String[] columns = value.split(",");
        TaskType type = TaskType.valueOf(columns[0]);
        int id = Integer.parseInt(columns[1]);
        String name = columns[2];
        String description = columns[3];
        TaskStatus status = TaskStatus.valueOf(columns[4]);
        LocalDateTime startTime = LocalDateTime.parse(columns[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(columns[6]));
        return switch (type) {
            case TASK -> {
                Task t = new Task(name, status, description, startTime, duration);
                t.setId(id);
                yield t;
            }
            case SUBTASK -> {
                int epic = Integer.parseInt(columns[7]);
                SubTask t = new SubTask(name, status, description, epic, startTime, duration);
                t.setId(id);
                yield t;
            }
            case EPIC -> {
                Epic t = new Epic(name, status, description, startTime, duration);
                t.setId(id);
                yield t;
            }
        };
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return Duration.ofMinutes(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration.toMinutes();
        this.endTime = startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        this.endTime = startTime.plus(Duration.ofMinutes(duration));
    }

}
