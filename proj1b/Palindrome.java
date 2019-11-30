public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        ArrayDeque<Character> d = new ArrayDeque<>();
        for (int i = 0; i < word.length(); i++) {
            d.addLast(word.charAt(i));
        }
        return d;
    }

    public boolean isPalindrome(String word) {
        ArrayDeque<Character> temp;
        temp = (ArrayDeque<Character>) wordToDeque(word);
        if (temp.size() <= 1) {
            return true;
        }
        if (temp.size() == 2) {
            return temp.get(0) == temp.get(1);
        }
        for (int i = 0; i < temp.size() / 2; i++) {
            if (!temp.get(i).equals(temp.get(temp.size() - i - 1))) {
                return false;
            }
        }
        return true;
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        ArrayDeque<Character> temp;
        temp = (ArrayDeque<Character>) wordToDeque(word);
        if (temp.size() <= 1) {
            return true;
        }
        if (temp.size() == 2) {
            return cc.equalChars(temp.get(0), temp.get(1));
        }
        for (int i = 0; i < temp.size() / 2; i++) {
            if (!cc.equalChars(temp.get(i), (temp.get(temp.size() - i - 1)))) {
                return false;
            }
        }
        return true;
    }
}
