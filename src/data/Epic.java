package data;

import java.util.ArrayList;

public class Epic extends Task {

    /// Хранилище ID подзадач
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    /// Конструктор
    public Epic(Integer id, String title, String description) {
        super(id, title, description);
    }

    /// Получение всех ID подзадач
    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    /// Добавление новой ID подзадачи
    public void addSubtaskId(Integer subtaskId) {
        subtaskIds.add(subtaskId);
    }

    /// Удаление всех ID подзадач
    public void removeAllSubTaskIds() {
        subtaskIds.clear();
    }

    /// Удаление ID подзадачи
    public void removeSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + super.getId() + '\'' +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                '}';
    }
}
