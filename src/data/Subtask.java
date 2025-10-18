package data;

public class Subtask extends Task {

    /// ID целевого эпика
    private final Integer epicId;

    /// Конструктор
    public  Subtask(Integer id, String title, String description, Status status, Integer epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    /// Получение ID целевого эпика
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    /// Получение копии объекта
    public Subtask getCopy() {
        return new Subtask(this.id, this.title, this.description, this.status, this.epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + super.getId() + '\'' +
                ", epicId='" + this.epicId + '\'' +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                '}';
    }
}
