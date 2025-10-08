package manager;

import data.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node first;
    private Node last;

    private final Map<Integer, Node> idToNode = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        List<Task> history = List.copyOf(getTasks());
        if (history.isEmpty()) {
            return null;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();

        if (idToNode.isEmpty()) {
            Node newNode = new Node(null, task, null);
            idToNode.put(task.getId(), newNode);
            first = newNode;
        } else if (idToNode.containsKey(taskId)) {
            removeNode(idToNode.get(taskId));
            linkLast(task);
        } else {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (idToNode.containsKey(id)) {
            removeNode(idToNode.get(id));
        }
    }

    private void linkLast(Task task) {
        Node oldLastNode;
        Node newLastNode;

        if (last == null) {
            oldLastNode = first;
        } else {
            oldLastNode = last;
        }

        newLastNode = new Node(oldLastNode, task, null);
        oldLastNode.next = newLastNode;
        last = newLastNode;

        idToNode.put(task.getId(), newLastNode);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (first == null) {
            return tasks;
        }

        tasks.add(first.data);

        Node node = first;
        while (node.next != null) {
            tasks.add(node.next.data);
            node = node.next;
        }

        return tasks;
    }

    private void removeNode(Node node) {

        int taskId = node.data.getId();

        if (node.prev == null & node.next == null) {
            idToNode.remove(taskId);
            first = null;
        } else if (node.prev == null) {
            first = node.next;
            first.prev = null;
            idToNode.remove(taskId);
        } else if (node.next == null) {
            last = node.prev;
            last.next = null;
            idToNode.remove(taskId);
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            idToNode.remove(taskId);
        }
    }

    static class Node {

        public Node prev;
        public Task data;
        public Node next;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(data, node.data);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(data);
        }
    }
}
