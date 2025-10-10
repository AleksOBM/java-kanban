package manager;

import data.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node first;
    private Node last;

    private final Map<Integer, Node> idToNode = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();

        if (idToNode.containsKey(taskId)) {
            removeNode(idToNode.get(taskId));
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (idToNode.containsKey(id)) {
            removeNode(idToNode.get(id));
        }
    }

    private void linkLast(Task task) {
        Node newLastNode;

        if (idToNode.isEmpty()) {
            newLastNode = new Node(null, task, null);
            idToNode.put(task.getId(), newLastNode);
            first = newLastNode;
            last = newLastNode;
        } else if (last == first) {
            newLastNode = new Node(first, task, null);
            last = newLastNode;
            first.next = newLastNode;
        } else {
            newLastNode = new Node(last, task, null);
            last.next = newLastNode;
            last = newLastNode;
        }

        idToNode.put(task.getId(), newLastNode);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (first == null) {
            return tasks;
        }

        Node node = first;
        while (node.next != null) {
            tasks.add(node.data);
            node = node.next;
        }
        tasks.add(node.data);

        return tasks;
    }

    private void removeNode(Node node) {

        int taskId = node.data.getId();

        if (node.prev == null & node.next == null) {
            first = null;
            last = null;
        } else if (node.prev == null) {
            first = node.next;
            first.prev = null;
        } else if (node.next == null) {
            last = node.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        idToNode.remove(taskId);
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
