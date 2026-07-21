# Kellerautomat – UPN-Rechner

Deterministischer Kellerautomat zur Auswertung von UPN (Umgekehrte Polnische Notation) mit grafischer und textueller Darstellung.

Ein Lehrbeispiel zum Verständnis von Kellerautomaten und deren Funktionsweise. Die Anwendung verarbeitet mathematische Ausdrücke in UPN schrittweise und macht die Stack-Operationen sichtbar.

## Interfaces

### CLI
![img_1.png](img_1.png)

### GUI
![img.png](img.png)

## Features

- Eigener Stack (`IntStack`) statt `java.util.Stack`
- Operatoren `+` und `*`
- Token-Eingabe mit mehrstelligen Zahlen, durch Leerzeichen getrennt (z. B. `31 78 + 1214 +`)
- CLI mit drei Modi: Step (Pause pro Schritt), Lauf (alle Schritte sofort) und interaktiv
- Swing-GUI mit animierter Stack-Darstellung

## Voraussetzungen

- Java Development Kit (JDK) 17 oder höher (die Klasse `EvaluationStep` nutzt ein `record`)
- Terminal/Konsole für Kompilierung

## Klassen

| Package | Datei | Beschreibung |
|---|---|---|
| `pda` | `IntStack` | Eigener Keller/Stack |
| `pda` | `UpnEvaluator` | PDA-Logik |
| `pda` | `EvaluationStep` | Ein Berechnungsschritt |
| `pda` | `EvaluationResult` | Ergebnis (akzeptiert/abgelehnt) |
| `ui` | `PdaGui` | Swing-GUI |
| `ui` | `StackPanel` | Animierte Stack-Zeichnung |
| `ui` | `StackAnimationPlayer` | Zeitsteuerung der Animation |
| – | `Main` | Einstiegspunkt |

## Starten

**Compile:**
```powershell
javac -encoding UTF-8 -d out src\Main.java src\pda\*.java src\ui\*.java
```

**Run:**

### GUI (Standard)
```powershell
java -cp out Main
```
Startet das grafische Interface mit animierter Stack-Visualisierung.

### CLI-Modi

#### Step-Modus (mit Pausen)
```powershell
java -cp out Main step "3 4 + 2 *"
```
Wertet den Ausdruck schrittweise aus, mit einer Sekunde Pause zwischen den Schritten.

#### Lauf-Modus (direkt)
```powershell
java -cp out Main lauf "3 4 + 2 *"
```
Führt alle Schritte direkt hintereinander aus.

#### Interaktiver Modus
```powershell
java -cp out Main cli
```
Eingabe von Ausdrücken in einer interaktiven Kommandozeile. Ein vorangestelltes `step ` schaltet für die jeweilige Eingabe in den Step-Modus; `quit` beendet.

#### Test
```powershell
java -cp out Main test
```
Führt vordefinierte Test- und Ablehnungsfälle aus.

## Beispiel

Ausdruck `3 4 + 2 *` = 14.

### GUI
Das grafische Interface zeigt die animierte Stack-Visualisierung. Mit jedem Verarbeitungsschritt wird der Stack aktualisiert.

### CLI Step-Modus
```
=== Step-Modus ===
Ausdruck: 3 4 + 2 *

  Schritt 1  │  Token: 3      │  Stack: [3]
  Schritt 2  │  Token: 4      │  Stack: [3, 4]
  Schritt 3  │  Token: +      │  Stack: [7]
  Schritt 4  │  Token: 2      │  Stack: [7, 2]
  Schritt 5  │  Token: *      │  Stack: [14]
  Schritt 6  │  Token: ε  │  Stack: [14] (ε-Übergang)

✓ Akzeptiert. Resultat = 14
```

Der abschliessende ε-Übergang prüft, ob genau ein Wert auf dem Stack liegt. Bleibt mehr als ein Wert übrig oder tritt ein ungültiges Token auf, wird die Eingabe verworfen.
