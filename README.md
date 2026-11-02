# Personal Expense Tracker

A beginner friendly console application written in Java. It records income and expenses, searches and deletes transactions, calculates a balance, and saves records locally.

## Requirements

- JDK 8 or later (JDK 17 recommended)
- VS Code (the Java Extension Pack is optional for compiling from the terminal)

## Open and run in VS Code

1. Open this `PersonalExpenseTracker` folder in VS Code.
2. Open **Terminal → New Terminal** in VS Code.
3. From this folder, compile the source files:

   ```powershell
   javac -encoding UTF-8 -d out src\*.java
   ```

4. Run the program:

   ```powershell
   java -cp out Main
   ```

The app creates `data/transactions.txt` when it first saves. It loads saved records on the next start. Choose menu option 8 to exit and save.

## Included Java concepts

- Inheritance: `Expense` and `Income` extend `Transaction`
- Method overriding: both transaction types override `getType()`
- Interfaces: `Displayable` and `Storage`
- Collections: `ArrayList<Transaction>`
- Multithreading: periodic background saving in `AutoSaveThread`
- Exception handling: invalid amounts and file/input errors
- Java NIO: `Path`, `Files`, and UTF-8 file operations in `NioFileStorage`

All source files are in `src/` and use the default package to keep this first project easy to compile.
