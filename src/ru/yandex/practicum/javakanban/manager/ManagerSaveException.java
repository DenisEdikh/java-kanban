package ru.yandex.practicum.javakanban.manager;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {
        System.out.println("Файл отсутствует!");
    }
}