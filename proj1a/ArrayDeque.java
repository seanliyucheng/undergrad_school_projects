public class ArrayDeque<T> {
    private T[] d;
    private int size;
    private int frontsent;
    private int backsent;

    public ArrayDeque() {
        d = (T[]) new Object[8];
        size = 0;
        frontsent = 0;
        backsent = 1;
    }

    private boolean isFull() {
        return !(this.size < d.length);
    }

    public int size() {
        return size;
    }

    private int moveback(int i) {
        return Math.floorMod(i - 1, d.length);
    }

    private int moveforward(int i) {
        return Math.floorMod(i + 1, d.length);
    }

    public void addFirst(T item) {
        if (isFull()) {
            resize("getbigger");
        }
        d[frontsent] = item;
        frontsent = moveback(frontsent);
        size++;
    }

    public void addLast(T item) {
        if (isFull()) {
            resize("getbigger");
        }
        d[backsent] = item;
        backsent = moveforward(backsent);
        size++;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    private boolean needshrink() {
        return size() < d.length / 4 && d.length >= 16;
    }

    private void resize(String a) {
        if (a.equals("getbigger")) {
            T[] temp = (T[]) new Object[2 * d.length];
            if (frontsent < backsent && size < d.length) {
                System.arraycopy(d, frontsent + 1, temp, 1, size());
            } else {
                System.arraycopy(d, frontsent + 1, temp, 1, d.length - frontsent - 1);
                System.arraycopy(d, 0, temp, d.length - frontsent,
                        size() - (d.length - frontsent - 1));
            }
            d = temp;
        } else {
            T[] temp = (T[]) new Object[d.length / 2];
            if (frontsent < backsent) {
                System.arraycopy(d, frontsent + 1, temp, 1, size());
            } else {
                System.arraycopy(d, frontsent + 1, temp, 1, d.length - frontsent - 1);
                System.arraycopy(d, 0, temp, d.length - frontsent,
                        size() - (d.length - frontsent - 1));
            }
            d = temp;
        }

        frontsent = 0;
        backsent = size() + 1;
    }

    public T removeFirst() {
        if (needshrink()) {
            resize("getsmaller");
        }
        frontsent = moveforward(frontsent);
        T temp = d[frontsent];
        d[frontsent] = null;
        if (size != 0) {
            size -= 1;
        }
        return temp;
    }

    public T removeLast() {
        if (needshrink()) {
            resize("getbigger");
        }
        backsent = moveback(backsent);
        T temp = d[backsent];
        d[backsent] = null;
        if (size != 0) {
            size -= 1;
        }
        return temp;
    }

    public T get(int index) {
        return d[Math.floorMod(frontsent + index + 1, d.length)];
    }

    public void printDeque() {
        int j = Math.floorMod(frontsent + 1, d.length);
        for (int i = 0; i < size(); i++) {
            System.out.print(d[j] + " ");
            j = moveforward(j);
        }
    }

    public ArrayDeque(ArrayDeque other) {
        d = (T[]) new Object[8];
        size = 0;
        frontsent = 0;
        backsent = 1;
        for (int i = 0; i < other.size(); i++) {
            addLast((T) other.get(i));
        }
    }
}
