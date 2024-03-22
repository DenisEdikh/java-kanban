import ru.yandex.practicum.javakaban.manager.TaskManager;
import ru.yandex.practicum.javakaban.model.Epic;
import ru.yandex.practicum.javakaban.model.Status;
import ru.yandex.practicum.javakaban.model.Subtask;
import ru.yandex.practicum.javakaban.model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Buy bicycle", "Price < 500$");
        int t1 = taskManager.addNewTask(task1);

        Task task2 = new Task("Buy auto", "Class of auto D");
        int t2 = taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Build house", "Very big");
        int e1 = taskManager.addNewEpic(epic1);

        Subtask subtask11 = new Subtask("Buy bricks", "Need 50000 pieces of bricks", e1);
        int s11 = taskManager.addNewSubtask(subtask11);

        Subtask subtask12 = new Subtask("Buy cement", "Need 500 kg", e1);
        int s12 = taskManager.addNewSubtask(subtask12);

        Epic epic2 = new Epic("Build a swimming pool", "For children");
        int e2 = taskManager.addNewEpic(epic2);

        Subtask subtask21 = new Subtask("Buy tiles", "Need 500 pieces", e2);
        int s21 = taskManager.addNewSubtask(subtask21);

        Epic newEpic3 = new Epic("123", "qwe", e1);


        System.out.println("tasks = " + taskManager.getAllTasks());
        System.out.println("epics = " + taskManager.getAllEpics());
        System.out.println("subtasks = " + taskManager.getAllSubtask());
        System.out.println();

        Subtask newsubtask11 = new Subtask("Buy bricks", "Need 50000 pieces of bricks", Status.DONE, s11);
        taskManager.updateSubTask(newsubtask11);

        Subtask newsubtask12 = new Subtask("Buy cement", "Need 600 kg", Status.IN_PROGRESS, s12);
        taskManager.updateSubTask(newsubtask12);

        System.out.println("epics = " + taskManager.getAllEpics());
        System.out.println("subtasks = " + taskManager.getAllSubtask());
        System.out.println();

        taskManager.removeEpicById(e2);
        taskManager.removeSubtaskById(s11);

        System.out.println("epics = " + taskManager.getAllEpics());
        System.out.println("subtasks = " + taskManager.getAllSubtask());
        System.out.println();
        
        taskManager.updateEpic(newEpic3);
        System.out.println("epics = " + taskManager.getAllEpics());
    }
}
