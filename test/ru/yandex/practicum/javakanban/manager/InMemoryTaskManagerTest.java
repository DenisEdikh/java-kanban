package ru.yandex.practicum.javakanban.manager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }
}