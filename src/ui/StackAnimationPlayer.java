package ui;

import pda.EvaluationStep;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public final class StackAnimationPlayer {

    public interface Listener {
        void onStep(EvaluationStep step);
        void onDone();
    }

    private final Listener listener;
    private final Timer timer;
    private List<EvaluationStep> steps = new ArrayList<>();
    private int index = 0;

    public StackAnimationPlayer(Listener listener) {
        this.listener = listener;
        this.timer = new Timer(900, e -> tick());
        this.timer.setInitialDelay(0);
    }

    public void start(List<EvaluationStep> newSteps, int delayMs) {
        stop();
        steps = new ArrayList<>(newSteps);
        index = 0;
        timer.setDelay(Math.max(100, delayMs));
        if (steps.isEmpty()) { listener.onDone(); return; }
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void tick() {
        if (index >= steps.size()) {
            timer.stop();
            listener.onDone();
            return;
        }
        listener.onStep(steps.get(index++));
    }
}
