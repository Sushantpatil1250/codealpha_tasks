import java.io.Serializable;

/** A tradable stock with a current and previous price. */
public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String symbol;
    private final String name;
    private double price;
    private double previousPrice;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.previousPrice = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void updatePrice(double newPrice) {
        previousPrice = price;
        price = newPrice;
    }

    public double getChangePercent() {
        return (price - previousPrice) / previousPrice * 100.0;
    }
}
