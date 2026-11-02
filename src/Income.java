import java.time.LocalDate;

public class Income extends Transaction {
    public Income(int id, double amount, String category, String description, LocalDate date) {
        super(id, amount, category, description, date);
    }

    @Override
    public String getType() { return "INCOME"; }
}
