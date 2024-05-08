package ru.yandex.practicum.javakanban.model;

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

    public void addSubtaskId(int id) {
        subtaskIdS.add(id);
    } // добавление подзадачи

    public void removeSubtaskId(int id) {
        subtaskIdS.remove(Integer.valueOf(id));
    } // удаление задачи

    public void clearSubtaskIdS() {
        subtaskIdS.clear();
    } // удаление всех подзадач

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.EPIC;
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
