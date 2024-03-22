package ru.yandex.practicum.javakaban.model;

public class Subtask extends Task {
    protected String epic;
    protected int epicId;

    public Subtask(String title, String epic, String description) {
        super(title, description);
        this.epic = epic;
        this.status = Status.NEW;
    } //конструктор по созданию подзадачи

    public Subtask(String title, String epic, String description, String status, int id) {
        super(title, description, status, id);
        this.epic = epic;
    } // конструктор по обновлению подзадачи

    public String getEpic() {
        return epic;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "ru.yandex.practicum.javakaban.model.Subtask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                ", epic='" + epic + '\'' +
                '}';
    }
}
