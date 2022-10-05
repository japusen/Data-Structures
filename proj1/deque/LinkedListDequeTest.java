package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        LinkedListDeque<String> l = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", l.isEmpty());
		l.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, l.size());
        assertFalse("l should now contain 1 item", l.isEmpty());

		l.addLast("middle");
		assertEquals(2, l.size());

		l.addLast("back");
		assertEquals(3, l.size());

		System.out.println("Printing out deque: ");
		l.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("l should be empty upon initialization", l.isEmpty());

		l.addFirst(10);
		// should not be empty
		assertFalse("l should contain 1 item", l.isEmpty());

		l.removeFirst();
		// should be empty
		assertTrue("l should be empty after removal", l.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(3);

        l.removeLast();
        l.removeFirst();
        l.removeLast();
        l.removeFirst();

        int size = l.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String>  l = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        l.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = l.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, l.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, l.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            l.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) l.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) l.removeLast(), 0.0);
        }
    }

    @Test
    public void equalsNull() {
        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            l.addLast(i);
        }

        assertFalse("equals: null", l.equals(null));
    }

    @Test
    public void equalsPointer() {
        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            l.addLast(i);
        }

        LinkedListDeque<Integer> l2 = l;
        assertTrue("equals: same pointer", l.equals(l2));

    }

    @Test
    public void equalsDifferentSizes() {
        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            l.addLast(i);
        }

        LinkedListDeque<Integer> l2 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 5; i++) {
            l2.addLast(i);
        }

        assertFalse("equals: different sizes", l.equals(l2));

    }

    @Test
    public void equalsSameSizeDifferentValues() {
        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            l.addLast(i);
        }

        LinkedListDeque<Integer> l2 = new LinkedListDeque<Integer>();
        for (int i = 1; i < 11; i++) {
            l2.addLast(i);
        }

        assertFalse("equals: same size, different values", l.equals(l2));

    }

    @Test
    public void equalsSameCopy() {
        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            l.addLast(i);
        }

        LinkedListDeque<Integer> l2 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            l2.addLast(i);
        }

        assertTrue("equals: same", l.equals(l2));
    }

    @Test
    public void equalsSameArray() {
        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            l.addLast(i);
        }


        ArrayDeque<Integer> a = new ArrayDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            a.addLast(i);
        }

        assertTrue("equals: LLDeque = ADeque", l.equals(a));
    }

    @Test
    public void equalsDifferentArray() {
        LinkedListDeque<Integer> l = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            l.addLast(i);
        }

        ArrayDeque<Integer> a = new ArrayDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            a.addFirst(i);
        }

        assertFalse("equals: LLDeque != ADeque", l.equals(a));
    }

    @Test
    public void abstractList() {
        class Student {
            private int id;
            private String name;

            public Student(int i, String n) {
                id = i;
                name = n;
            }
            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }
        }

        LinkedListDeque<Student> students = new LinkedListDeque<>();

        Student student0 = new Student(0, "Adrian");
        Student student1 = new Student(1, "Kaitlyn");
        Student student2 = new Student(2, "Nora");
        Student student3 = new Student(3, "Alonso");
        Student student4 = new Student(4, "Darwen");
        Student student5 = new Student(5, "Bryan");
        Student student6 = new Student(6, "Natalie");

        students.addLast(student1);
        students.addLast(student2);
        students.addLast(student3);
        students.addLast(student4);
        students.addLast(student5);
        students.addLast(student6);
        students.addFirst(student0);

        Student test1 = students.get(2);
        assertEquals(2, test1.getId());
        assertEquals("Nora", test1.getName());

        Student test2 = students.removeFirst();
        assertEquals(0, test2.getId());
        assertEquals("Adrian", test2.getName());
    }
}
