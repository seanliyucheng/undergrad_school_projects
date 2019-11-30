import org.junit.Test;

import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome() {
        String a = "racecar";
        String x = "racecAr";
        String b = "noon";
        String c = "horse";
        String d = "rancor";
        String e = "";
        String f = "a";
        assertFalse(palindrome.isPalindrome(x));
        assertTrue(palindrome.isPalindrome(a));
        assertTrue(palindrome.isPalindrome(b));
        assertFalse(palindrome.isPalindrome(c));
        assertFalse(palindrome.isPalindrome(d));
        assertTrue(palindrome.isPalindrome(e));
        assertTrue(palindrome.isPalindrome(f));
    }

    @Test
    public void testOverloadedIsPalindrome() {
        CharacterComparator comparator = new OffByOne();
        String x = "racecar";
        String a = "flake";
        String hey = "flaKE";
        String b = "horse";
        String c = "";
        String d = "a";
        String z = "Flake";
        String l = "$a#";
        String h = "#a$";
        assertTrue(palindrome.isPalindrome(h, comparator));
        assertTrue(palindrome.isPalindrome(l, comparator));
        assertFalse(palindrome.isPalindrome(hey, comparator));
        assertTrue(palindrome.isPalindrome(a, comparator));
        assertFalse(palindrome.isPalindrome(b, comparator));
        assertFalse(palindrome.isPalindrome(z, comparator));
        assertFalse(palindrome.isPalindrome(x, comparator));
        assertTrue(palindrome.isPalindrome(c, comparator));
        assertTrue(palindrome.isPalindrome(d, comparator));
    }
}
