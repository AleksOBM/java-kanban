package data;

public enum Endpoint {

    BASE,

    // Задачи
    GET_TASK, // tasks/{id}
    GET_ALL_TASKS, // tasks
    POST_NEW_TASK, // tasks/{id}
    POST_UPDATE_TASK, // tasks
    DELETE_TASK, // tasks/{id}

    // Подзадачи
    GET_SUBTASK, // subtasks/{id}
    GET_ALL_SUBTASKS, // subtasks
    POST_NEW_SUBTASK, // subtasks
    POST_UPDATE_SUBTASK, // subtasks/{id}
    DELETE_SUBTASK, // subtasks{id}

    GET_ALL_SUBTASKS_BY_EPIC, // epic/{id}/subtasks

    // Эпики
    GET_EPIC, // epics/{id}
    GET_ALL_EPICS, // epics
    POST_NEW_EPIC, // epics
    POST_UPDATE_EPIC, // epics/{id}
    DELETE_EPIC, // epics/{id}

    // История
    GET_HISTORY, // history

    // Приоритеты
    GET_PRIORITIZED, // prioritized

    // Пустой
    UNKNOWN
}
