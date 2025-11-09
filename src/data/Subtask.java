package data;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    /// ID целевого эпика
    private final Integer epicId;

    /// Конструктор
    public  Subtask(
            Integer id,
            String title,
            String description,
            Status status,
            Integer epicId,
            LocalDateTime startTime,
            Duration duration
    ) {
        super(id, title, description, status);
        this.epicId = epicId;
        this.startTime = startTime;
        setDuration(duration);
    }

    /// Получение ID целевого эпика
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    /// Получение копии объекта
    @Override
    public Subtask getCopy() {
        return new Subtask(
                this.id,
                this.title,
                this.description,
                this.status,
                this.epicId,
                this.startTime,
                this.duration
        );
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + super.getId() + '\'' +
                ", epicId='" + this.epicId + '\'' +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", startTime='" + (startTime == null ? "null" : startTime.format(DATE_TIME_FORMATTER)) + '\'' +
                ", endTime='" + (endTime == null ? "null" : endTime.format(DATE_TIME_FORMATTER)) + '\'' +
                ", duration='" + (duration == null ? "null" : duration.toMinutes()) + '\'' +
                '}';
    }
}
