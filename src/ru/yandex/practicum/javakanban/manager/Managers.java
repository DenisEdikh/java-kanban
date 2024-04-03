package ru.yandex.practicum.javakanban.manager;

public final class Managers {

    private static HistoryManager historyManager;
    private static TaskManager taskManager;

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }

    public static TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager();
        }
        return taskManager;
    }
}
