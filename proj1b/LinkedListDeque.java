public class LinkedListDeque<T> implements Deque<T> {

    private Node sentinel;
    private int size;
    public LinkedListDeque() {
        sentinel = new Node(null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public LinkedListDeque(LinkedListDeque other) {
        sentinel = new Node(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;

        for (int i = 0; i < other.size(); i++) {
            addLast((T) other.get(i));
        }
    }

    @Override
    public void addFirst(T item) {
        Node n = new Node(item);
        n.next = sentinel.next;
        n.prev = sentinel;
        sentinel.next.prev = n;
        sentinel.next = n;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node n = new Node(item);
        n.next = sentinel;
        n.prev = sentinel.prev;
        sentinel.prev.next = n;
        sentinel.prev = n;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node temp = sentinel.next;
        while (temp != sentinel) {
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
    }

    @Override
    public T removeFirst() {

        T r = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        if (size() != 0) {
            size -= 1;
        }
        return r;
    }

    @Override
    public T removeLast() {
        T l = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        if (size() != 0) {
            size -= 1;
        }
        return l;
    }

    @Override
    public T get(int index) {
        Node temp = sentinel.next;
        int i = 0;
        while (i != index) {
            temp = temp.next;
            i++;
        }
        return temp.item;
    }

    public T getRecursive(int index) {
        return getRecursive(index, sentinel.next);
    }

    private T getRecursive(int i, Node temp) {
        if (i == 0) {
            return temp.item;
        }
        return getRecursive(i - 1, temp.next);
    }

    public class Node {
        private Node prev;
        private Node next;
        private T item;

        public Node(T i) {
            prev = null;
            next = null;
            item = i;
        }
    }
}
