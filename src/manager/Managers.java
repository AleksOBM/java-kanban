package manager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        // TODO
        //return manager.InMemoryHistoryManager.history
        return null;
    }
}
