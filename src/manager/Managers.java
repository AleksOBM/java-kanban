package manager;

public class Managers {

    public static TaskManager getTaskManager(TaskManagerType type) {
        switch (type) {
            case IN_MEMORY -> {
                return new InMemoryTaskManager();
            }
            case FILE_BACKED -> {
                System.out.println("FILE_BACKED will be adding at next sprint");
                return null;
            }
            default -> {
                return null;
            }
        }
    }

    public static <T> HistoryManager<T> getHistoryManager() {
        return new InMemoryHistoryManager<>();
    }
}
