package data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task implements Comparable<Task> {

    /// ID задачи
    protected Integer id;

    /// Заголовок задачи
    protected String title;

    /// Описание задачи
    protected String description;

    /// Статус задачи
    protected Status status;

    /// Дата и время, когда предполагается приступить к выполнению задачи
    protected LocalDateTime startTime;

    /// Продолжительность задачи в минутах
    protected Duration duration;

    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    /// Конструктор 3
    public Task(
            Integer id,
            String title,
            String description,
            Status status,
            LocalDateTime startTime,
            Duration duration
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    /// Дата и время начала выполнения задачи
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /// Внесение времени начала выполнения
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /// Время выполнения задачи
    public Duration getDuration() {
        return duration;
    }

    /// Внесение продолжительности задачи
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    /// Получение типа объекта
    public Type getType() {
        return Type.TASK;
    }

    /// Получение копии объекта
    public Task getCopy() {
        return new Task(this.id, this.title, this.description, this.status, this.startTime, this.duration);
    }

    @Override
    public int compareTo(Task task) {
        return Integer.compare(this.id, task.id);
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
                ", status='" + status + '\'' +
                ", startTime='" + (startTime == null ? "null" : startTime.format(DATE_TIME_FORMATTER)) + '\'' +
                ", endTime='" + (getEndTime() == null ? "null" : getEndTime().format(DATE_TIME_FORMATTER)) + '\'' +
                ", duration='" + (duration == null ? "null" : duration.toMinutes()) + '\'' +
                '}';
    }
}