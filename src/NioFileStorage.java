import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NioFileStorage implements Storage {
    private final Path file;

    public NioFileStorage(String fileName) {
        this.file = Paths.get(fileName);
    }

    @Override
    public void save(List<Transaction> transactions) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        List<String> lines = new ArrayList<>();
        for (Transaction transaction : transactions) lines.add(transaction.toDataLine());
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    @Override
    public List<Transaction> load() throws IOException {
        List<Transaction> result = new ArrayList<>();
        if (!Files.exists(file)) return result;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            try {
                String[] fields = line.split("\\|", -1);
                if (fields.length != 6) continue;
                int id = Integer.parseInt(fields[0]);
                String type = fields[1];
                double amount = Double.parseDouble(fields[2]);
                String category = Transaction.decode(fields[3]);
                String description = Transaction.decode(fields[4]);
                LocalDate date = LocalDate.parse(fields[5]);
                if (amount <= 0) continue;
                if ("EXPENSE".equals(type)) result.add(new Expense(id, amount, category, description, date));
                else if ("INCOME".equals(type)) result.add(new Income(id, amount, category, description, date));
            } catch (RuntimeException ignored) {
                // Skip malformed rows so one damaged line does not block the other records.
            }
        }
        return result;
    }
}
