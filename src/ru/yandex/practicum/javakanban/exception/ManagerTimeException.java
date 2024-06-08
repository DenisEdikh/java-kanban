package ru.yandex.practicum.javakanban.exception;

public class ManagerTimeException extends RuntimeException {
    public ManagerTimeException(String message) {
        super(message);
    }
}