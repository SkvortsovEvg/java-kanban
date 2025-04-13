package task;

import enums.TaskStatus.Status;
import enums.TaskType.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    public static final LocalDateTime END_TIME = LocalDateTime.of(2000, 1, 1, 0, 0);
    private List<Subtask> subtaskList = new ArrayList<>();
    private LocalDateTime endTime = END_TIME;

    public Epic(int id,
                String name,
                String description,
                Status status,
                LocalDateTime startTime,
                long duration,
                LocalDateTime endTime) {
        super(id, name, description, status, startTime, duration);
        this.endTime = endTime;
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public void setSubtaskList(List<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Type getTaskType() {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        if (subtaskList.isEmpty()) {
            return "Epic{"
                    + "id=" + getId()
                    + ", name=\"" + getName()
                    + "\", description=\"" + getDescription()
                    + "\", status=" + getStatus()
                    + ", startTime=" + getStartTime().format(formatter)
                    + ", duration=" + getDuration()
                    + "}";
        } else {
            return "Epic{"
                    + "id=" + getId()
                    + ", name=\"" + getName()
                    + "\", description=\"" + getDescription()
                    + "\",\nsubtaskList=" + subtaskList
                    + ",\nstatus=" + getStatus()
                    + ", startTime=" + getStartTime().format(formatter)
                    + ", duration=" + getDuration()
                    + ", endTime=" + getEndTime().format(formatter)
                    + "}";
        }
    }
}
