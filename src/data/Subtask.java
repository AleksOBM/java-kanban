package data;

public class Subtask extends Task {

    /// ID целевого эпика
    private Integer epicId;

    /// Конструктор
    public  Subtask(Integer id, String title, String description, Status status, Integer epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    /// Получение ID целевого эпика
    public Integer getEpicId() {
        return epicId;
    }

    /// Внесение ID целевого эпика
    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
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
