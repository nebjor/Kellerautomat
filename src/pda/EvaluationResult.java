package pda;

public final class EvaluationResult {

    private final boolean accepted;
    private final int value;
    private final String error;

    private EvaluationResult(boolean accepted, int value, String error) {
        this.accepted = accepted;
        this.value = value;
        this.error = error;
    }

    public static EvaluationResult accept(int value)   { return new EvaluationResult(true, value, null); }
    public static EvaluationResult reject(String msg)  { return new EvaluationResult(false, 0, msg); }

    public boolean accepted() { return accepted; }
    public int value()        { return value; }
    public String error()     { return error; }
}
