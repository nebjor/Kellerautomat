package pda;

import java.util.function.Consumer;

public final class UpnEvaluator {
    public EvaluationResult evaluate(String input, Consumer<EvaluationStep> listener) {
        if (input == null || input.trim().isEmpty())
            return EvaluationResult.reject("Leere Eingabe.");

        String[] tokens = input.trim().split("\\s+");
        IntStack stack = new IntStack();
        int step = 0;

        for (String tok : tokens) {
            if (isNumber(tok)) {
                stack.push(Integer.parseInt(tok));
            } else if ("+".equals(tok) || "*".equals(tok)) {
                if (stack.size() < 2)
                    return EvaluationResult.reject("Zu wenig Operanden fuer '" + tok + "'.");
                int b = stack.pop(), a = stack.pop();
                stack.push("+".equals(tok) ? a + b : a * b);
            } else {
                return EvaluationResult.reject("Ungueltiges Token: '" + tok + "'.");
            }

            step++;
            if (listener != null)
                listener.accept(new EvaluationStep(step, tok, stack.snapshot()));
        }

        // ε-Übergang: Endzustandsprüfung ohne Eingabesymbol
        step++;
        if (stack.size() != 1) {
            if (listener != null)
                listener.accept(new EvaluationStep(step, "ε", stack.snapshot()));
            return EvaluationResult.reject("Stack enthaelt " + stack.size() + " Werte statt 1.");
        }

        int result = stack.pop();
        if (listener != null)
            listener.accept(new EvaluationStep(step, "ε", new int[]{result}));
        return EvaluationResult.accept(result);
    }

    private static boolean isNumber(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++)
            if (!Character.isDigit(s.charAt(i))) return false;
        return true;
    }
}
