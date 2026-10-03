import java.io.*;

/** Saves/loads the whole trading state to a file (Java serialization). */
public class DataStore {
    public static class State implements Serializable {
        private static final long serialVersionUID = 1L;
        public final Market market;
        public final User user;
        public State(Market market, User user) { this.market = market; this.user = user; }
    }

    private static final String FILE = "portfolio.dat";

    public static void save(Market m, User u) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeObject(new State(m, u));
            System.out.println("Data saved to " + FILE);
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    public static State load() {
        File f = new File(FILE);
        if (!f.exists()) return null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (State) in.readObject();
        } catch (Exception e) {
            System.out.println("Saved data unreadable, starting fresh. (" + e.getMessage() + ")");
            return null;
        }
    }
}
