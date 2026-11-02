import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final List<Transaction> TRANSACTIONS = new ArrayList<>();
    private static final Storage STORAGE = new NioFileStorage("data/transactions.txt");

    public static void main(String[] args) {
        try {
            TRANSACTIONS.addAll(STORAGE.load());
            System.out.println("Loaded " + TRANSACTIONS.size() + " saved transaction(s).\n");
        } catch (IOException e) {
            System.out.println("Could not load saved data: " + e.getMessage());
        }
        AutoSaveThread autoSave = new AutoSaveThread(TRANSACTIONS, STORAGE);
        autoSave.start();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = INPUT.nextLine().trim();
            try {
                switch (choice) {
                    case "1": addTransaction(false); break;
                    case "2": addTransaction(true); break;
                    case "3": viewTransactions(); break;
                    case "4": showSummary(); break;
                    case "5": searchByCategory(); break;
                    case "6": deleteTransaction(); break;
                    case "7": saveNow(); break;
                    case "8": running = false; break;
                    default: System.out.println("Choose a number from 1 to 8.");
                }
            } catch (IOException e) {
                System.out.println("File operation failed: " + e.getMessage());
            }
            System.out.println();
        }
        autoSave.stopSaving();
        try { saveNow(); } catch (IOException e) {
            System.out.println("Could not save before exit: " + e.getMessage());
        }
        System.out.println("Goodbye!");
        INPUT.close();
    }

    private static void printMenu() {
        System.out.println("=== Personal Expense Tracker ===");
        System.out.println("1. Add expense\n2. Add income\n3. View transactions\n4. Financial summary");
        System.out.println("5. Search by category\n6. Delete transaction\n7. Save data\n8. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addTransaction(boolean income) {
        try {
            System.out.print("Amount: ₹");
            double amount = Double.parseDouble(INPUT.nextLine().trim());
            if (!Double.isFinite(amount) || amount <= 0) throw new InvalidAmountException("Amount must be greater than zero.");
            System.out.print("Category: ");
            String category = requiredText(INPUT.nextLine(), "Category");
            System.out.print("Description: ");
            String description = INPUT.nextLine().trim();
            int id = nextId();
            Transaction transaction = income
                    ? new Income(id, amount, category, description, LocalDate.now())
                    : new Expense(id, amount, category, description, LocalDate.now());
            synchronized (TRANSACTIONS) { TRANSACTIONS.add(transaction); }
            System.out.println((income ? "Income" : "Expense") + " added with ID " + id + ".");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number for the amount.");
        } catch (InvalidAmountException | IllegalArgumentException e) {
            System.out.println("Invalid transaction: " + e.getMessage());
        }
    }

    private static String requiredText(String value, String label) {
        String text = value.trim();
        if (text.isEmpty()) throw new IllegalArgumentException(label + " cannot be empty.");
        return text;
    }

    private static int nextId() {
        synchronized (TRANSACTIONS) {
            return TRANSACTIONS.stream().mapToInt(Transaction::getId).max().orElse(0) + 1;
        }
    }

    private static void viewTransactions() {
        List<Transaction> snapshot = snapshot();
        if (snapshot.isEmpty()) { System.out.println("No transactions recorded yet."); return; }
        System.out.printf("%-4s %-8s %-12s %-14s %-12s %s%n", "ID", "TYPE", "AMOUNT", "CATEGORY", "DATE", "DESCRIPTION");
        for (Transaction transaction : snapshot) transaction.display();
    }

    private static void showSummary() {
        double income = 0, expense = 0;
        for (Transaction transaction : snapshot()) {
            if (transaction instanceof Income) income += transaction.getAmount();
            else expense += transaction.getAmount();
        }
        System.out.printf("Total income:   ₹%.2f%nTotal expenses: ₹%.2f%nBalance:        ₹%.2f%n", income, expense, income - expense);
    }

    private static void searchByCategory() {
        System.out.print("Category to search: ");
        String query = INPUT.nextLine().trim();
        boolean found = false;
        for (Transaction transaction : snapshot()) {
            if (transaction.getCategory().equalsIgnoreCase(query)) { transaction.display(); found = true; }
        }
        if (!found) System.out.println("No transactions found in that category.");
    }

    private static void deleteTransaction() {
        System.out.print("Transaction ID to delete: ");
        try {
            int id = Integer.parseInt(INPUT.nextLine().trim());
            boolean removed;
            synchronized (TRANSACTIONS) { removed = TRANSACTIONS.removeIf(t -> t.getId() == id); }
            System.out.println(removed ? "Transaction deleted." : "No transaction has that ID.");
        } catch (NumberFormatException e) { System.out.println("Please enter a valid whole number."); }
    }

    private static void saveNow() throws IOException {
        STORAGE.save(snapshot());
        System.out.println("Transactions saved to data/transactions.txt.");
    }

    private static List<Transaction> snapshot() {
        synchronized (TRANSACTIONS) { return new ArrayList<>(TRANSACTIONS); }
    }
}
