package ru.yandex.practicum.javakaban.model;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    } // конструктор создания новой задачи


    public Task(String title, String description, String status, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = Status.valueOf(status);
    } // конструктор для обновления задачи/подзадачи по верному id

    public Task(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    } // конструктор для обновления эпиков

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

    @Override
    public String toString() {
        return "ru.yandex.practicum.javakaban.model.Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
