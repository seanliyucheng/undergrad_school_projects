import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestArrayDequeGold {

    /*Inspired by StudentArrayDequeLauncher
     * @source StudentArrayDequeLauncher
     * */
    @Test
    public void taskone() {
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();
        String message = "";
        int i = 0;
        while (true) {
            int temp = StdRandom.uniform(0, 4);
            if (temp == 0) {
                message = message + "addFirst(" + i + ")\n";
                student.addFirst(i);
                solution.addFirst(i);
                i++;
            }
            if (temp == 1) {
                message = message + "addLast(" + i + ")\n";
                student.addLast(i);
                solution.addLast(i);
                i++;
            }
            if (temp == 2) {
                if (solution.size() > 0) {
                    message = message + "removeFirst()\n";
                    assertEquals(message, solution.removeFirst(), student.removeFirst());
                    i++;
                }
            }
            if (temp == 3) {
                if (solution.size() > 0) {
                    message = message + "removeLast()\n";
                    assertEquals(message, solution.removeLast(), student.removeLast());
                    i++;
                }
            }
        }

    }
}
