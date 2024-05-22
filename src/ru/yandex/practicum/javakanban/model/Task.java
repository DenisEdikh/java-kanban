package ru.yandex.practicum.javakanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    // конструктор создания новой задачи
    public Task(String title, String description, LocalDateTime startTime, long duration) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    // конструктор для обновления задачи/подзадачи по верному id

    public Task(String title, String description, Status status, int id, LocalDateTime startTime, long duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    // конструктор только для создания эпиков (отдельно для создания задачи не используется)
    protected Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    // конструктор только для обновления эпиков
    protected Task(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TypeOfTask getType() {
        return TypeOfTask.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Task task = (Task) object;

        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
