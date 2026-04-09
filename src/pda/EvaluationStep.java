package pda;

import java.util.Arrays;

public record EvaluationStep(int nr, String token, int[] stack) {

    public EvaluationStep(int nr, String token, int[] stack) {
        this.nr = nr;
        this.token = token;
        this.stack = Arrays.copyOf(stack, stack.length);
    }

    @Override
    public int[] stack() { return Arrays.copyOf(stack, stack.length); }
}
