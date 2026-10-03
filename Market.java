import java.io.Serializable;
import java.util.*;

/** Simulated stock market. Prices move randomly each "day". */
public class Market implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, Stock> stocks = new LinkedHashMap<>();
    private final Random random = new Random();
    private int day = 0;

    public Market() {
        add("TCS",   "Tata Consultancy Services", 3850.00);
        add("INFY",  "Infosys Ltd",              1520.50);
        add("RELI",  "Reliance Industries",      2890.25);
        add("HDFC",  "HDFC Bank",                1675.80);
        add("WIPRO", "Wipro Ltd",                 480.40);
        add("ITC",   "ITC Ltd",                   445.10);
        add("TATAMO","Tata Motors",               960.75);
        add("SBI",   "State Bank of India",       790.30);
    }

    private void add(String sym, String name, double price) {
        stocks.put(sym, new Stock(sym, name, price));
    }

    public int getDay() { return day; }
    public Stock getStock(String symbol) { return stocks.get(symbol.toUpperCase()); }
    public Collection<Stock> getStocks() { return stocks.values(); }

    /** Move to the next day; each price changes by roughly +/- 2% (max 7%). */
    public void nextDay() {
        day++;
        for (Stock s : stocks.values()) {
            double change = Math.max(-0.07, Math.min(0.07, random.nextGaussian() * 0.02));
            s.updatePrice(Math.max(1.0, s.getPrice() * (1 + change)));
        }
    }

    public void display() {
        System.out.println("\n=========== MARKET DATA (Day " + day + ") ===========");
        System.out.printf("%-8s %-28s %10s %9s%n", "Symbol", "Company", "Price", "Change");
        System.out.println("----------------------------------------------------------");
        for (Stock s : stocks.values()) {
            System.out.printf("%-8s %-28s %10.2f %+8.2f%%%n",
                    s.getSymbol(), s.getName(), s.getPrice(), s.getChangePercent());
        }
    }
}
