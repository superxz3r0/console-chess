
# ♟️ Console Chess Implementation(Java)

### Developer  
**Name:** Meshach George Mathew  
**Roll Number:** 25204391  

---

## 🧠 Overview

This project is a **console-based Chess game** written in **Java**, developed as part of the Individual Project 2025.  
It allows two players to play a complete game of chess in the terminal, implementing full rule logic such as:

- Standard chess movement for all pieces  
- Castling (kingside and queenside)  
- En passant  
- Pawn promotion with player choice  
- Check and checkmate detection  
- Hint and pip commands for legal move display  
- Resign feature to end the game early  

The system demonstrates key **object-oriented programming (OOP)** principles such as **inheritance**, **encapsulation**, and **composition**, and all logic has been validated using **JUnit 5** tests.

---

## ⚙️ Requirements

Before running the project, make sure you have:

- **Java JDK 17** (or later)  
- **JUnit 5 Console Standalone JAR** (already included: `junit-platform-console-standalone-1.10.2.jar`)  
- A command-line terminal (Windows PowerShell, Command Prompt, or macOS/Linux Terminal)

---

## 🚀 How to Run the Chess Game

### 1️⃣ Compile all source files

From the project root folder (`console-chess`):

**Windows CMD:**

```bash
javac -d out src\*.java
````

**PowerShell or macOS/Linux:**

```bash
javac -d out src/*.java
```

This compiles all Java files into the `out` directory.

---

### 2️⃣ Run the game

**Windows CMD:**

```bash
java -cp out Main
```

**PowerShell or macOS/Linux:**

```bash
java -cp out Main
```

✅ The chessboard will display in the console.
Players take turns typing moves in **algebraic format**, for example:

```
e2e4
e7e5
g1f3
b8c6
```

---

### 3️⃣ Optional Commands in the Game

| Command   | Description                                                                                           |
| --------- | ----------------------------------------------------------------------------------------------------- |
| `hint`    | Shows all legal moves for the current player                                                          |
| `pip e2`  | Shows all legal moves for a specific piece (e.g., pawn at e2)                                         |
| `resign`  | Ends the game immediately                                                                             |
| Promotion | When a pawn reaches the last rank, the program asks which piece to promote to (`Q`, `R`, `B`, or `N`) |

---

## 🧪 How to Run the JUnit Tests

This project includes **14 automated tests** for movement, special rules, and checkmate detection.

1️⃣ Compile both `src` and `test` folders:

```bash
javac -d out -cp junit-platform-console-standalone-1.10.2.jar src\*.java test\*.java
```

2️⃣ Run all tests:

```bash
java -jar junit-platform-console-standalone-1.10.2.jar --class-path out --scan-class-path
```

✅ You should see output like:

```
> java -jar junit-platform-console-standalone-1.10.2.jar --class-path out --scan-class-path

Thanks for using JUnit! Support its development at https://junit.org/sponsoring

.
+-- JUnit Jupiter [OK]
| +-- SpecialMovesTest [OK]
| | +-- whiteKingsideCastlingMinimal() [OK]
| | '-- enPassantImmediateCapture() [OK]
| +-- BoardCheckTest [OK]
| | +-- noCheckWhenBlocked() [OK]
| | '-- detectsCheckAlongOpenFile() [OK]
| +-- CheckmateTest [OK]
| | '-- foolsMate() [OK]
| +-- PositionTest [OK]
| | +-- equalityAndHash() [OK]
| | +-- parsesAndPrintsAlgebraic() [OK]
| | '-- badSquaresThrow() [OK]
| +-- PromotionTest [OK]
| | '-- straightPromotionChoice() [OK]
| +-- HintAndPipTest [OK]
| | '-- legalMovesFromSquare() [OK]
| '-- PieceMovementTest [OK]
|   +-- knightLeapsOver() [OK]
|   +-- pawnSingleAndDoubleFromStart() [OK]
|   +-- bishopInitiallyBlocked() [OK]
|   '-- cannotCaptureOwnPiece() [OK]
+-- JUnit Vintage [OK]
'-- JUnit Platform Suite [OK]

Test run finished after 182 ms
[        10 containers found      ]
[         0 containers skipped    ]
[        10 containers started    ]
[         0 containers aborted    ]
[        10 containers successful ]
[         0 containers failed     ]
[        14 tests found           ]
[         0 tests skipped         ]
[        14 tests started         ]
[         0 tests aborted         ]
[        14 tests successful      ]
[         0 tests failed          ]


```

*Developed by Meshach George Mathew (Roll No. 25204391)*
*Individual Project 2025 – Console Chess (Java)*


