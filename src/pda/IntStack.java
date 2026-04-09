package pda;

import java.util.Arrays;

public final class IntStack {

    private int[] data = new int[8];
    private int size = 0;

    public void push(int value) {
        if (size == data.length)
            data = Arrays.copyOf(data, data.length * 2);
        data[size++] = value;
    }

    public int pop() {
        if (size == 0) throw new IllegalStateException("Stack ist leer");
        return data[--size];
    }


    public int size() { return size; }

    public int[] snapshot() {
        return Arrays.copyOf(data, size);
    }

    public static String format(int[] s) {
        if (s.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < s.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(s[i]);
        }
        return sb.append("]").toString();
    }
}
