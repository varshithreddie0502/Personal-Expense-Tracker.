import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Transaction implements Displayable {
    protected final int id;
    protected final double amount;
    protected final String category;
    protected final String description;
    protected final LocalDate date;

    protected Transaction(int id, double amount, String category, String description, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public abstract String getType();

    @Override
    public void display() {
        System.out.printf("%-4d %-8s ₹%10.2f %-14s %-12s %s%n",
                id, getType(), amount, category, date.format(DateTimeFormatter.ISO_DATE), description);
    }

    public String toDataLine() {
        return String.join("|", Integer.toString(id), getType(), Double.toString(amount),
                encode(category), encode(description), date.toString());
    }

    private static String encode(String value) {
        return value.replace("%", "%25").replace("|", "%7C").replace("\n", "%0A").replace("\r", "%0D");
    }

    public static String decode(String value) {
        return value.replace("%0D", "\r").replace("%0A", "\n").replace("%7C", "|").replace("%25", "%");
    }
}
