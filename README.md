# 📈 Stock Trading Platform

## 📖 Description
Stock Trading Platform is a Java Swing based desktop application that simulates a basic stock market environment. Users can view market data, buy and sell stocks, track their portfolio and monitor performance over time through a simple graphical interface. All market data is simulated and portfolio data is saved to a file.

## ✨ Features
- ✅ Market Data Display
- ✅ Buy Stocks
- ✅ Sell Stocks
- ✅ Portfolio Tracking (Average Cost, Value, Profit/Loss)
- ✅ Portfolio Performance Over Time (Line Chart)
- ✅ Transaction History
- ✅ Market Simulation (Next Day Price Changes)
- ✅ Input Validation (Insufficient Funds, Invalid Quantity, Shares Not Owned)
- ✅ Save and Load Portfolio Data (File I/O)
- ✅ User-Friendly GUI
- ✅ Console Version Included

## 🖥 Application Tabs
| Tab | Description |
|---|---|
| Market & Portfolio | Live stock prices with daily change % and your current holdings |
| Transactions | Complete history of all buy and sell operations |
| Performance | Day-wise portfolio value shown as a line chart |

## 🛠 Technologies Used
- Java
- Java Swing
- Object-Oriented Programming (OOP)
- Collections (Map, List)
- Exception Handling
- File Handling (Serialization)
- IntelliJ IDEA

## 🧱 OOP Design
| Class | Responsibility |
|---|---|
| `Stock` | Stores symbol, company name, current and previous price |
| `Market` | Manages all stocks and simulates daily price changes |
| `User` | Manages cash, holdings, transactions and performance history |
| `Transaction` | Record of a single BUY or SELL |
| `DataStore` | Saves and loads data using file serialization |
| `StockTradingGUI` | Swing graphical interface |
| `Main` | Console based interface |

## 📂 Project Structure
```
CodeAlpha_StockTradingPlatform
│
├── Stock.java
├── Market.java
├── Transaction.java
├── User.java
├── DataStore.java
├── StockTradingGUI.java
├── Main.java
└── README.md
```

## 🚀 How to Run
1. Open the project in IntelliJ IDEA.
2. Compile all Java files.
3. Run `StockTradingGUI.java`.
4. Select a stock, enter quantity and click **Buy** or **Sell**.
5. Click **Next Day >>** to simulate market movement.
6. Close the window to save your portfolio automatically.

> To start fresh, delete the `portfolio.dat` file.

## 📸 Screenshots
Market & Portfolio

(Add screenshot here)

Performance Chart

(Add screenshot here)

## 👨‍💻 Author
Sushant Patil

CodeAlpha Java Programming Internship

⭐ Developed as part of the CodeAlpha Internship Program.
