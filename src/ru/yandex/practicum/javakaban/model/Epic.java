package ru.yandex.practicum.javakaban.model;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> listOfSubtask;

    public Epic(String title, String description) {
        super(title, description);
        this.listOfSubtask = new ArrayList<>();
    } // конструктор создания эпиков

    public Epic(String title, String description, int id) {
        super(title, description, id);
        this.listOfSubtask = new ArrayList<>();
    } // конструктор обновления эпиков

    public ArrayList<Integer> getListOfSubtask() {
        return listOfSubtask;
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
                ", listOfSubtask=" + listOfSubtask +
                '}';
    }
}
