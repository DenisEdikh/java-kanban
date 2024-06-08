package ru.yandex.practicum.javakanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<Integer> subtaskIdS;
    protected LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.endTime = null;
        this.subtaskIdS = new ArrayList<>();
    } // конструктор создания эпиков

    public Epic(String title, String description, int id) {
        super(title, description, id);
        this.subtaskIdS = new ArrayList<>();
    } // конструктор обновления эпиков

    public List<Integer> getSubtaskIdS() {
        return subtaskIdS;
    }

    public void addSubtaskId(int id) {
        subtaskIdS.add(id);
    } // добавление подзадачи

    public void removeSubtaskId(int id) {
        subtaskIdS.remove(Integer.valueOf(id));
    } // удаление задачи

    public void clearSubtaskIdS() {
        subtaskIdS.clear();
    } // удаление всех подзадач

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIdS=" + subtaskIdS +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }
}
