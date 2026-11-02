import java.time.LocalDate;

public class Expense extends Transaction {
    public Expense(int id, double amount, String category, String description, LocalDate date) {
        super(id, amount, category, description, date);
    }

    @Override
    public String getType() { return "EXPENSE"; }
}
