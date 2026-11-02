import java.io.IOException;
import java.util.List;

public interface Storage {
    void save(List<Transaction> transactions) throws IOException;
    List<Transaction> load() throws IOException;
}
