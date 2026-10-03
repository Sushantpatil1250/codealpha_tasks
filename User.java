import java.io.Serializable;
import java.util.*;

/** A trader: cash balance, holdings, transaction log and performance history. */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final double startingCash;
    private double cash;
    private final Map<String, Integer> holdings = new TreeMap<>();
    private final Map<String, Double> costBasis = new HashMap<>();   // total money spent per symbol
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<double[]> history = new ArrayList<>();        // {day, portfolioValue}

    public User(String name, double startingCash) {
        this.name = name;
        this.startingCash = startingCash;
        this.cash = startingCash;
        history.add(new double[]{0, startingCash});
    }

    public String getName() { return name; }
    public double getCash() { return cash; }
    public double getStartingCash() { return startingCash; }
    public List<Transaction> getTransactions() { return transactions; }
    public List<double[]> getHistory() { return history; }
    public Map<String, Integer> getHoldings() { return holdings; }
    public Map<String, Double> getCostBasis() { return costBasis; }

    public void buy(Stock s, int qty, int day) {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        double cost = s.getPrice() * qty;
        if (cost > cash)
            throw new IllegalStateException(String.format("Insufficient funds. Need %.2f, have %.2f", cost, cash));
        cash -= cost;
        holdings.merge(s.getSymbol(), qty, Integer::sum);
        costBasis.merge(s.getSymbol(), cost, Double::sum);
        transactions.add(new Transaction(Transaction.Type.BUY, s.getSymbol(), qty, s.getPrice(), day));
    }

    public void sell(Stock s, int qty, int day) {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        int owned = holdings.getOrDefault(s.getSymbol(), 0);
        if (qty > owned)
            throw new IllegalStateException("You only own " + owned + " share(s) of " + s.getSymbol());
        double avgCost = costBasis.get(s.getSymbol()) / owned;
        if (qty == owned) {
            holdings.remove(s.getSymbol());
            costBasis.remove(s.getSymbol());
        } else {
            holdings.put(s.getSymbol(), owned - qty);
            costBasis.put(s.getSymbol(), avgCost * (owned - qty));
        }
        cash += s.getPrice() * qty;
        transactions.add(new Transaction(Transaction.Type.SELL, s.getSymbol(), qty, s.getPrice(), day));
    }

    public double holdingsValue(Market m) {
        double total = 0;
        for (Map.Entry<String, Integer> e : holdings.entrySet())
            total += m.getStock(e.getKey()).getPrice() * e.getValue();
        return total;
    }

    public double totalValue(Market m) { return cash + holdingsValue(m); }

    /** Save today's portfolio value (called after each market day). */
    public void recordSnapshot(Market m) {
        history.add(new double[]{m.getDay(), totalValue(m)});
    }

    public void displayPortfolio(Market m) {
        System.out.println("\n=========== PORTFOLIO: " + name + " ===========");
        if (holdings.isEmpty()) {
            System.out.println("No holdings yet.");
        } else {
            System.out.printf("%-8s %5s %10s %10s %12s %12s%n", "Symbol", "Qty", "Avg Cost", "Price", "Value", "P/L");
            System.out.println("-----------------------------------------------------------------");
            for (Map.Entry<String, Integer> e : holdings.entrySet()) {
                Stock s = m.getStock(e.getKey());
                int q = e.getValue();
                double cost = costBasis.get(e.getKey());
                double value = s.getPrice() * q;
                System.out.printf("%-8s %5d %10.2f %10.2f %12.2f %+12.2f%n",
                        s.getSymbol(), q, cost / q, s.getPrice(), value, value - cost);
            }
        }
        double total = totalValue(m);
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("Cash:            %12.2f%n", cash);
        System.out.printf("Holdings value:  %12.2f%n", holdingsValue(m));
        System.out.printf("Total value:     %12.2f%n", total);
        System.out.printf("Overall P/L:     %+12.2f (%+.2f%%)%n",
                total - startingCash, (total - startingCash) / startingCash * 100);
    }

    public void displayPerformance() {
        System.out.println("\n=========== PERFORMANCE OVER TIME ===========");
        System.out.printf("%-5s %13s %10s%n", "Day", "Value", "Return");
        for (double[] h : history) {
            double ret = (h[1] - startingCash) / startingCash * 100;
            int bars = (int) Math.max(0, Math.min(40, 20 + ret * 2));
            System.out.printf("%-5d %13.2f %+9.2f%%  %s%n", (int) h[0], h[1], ret, "#".repeat(bars));
        }
    }
}
