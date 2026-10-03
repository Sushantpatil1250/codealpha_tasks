import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Record of a single BUY or SELL. */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Type { BUY, SELL }

    private final Type type;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final int marketDay;
    private final LocalDateTime time = LocalDateTime.now();

    public Transaction(Type type, String symbol, int quantity, double price, int marketDay) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.marketDay = marketDay;
    }

    public double getTotal() { return quantity * price; }

    @Override
    public String toString() {
        return String.format("%-19s Day %-3d %-4s %-6s qty=%-4d @ %9.2f  total=%11.2f",
                time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                marketDay, type, symbol, quantity, price, getTotal());
    }
}
