# Kellerautomat – UPN-Rechner

Deterministischer Kellerautomat zur Auswertung von **UPN** (Umgekehrte Polnische Notation).

## Features

- Eigener Stack (`IntStack`) – kein `java.util.Stack`
- Operatoren `+` und `*`
- Kompakte Eingabe (`23+4*`) und Token-Eingabe mit mehrstelligen Zahlen (`31 78 + 1214 +`)
- Konsole: **Step-Modus** (1 Sek. Pause pro Schritt) und **Lauf-Modus**
- GUI mit **animierter Stack-Darstellung**

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

### CLI Modes
```powershell
java -cp out Main step "<expression>"    # Step-Modus (1s Pause pro Schritt)
java -cp out Main lauf "<expression>"    # Lauf-Modus (alle Schritte sofort)
java -cp out Main cli                    # Interaktiver Modus
java -cp out Main test                   # Schnelltest
```
