package ru.yandex.practicum.javakanban.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(Throwable cause) {
        super(cause);
    }
}