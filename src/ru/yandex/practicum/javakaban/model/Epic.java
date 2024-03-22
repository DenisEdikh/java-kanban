package ru.yandex.practicum.javakaban.model;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIdS;

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIdS = new ArrayList<>();
    } // конструктор создания эпиков

    public Epic(String title, String description, int id) {
        super(title, description, id);
        this.subtaskIdS = new ArrayList<>();
    } // конструктор обновления эпиков

    public ArrayList<Integer> getSubtaskIdS() {
        return subtaskIdS;
    }

    public void setListOfSubtask(ArrayList<Integer> listOfSubtask) {
        this.listOfSubtask = listOfSubtask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", listOfSubtask=" + subtaskIdS +
                '}';
    }
}
