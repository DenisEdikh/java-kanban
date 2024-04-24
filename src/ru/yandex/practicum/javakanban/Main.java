package ru.yandex.practicum.javakanban;

import ru.yandex.practicum.javakanban.manager.InMemoryTaskManager;
import ru.yandex.practicum.javakanban.manager.TaskManager;
import ru.yandex.practicum.javakanban.model.Epic;
import ru.yandex.practicum.javakanban.model.Subtask;
import ru.yandex.practicum.javakanban.model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new InMemoryTaskManager();
        int idT1 = taskManager.addNewTask(new Task("T1", "t1"));
        int idT2 = taskManager.addNewTask(new Task("T2", "t2"));
        int idE1 = taskManager.addNewEpic(new Epic("E1", "e1"));
        int idS1 = taskManager.addNewSubtask(new Subtask("S1", "s1", idE1));
        int idS2 = taskManager.addNewSubtask(new Subtask("S2", "s2", idE1));
        int idS3 = taskManager.addNewSubtask(new Subtask("S3", "s3", idE1));
        int idE2 = taskManager.addNewEpic(new Epic("E2", "e2"));

        taskManager.getTaskById(idT1);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getTaskById(idT2);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getEpicById(idE1);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getTaskById(idT1);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getTaskById(idT2);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getSubtaskById(idS2);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getSubtaskById(idS1);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getTaskById(idT1);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getSubtaskById(idS3);
        System.out.println("tasks" + taskManager.getHistory());
        taskManager.getSubtaskById(idS1);
        System.out.println("tasks" + taskManager.getHistory());

        taskManager.removeTaskById(idT1);
        System.out.println("tasks" + taskManager.getHistory());

        taskManager.removeEpicById(idE1);
        System.out.println("tasks" + taskManager.getHistory());

    }
}
