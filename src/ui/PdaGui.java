package ui;

import pda.EvaluationResult;
import pda.EvaluationStep;
import pda.IntStack;
import pda.UpnEvaluator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class PdaGui extends JFrame {

    private static final Color LABEL_COLOR = new Color(107, 114, 128);

    private final UpnEvaluator evaluator = new UpnEvaluator();
    private final StackPanel stackPanel = new StackPanel();
    private final JTextField exprField = new JTextField("3 4 + 6 2 + 8 9 + 4 3 + * * *");
    private final JComboBox<String> modeBox = new JComboBox<>(new String[]{"step", "lauf"});
    private final JSpinner delaySpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 5000, 100));
    private final JTextArea log = new JTextArea();
    private final JLabel resultLabel = new JLabel("Bereit", SwingConstants.LEFT);
    private final JButton startBtn = new JButton("Start");
    private final JButton stopBtn  = new JButton("Stop");
    private final JButton clearBtn = new JButton("Clear");

    private final StackAnimationPlayer player;

    public PdaGui() {
        super("Deterministischer Kellerautomat – UPN");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 620);
        setLocationRelativeTo(null);

        player = new StackAnimationPlayer(new StackAnimationPlayer.Listener() {
            @Override public void onStep(EvaluationStep step) {
                String label = "ε".equals(step.token()) ? " (ε-Übergang)" : "";
                appendLog("Schritt " + step.nr()
                        + " | '" + step.token()
                        + "' | Stack: " + IntStack.format(step.stack())
                        + label);
                stackPanel.setStep(step);
            }
            @Override public void onDone() {
                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
            }
        });

        setLayout(new BorderLayout(10, 10));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        stopBtn.setEnabled(false);
        clearBtn.setEnabled(false);

        startBtn.addActionListener(e -> run());
        stopBtn.addActionListener(e -> {
            player.stop();
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        });
        clearBtn.addActionListener(e -> {
            player.stop();
            stackPanel.setSnapshot(new int[0]);
            log.setText("");
            resultLabel.setText("Bereit");
            resultLabel.setForeground(LABEL_COLOR);
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            clearBtn.setEnabled(false);
        });
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));

        JLabel title = new JLabel("UPN-Auswertung mit animiertem Keller");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        p.add(title, BorderLayout.NORTH);

        JPanel row = new JPanel();
        row.add(new JLabel("UPN:"));
        exprField.setPreferredSize(new Dimension(380, 28));
        row.add(exprField);
        row.add(new JLabel("Modus:"));
        row.add(modeBox);
        row.add(new JLabel("Delay (ms):"));
        row.add(delaySpinner);
        row.add(startBtn);
        row.add(stopBtn);
        row.add(clearBtn);
        p.add(row, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        stackPanel.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219)));
        p.add(stackPanel, BorderLayout.WEST);

        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(log);
        sp.setBorder(BorderFactory.createTitledBorder("Ablauf"));
        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 14f));
        p.add(resultLabel, BorderLayout.CENTER);
        return p;
    }

    private void run() {
        String expr = exprField.getText().trim();
        if (expr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte UPN eingeben.");
            return;
        }

        player.stop();
        stackPanel.setSnapshot(new int[0]);
        log.setText("");
        resultLabel.setText("…");
        resultLabel.setForeground(LABEL_COLOR);

        List<EvaluationStep> steps = new ArrayList<>();
        EvaluationResult result = evaluator.evaluate(expr, steps::add);

        if ("step".equals(modeBox.getSelectedItem())) {
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            player.start(steps, (int) delaySpinner.getValue());
            showResult(result);
        } else {
            startBtn.setEnabled(false);
            Timer flash = new Timer(150, e -> {
                for (EvaluationStep s : steps) {
                    String label = "ε".equals(s.token()) ? " (ε-Übergang)" : "";
                    appendLog("Schritt " + s.nr()
                            + " | '" + s.token()
                            + "' | Stack: " + IntStack.format(s.stack())
                            + label);
                }
                if (!steps.isEmpty())
                    stackPanel.setSnapshot(steps.get(steps.size() - 1).stack());
                showResult(result);
                startBtn.setEnabled(true);
            });
            flash.setRepeats(false);
            flash.start();
        }
    }

    private void showResult(EvaluationResult r) {
        clearBtn.setEnabled(true);
        if (r.accepted()) {
            resultLabel.setForeground(new Color(5, 150, 105));
            resultLabel.setText("Akzeptiert. Resultat = " + r.value());
        } else {
            resultLabel.setForeground(new Color(185, 28, 28));
            resultLabel.setText("Nicht akzeptiert: " + r.error());
        }
    }

    private void appendLog(String text) {
        if (!log.getText().isEmpty()) log.append("\n");
        log.append(text);
        log.setCaretPosition(log.getDocument().getLength());
    }
}
