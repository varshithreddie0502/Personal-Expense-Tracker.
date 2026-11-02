import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoSaveThread extends Thread {
    private final List<Transaction> transactions;
    private final Storage storage;
    private volatile boolean running = true;

    public AutoSaveThread(List<Transaction> transactions, Storage storage) {
        super("expense-tracker-auto-save");
        this.transactions = transactions;
        this.storage = storage;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(30_000);
                if (!running) break;
                saveSnapshot();
                System.out.println("[Auto-save] Transactions saved.");
            } catch (InterruptedException e) {
                if (running) continue;
                break;
            } catch (IOException e) {
                System.out.println("[Auto-save] Could not save: " + e.getMessage());
            }
        }
    }

    public void stopSaving() {
        running = false;
        interrupt();
    }

    private void saveSnapshot() throws IOException {
        List<Transaction> snapshot;
        synchronized (transactions) {
            snapshot = new ArrayList<>(transactions);
        }
        storage.save(snapshot);
    }
}
