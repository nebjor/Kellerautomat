import pda.EvaluationResult;
import pda.EvaluationStep;
import pda.IntStack;
import pda.UpnEvaluator;
import ui.PdaGui;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String ANSI_RESET  = "\u001B[0m";
    private static final String ANSI_GREEN  = "\u001B[32m";
    private static final String ANSI_RED    = "\u001B[31m";
    private static final String ANSI_CYAN   = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BOLD   = "\u001B[1m";

    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(() -> new PdaGui().setVisible(true));
            return;
        }

        String command = args[0].toLowerCase();

        switch (command) {
            case "step":
                if (args.length < 2) {
                    System.out.println(ANSI_RED + "Fehler: Ausdruck fehlt. Beispiel: java Main step \"3 4 + 2 *\"" + ANSI_RESET);
                    return;
                }
                runStep(args[1]);
                break;

            case "lauf":
                if (args.length < 2) {
                    System.out.println(ANSI_RED + "Fehler: Ausdruck fehlt. Beispiel: java Main lauf \"3 4 + 2 *\"" + ANSI_RESET);
                    return;
                }
                runLauf(args[1]);
                break;

            case "cli":
                runInteractive();
                break;

            case "test":
                runTests();
                break;

            default:
                System.out.println(ANSI_RED + "Unbekannter Befehl: " + command + ANSI_RESET);
                printUsage();
                break;
        }
    }

    // Step-Modus
    private static void runStep(String expr) {
        System.out.println(ANSI_BOLD + "=== Step-Modus ===" + ANSI_RESET);
        System.out.println("Ausdruck: " + ANSI_CYAN + expr + ANSI_RESET);
        System.out.println();

        UpnEvaluator evaluator = new UpnEvaluator();
        List<EvaluationStep> steps = new ArrayList<>();
        EvaluationResult result = evaluator.evaluate(expr, steps::add);

        for (EvaluationStep s : steps) {
            printStep(s);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

        System.out.println();
        printResult(result);
    }

    // Lauf-Modus
    private static void runLauf(String expr) {
        System.out.println(ANSI_BOLD + "=== Lauf-Modus ===" + ANSI_RESET);
        System.out.println("Ausdruck: " + ANSI_CYAN + expr + ANSI_RESET);
        System.out.println();

        UpnEvaluator evaluator = new UpnEvaluator();
        List<EvaluationStep> steps = new ArrayList<>();
        EvaluationResult result = evaluator.evaluate(expr, steps::add);

        for (EvaluationStep s : steps) {
            printStep(s);
        }

        System.out.println();
        printResult(result);
    }

    // Interaktiver CLI-Modus
    private static void runInteractive() {
        Scanner scanner = new Scanner(System.in);
        UpnEvaluator evaluator = new UpnEvaluator();

        System.out.println(ANSI_BOLD + "╔═════════════════════════════════════════════╗");
        System.out.println("║  Kellerautomat - UPN-Rechner (Interaktiv)   ║");
        System.out.println("╚═════════════════════════════════════════════╝" + ANSI_RESET);
        System.out.println();
        System.out.println("Befehle:");
        System.out.println("  UPN-Ausdruck eingeben   -> Auswertung im Lauf-Modus");
        System.out.println("  " + ANSI_YELLOW + "step <ausdruck>" + ANSI_RESET + "        -> Auswertung im Step-Modus");
        System.out.println("  " + ANSI_YELLOW + "quit" + ANSI_RESET + "                   -> Beenden");
        System.out.println();

        while (true) {
            System.out.print(ANSI_CYAN + "upn> " + ANSI_RESET);
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) continue;
            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                System.out.println("Auf Wiedersehen!");
                break;
            }

            boolean stepMode = false;
            String expr = line;
            if (line.toLowerCase().startsWith("step ")) {
                stepMode = true;
                expr = line.substring(5).trim();
            }

            List<EvaluationStep> steps = new ArrayList<>();
            EvaluationResult result = evaluator.evaluate(expr, steps::add);

            System.out.println();
            for (EvaluationStep s : steps) {
                printStep(s);
                if (stepMode) {
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                }
            }

            System.out.println();
            printResult(result);
            System.out.println();
        }
    }

    // Schnelltest
    private static void runTests() {
        System.out.println(ANSI_BOLD + "=== Schnelltest ===" + ANSI_RESET);
        System.out.println();

        String[][] tests = {
            {"3 4 +",                         "7"},
            {"3 4 + 2 *",                     "14"},
            {"5 3 + 8 2 + *",                 "80"},
            {"3 4 + 6 2 + 8 9 + 4 3 + * * *", "6664"},
            {"31 78 + 987 + 1214 + 7 +",      "2317"},
            {"42",                             "42"},
        };

        String[] failTests = {
            "",           // leere Eingabe
            "+",          // zu wenig Operanden
            "3 4 + 2",   // Stack hat mehr als 1 Wert
            "3 abc +",   // ungültiges Token
        };

        int passed = 0, failed = 0;
        UpnEvaluator evaluator = new UpnEvaluator();

        for (String[] t : tests) {
            String expr = t[0];
            int expected = Integer.parseInt(t[1]);
            EvaluationResult result = evaluator.evaluate(expr, null);

            if (result.accepted() && result.value() == expected) {
                System.out.println(ANSI_GREEN + "  ✓ " + ANSI_RESET + padRight(expr, 40) + " = " + expected);
                passed++;
            } else {
                System.out.println(ANSI_RED + "  ✗ " + ANSI_RESET + padRight(expr, 40)
                        + " erwartet " + expected + ", bekam "
                        + (result.accepted() ? result.value() : result.error()));
                failed++;
            }
        }

        System.out.println();
        System.out.println(ANSI_BOLD + "Ablehnungstests:" + ANSI_RESET);

        for (String expr : failTests) {
            EvaluationResult result = evaluator.evaluate(expr, null);
            String display = expr.isEmpty() ? "(leer)" : expr;
            if (!result.accepted()) {
                System.out.println(ANSI_GREEN + "  ✓ " + ANSI_RESET + padRight(display, 40) + " → " + result.error());
                passed++;
            } else {
                System.out.println(ANSI_RED + "  ✗ " + ANSI_RESET + padRight(display, 40) + " sollte abgelehnt werden!");
                failed++;
            }
        }

        System.out.println();
        System.out.println(ANSI_BOLD + "Ergebnis: " + ANSI_RESET
                + ANSI_GREEN + passed + " bestanden" + ANSI_RESET + ", "
                + (failed > 0 ? ANSI_RED : ANSI_GREEN) + failed + " fehlgeschlagen" + ANSI_RESET);
    }

    // Hilfsfunktionen
    private static void printStep(EvaluationStep s) {
        String tokenDisplay = "ε".equals(s.token())
                ? ANSI_BOLD + "ε" + ANSI_RESET
                : ANSI_YELLOW + padRight(s.token(), 5) + ANSI_RESET;
        String label = "ε".equals(s.token()) ? " (ε-Übergang)" : "";
        System.out.println("  Schritt " + s.nr()
                + "  │  Token: " + tokenDisplay
                + "  │  Stack: " + ANSI_CYAN + IntStack.format(s.stack()) + ANSI_RESET
                + label);
    }

    private static void printResult(EvaluationResult r) {
        if (r.accepted()) {
            System.out.println(ANSI_GREEN + ANSI_BOLD + "✓ Akzeptiert. Resultat = " + r.value() + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + ANSI_BOLD + "✗ Nicht akzeptiert: " + r.error() + ANSI_RESET);
        }
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }

    private static void printUsage() {
        System.out.println();
        System.out.println(ANSI_BOLD + "Verwendung:" + ANSI_RESET);
        System.out.println("  java Main                      → GUI starten");
        System.out.println("  java Main step \"3 4 + 2 *\"     → Step-Modus (1s Pause)");
        System.out.println("  java Main lauf \"3 4 + 2 *\"     → Lauf-Modus (alles sofort)");
        System.out.println("  java Main cli                  → Interaktiver Modus");
        System.out.println("  java Main test                 → Schnelltest");
    }
}
