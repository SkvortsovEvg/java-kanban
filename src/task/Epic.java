package task;

import enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, TaskStatus.NEW, description);
    }

    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description);
    }

    public Epic(String name, TaskStatus status, String description, LocalDateTime startTime, Duration duration) {
        super(name, status, description, startTime, duration);
    }

    public void addSubTask(Subtask subTask) {
        subTask.setEpicId(getId());
        subtasks.add(subTask);
        calculateEpicStatus();
    }

    public ArrayList<Subtask> getSubTasks() {
        return subtasks;
    }

    public void removeTask(Subtask subTask) {
        subtasks.remove(subTask);
        calculateEpicStatus();
    }

    public void calculateEpicStatus() {
        boolean isdone = true;
        boolean isnew = true;

        LocalDateTime first = LocalDateTime.MAX;
        Duration duration = Duration.ZERO;

        for (Subtask sTask : subtasks) {
            if (first.isAfter(sTask.getStartTime())) {
                first = sTask.getStartTime();
            }
            duration = duration.plus(sTask.getDuration());

            if (sTask.getStatus() != TaskStatus.DONE) {
                isdone = false;
            }

            if (sTask.getStatus() != TaskStatus.NEW) {
                isnew = false;
            }
        }
        if (isnew) {
            setStatus(TaskStatus.NEW);
            return;
        }
        if (isdone) {
            setStatus(TaskStatus.DONE);
            return;
        }
        setStatus(TaskStatus.IN_PROGRESS);

        setStartTime(first);
        setDuration(duration);
    }

    @Override
    public Epic clone() {
        Epic task = new Epic(getName(), getStatus(), getDescription());
        task.setId(getId());
        for (Subtask t : getSubTasks()) {
            task.addSubTask(t);
        }
        task.calculateEpicStatus();
        return task;
    }

    @Override
    public String toString() {
        if (subtasks.isEmpty()) {
            return "Epic{"
                    + "id=" + getId()
                    + ", name=\"" + getName()
                    + "\", description=\"" + getDescription()
                    + "\", status=" + getStatus()
                    + ", startTime=" + getStartTime()
                    + ", duration=" + getDuration()
                    + "}";
        } else {
            return "Epic{"
                    + "id=" + getId()
                    + ", name=\"" + getName()
                    + "\", description=\"" + getDescription()
                    + "\",\nsubtaskList=" + subtasks
                    + ",\nstatus=" + getStatus()
                    + ", startTime=" + getStartTime()
                    + ", duration=" + getDuration()
                    + ", endTime=" + getEndTime()
                    + "}";
        }
    }

    @Override
    public String toCSV() {
        StringBuilder subtasks = new StringBuilder();
        if (!getSubTasks().isEmpty()) {
            for (Subtask sub : getSubTasks()) {
                subtasks.append(",").append(sub.getId());
            }
        }
        return String.join(",",
                "EPIC",
                String.valueOf(getId()),
                getName(),
                getDescription(),
                getStatus().name(),
                String.valueOf(getStartTime()),
                String.valueOf(getDuration().toMinutes())
        ) + subtasks;
    }
}