package bearmaps.proj2ab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    private int size;
    private HashMap<T, Integer> map = new HashMap<>();
    private ArrayList<Node> items;           // store items at indices 1 to n


    private ArrayHeapMinPQ(int initcapacity) {
        items = new ArrayList<>(initcapacity);
        items.add(0, null);
        size = 0;
    }

    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException();
        }
        items.add(new Node(item, priority));
        size++;
        map.put(item, size());
        swim(size());

    }

    public boolean contains(T item) {
        return map.containsKey(item);
    }

    public ArrayHeapMinPQ() {
        this(3);
    }

    private int parent(int index) {
        return index / 2;
    }

    public T removeSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        T temp = getSmallest();
        Node bottom = items.get(size());
        map.remove(temp);
        items.set(1, bottom);
        items.remove(size());
        size--;
        if (size() == 0) {
            return temp;
        }
        map.put(bottom.item, 1);
        sink(1);
        return temp;
    }

    public T getSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return items.get(1).item;
    }

    public int size() {
        return size;
    }

    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        int index = map.get(item);
        double p = items.get(index).getPriority();
        items.get(index).setPriority(priority);
        if (p > priority) {
            swim(index);
        } else if (p < priority) {
            sink(index);
        }
    }

    private void sink(int index) {
        int leftchild = 2 * index;
        int rightchild = 2 * index + 1;
        if (rightchild <= size()) {
            if (items.get(index).priority > items.get(leftchild).priority) {
                if (items.get(index).priority > items.get(rightchild).priority) {
                    if (items.get(leftchild).priority > items.get(rightchild).priority) {
                        swap(index, rightchild);
                        sink(rightchild);
                    } else {
                        swap(index, leftchild);
                        sink(leftchild);
                    }
                } else {
                    swap(index, leftchild);
                    sink(leftchild);
                }
            }
            if (items.get(index).priority < items.get(leftchild).priority) {
                if (items.get(index).priority > items.get(rightchild).priority) {
                    swap(index, rightchild);
                    sink(rightchild);
                }
            }
        } else if (leftchild == size()) {
            if (items.get(index).priority > items.get(leftchild).priority) {
                swap(index, leftchild);
                sink(leftchild);
            }
        }
    }

    private void swim(int k) {
        int parent = parent(k);
        if (0 < parent && parent < size()) {
            if (items.get(parent).priority > items.get(k).priority) {
                swap(k, parent);
                swim(parent);
            }
        }
    }

    private void swap(int x, int y) {
        Node tempY = items.get(y);
        Node tempX = items.get(x);
        items.set(y, tempX);
        items.set(x, tempY);
        map.put(tempX.item, y);
        map.put(tempY.item, x);
    }

    private class Node {
        private T item;
        private double priority;

        Node(T t, double p) {
            this.item = t;
            this.priority = p;
        }

        T getItem() {
            return item;
        }

        double getPriority() {
            return priority;
        }

        void setPriority(double priority) {
            this.priority = priority;
        }
    }
}
