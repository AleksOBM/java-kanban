package data;

import java.util.Objects;

public class Task {

    /// ID задачи
    protected Integer id;

    /// Заголовок задачи
    protected String title;

    /// Описание задачи
    protected String description;

    /// Статус задачи
    protected Status status;

    /// Конструктор 1
    public Task(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    /// Конструктор 2
    public Task(Integer id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    /// Получение ID задачи
    public Integer getId() {
        return id;
    }

    /// Внесение ID задачи
    public void setId(Integer id) {
        this.id = id;
    }

    /// Получение заголовка задачи
    public String getTitle() {
        return title;
    }

    /// Внесение заголовка задачи
    public void setTitle(String title) {
        this.title = title;
    }

    /// Получение описания задачи
    public String getDescription() {
        return description;
    }

    /// Внесение описания задачи
    public void setDescription(String description) {
        this.description = description;
    }

    /// Получение статуса задачи
    public Status getStatus() {
        return status;
    }

    /// Внесение статуса задачи
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}