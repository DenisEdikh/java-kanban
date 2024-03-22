public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        
        Task task1 = new Task("Buy bicycle", "Price < 500$");
        Task task2 = new Task("Buy auto", "Class of auto D");

        Epic epic1 = new Epic("Build house", "Very big");
        Subtask subtask11 = new Subtask("Buy bricks", "Build house", "Need 50000 pieces of bricks");
        Subtask subtask12 = new Subtask("Buy cement", "Build house", "Need 500 kg");

        Epic epic2 = new Epic("Build a swimming pool", "For children");
        Subtask subtask21 = new Subtask("Buy tiles", "Build a swimming pool", "Need 500 pieces");

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask11);
        taskManager.addNewSubtask(subtask12);

        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subtask21);

        System.out.println("tasks = " + taskManager.getAllTasks());
        System.out.println("epics = " + taskManager.getAllEpics());
        System.out.println("subtasks = " + taskManager.getAllSubtask());
        System.out.println();

        Subtask newsubtask11 = new Subtask("Buy bricks", "Build house",
                "Need 50000 pieces of bricks", "DONE", 4);
        Subtask newsubtask12 = new Subtask("Buy cement", "Build house",
                "Need 600 kg", "IN_PROGRESS", 5);

        taskManager.refreshOfSubTask(newsubtask11);
        taskManager.refreshOfSubTask(newsubtask12);

        System.out.println("epics = " + taskManager.getAllEpics());
        System.out.println("subtasks = " + taskManager.getAllSubtask());
        System.out.println();

        taskManager.removeSubtaskById(5);
        taskManager.removeEpicById(6);

        System.out.println("epics = " + taskManager.getAllEpics());
        System.out.println("subtasks = " + taskManager.getAllSubtask());
    }
}
