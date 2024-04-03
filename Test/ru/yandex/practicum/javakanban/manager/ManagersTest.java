package ru.yandex.practicum.javakanban.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldReturnInitializedHistoryManager() {
        assertNotNull(Managers.getDefault(), "Менеджер не найден");
    }

    @Test
    void shouldReturnInitializedTaskManager() {
        assertNotNull(Managers.getDefaultHistory(), "Менеджер не найден");
    }
}