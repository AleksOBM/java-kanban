package manager;

import java.io.File;

public class Managers {

    public static TaskManager getTaskManager(TaskManagerType type) {
        switch (type) {
            case IN_MEMORY -> {
                return new InMemoryTaskManager();
            }
            case FILE_BACKED -> {
                return FileBackedTaskManager.getInstance(new File(
                        "src/autosave/",
                        "data.csv"
                ));
            }
            default -> {
                return null;
            }
        }
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
