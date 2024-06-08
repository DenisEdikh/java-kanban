package ru.yandex.practicum.javakanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    protected int epicId;

    // Конструктор по созданию задачи
    public Subtask(String title,
                   String description,
                   LocalDateTime startTime,
                   Duration duration,
                   int epicId) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String title,
                   String description,
                   Status status,
                   LocalDateTime startTime,
                   Duration duration,
                   int epicId,
                   int id) {
        super(title, description, status, startTime, duration, id);
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
