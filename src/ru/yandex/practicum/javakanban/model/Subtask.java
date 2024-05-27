package ru.yandex.practicum.javakanban.model;

import java.time.LocalDateTime;

public class Subtask extends Task {
    protected int epicId;

    // Конструктор по созданию задачи
    public Subtask(String title,
                   String description,
                   int epicId,
                   LocalDateTime startTime,
                   long duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String title,
                   String description,
                   Status status,
                   int id,
                   int epicId,
                   LocalDateTime startTime,
                   long duration) {
        super(title, description, status, id, startTime, duration);
        this.epicId = epicId;
    } // конструктор по обновлению подзадачи

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", epicId=" + epicId +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
