package data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    /// Хранилище ID подзадач
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    /// Конструктор
    public Epic(Integer id, String title, String description) {
        super(id, title, description);
    }

    /// Получение всех ID подзадач
    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    /// Добавление новой ID подзадачи
    public void addSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
        subtaskIds.add(subtaskId);
    }

    /// Удаление всех ID подзадач
    public void removeAllSubtaskIds() {
        subtaskIds.clear();
    }

    /// Удаление ID подзадачи
    public void removeSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    /// Получение копии объекта
    @Override
    public Epic getCopy() {
        Epic newEpic = new Epic(this.id, this.title, this.description);
        newEpic.status = this.status;
        newEpic.startTime = this.startTime;
        newEpic.duration = this.duration;
        newEpic.endTime = this.endTime;
        newEpic.subtaskIds = this.subtaskIds;
        return newEpic;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + super.getId() + '\'' +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", startTime='" + (startTime == null ? "null" : startTime.format(DATE_TIME_FORMATTER)) + '\'' +
                ", endTime='" + (endTime == null ? "null" : endTime.format(DATE_TIME_FORMATTER)) + '\'' +
                ", duration='" + (duration == null ? "null" : duration.toMinutes()) + '\'' +
                '}';
    }
}
