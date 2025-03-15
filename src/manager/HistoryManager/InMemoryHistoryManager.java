package manager.HistoryManager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList list = new CustomLinkedList();

    private static class CustomLinkedList {
        private final Map<Integer, Node> table = new HashMap<>();
        private Node head;
        private Node tail;

        public void linkLast(Task task) {
            Node item = new Node();
            item.setTask(task);

            if (table.containsKey(task.getId())) {
                removeNode(table.get(task.getId()));
            }

            if (head == null) {
                head = item;
                tail = item;
                item.setNext(null);
                item.setPrev(null);
            } else {
                item.setNext(null);
                item.setPrev(tail);
                tail.setNext(item);
                tail = item;
            }

            table.put(task.getId(), item);
        }

        public List<Task> getTasks() {
            List<Task> result = new ArrayList<>();
            Node item = head;
            while (item != null) {
                result.add(item.getTask());
                item = item.getNext();
            }
            return result;
        }

        public void removeNode(Node node) {
            if (node != null) {
                table.remove(node.getTask().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }

                if (prev != null) {
                    prev.setNext(next);
                }
                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }

        public Node getNode(int id) {
            return table.get(id);
        }
    }

    @Override
    public void add(Task task) {
        list.linkLast(task);
    }

    @Override
    public void remove(int id) {
        list.removeNode(list.getNode(id));
    }

    @Override
    public List<Task> getHistory() {
        return list.getTasks();
    }
}

class Node {
    private Task task;
    private Node next;
    private Node prev;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}




