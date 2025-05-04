package managers.HistoryManager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    CustomLinkedList historyMap = new CustomLinkedList();

    @Override
    public void add(Task task) {
        historyMap.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyMap.getTasks();
    }

    @Override
    public void remove(int id) {
        historyMap.removeNode(historyMap.getNode(id));
    }

    private static class CustomLinkedList {
        private final Map<Integer, Node> nodeMap = new HashMap<>();
        private Node head;
        private Node tail;


        private void linkLast(Task task) {
            Node element = new Node();
            element.setTask(task);

            if (nodeMap.containsKey(task.getId())) {
                removeNode(nodeMap.get(task.getId()));
            }

            if (head == null) {
                tail = element;
                head = element;
                element.setNext(null);
                element.setPrev(null);
            } else {
                element.setPrev(tail);
                element.setNext(null);
                tail.setNext(element);
                tail = element;
            }

            nodeMap.put(task.getId(), element);
        }

        private List<Task> getTasks() {
            List<Task> result = new ArrayList<>();
            Node element = head;
            while (element != null) {
                result.add(element.getTask());
                element = element.getNext();
            }
            return result;
        }

        private void removeNode(Node node) {
            if (node != null) {
                nodeMap.remove(node.getTask().getId());
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

        private Node getNode(int id) {
            return nodeMap.get(id);
        }

        private static class Node {
            public Task task;
            public Node prev;
            public Node next;

            public Task getTask() {
                return task;
            }

            public void setTask(Task task) {
                this.task = task;
            }

            public Node getPrev() {
                return prev;
            }

            public void setPrev(Node prev) {
                this.prev = prev;
            }

            public Node getNext() {
                return next;
            }

            public void setNext(Node next) {
                this.next = next;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HistoryManager manager = (HistoryManager) o;
        return getHistory().equals(manager.getHistory());
    }
}

