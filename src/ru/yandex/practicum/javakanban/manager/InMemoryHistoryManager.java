package ru.yandex.practicum.javakanban.manager;

import ru.yandex.practicum.javakanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;

    public class Node {
        private Node prev;
        private Task task;
        private Node next;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }

        Node getPrev() {
            return prev;
        }

        Node getNext() {
            return next;
        }

//        @Override
//        public String toString() {
//            return "Node{" +
//                    "prev=" + prev +
//                    ", task=" + task +
//                    ", next=" + next +
//                    '}';
//        }
    }

    Node getHead() {
        return head;
    }

    Node getTail() {
        return tail;
    }

    Node linkLast(Task task) { // Метод добавления узла в связной список
        final Node oldTail = tail;
        final Node newNode = new Node(tail, task, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        return tail;
    }

    ArrayList<Task> getTasks() { // Метод предоставления задач в виде списка
        final Node oldHead = head;
        final ArrayList<Task> tasks = new ArrayList<>();

        for (Node i = oldHead; i != null; i = i.next) {
            tasks.add(i.task);
        }
        return tasks;
    }

    void removeNode(Node node) { // Метод удаления узла
        final Node prev = node.prev;
        final Node next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.task = null;
    }

    private final Map<Integer, Node> history = new HashMap<>();

    @Override
    public void addTaskToHistory(Task task) { // Метод добавления задачи в историю
        if (task != null) {
            if (history.containsKey(task.getId())) {
                removeNode(history.get(task.getId()));
            }
            history.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public List<Task> getHistory() { // Метод получения списка с историей просмотров
        return getTasks();
    }

    @Override
    public void remove(int id) { // Метод удаления из истории задачи
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

}
