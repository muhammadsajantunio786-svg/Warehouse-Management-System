# Warehouse Management System (WMS)

A Java Swing desktop application for managing warehouse inventory, stock transactions, supplier information, and automated low-stock alerts.

---

## Group Members

| Full Name         | CMS / Student ID | Section |
|-------------------|-----------------|---------|
| [Muhammad Sajan]   | [023-25-0170]      | [C]     |
| [Member 2 Name]   | [CMS-ID-2]      | [X]     |
| [Member 3 Name]   | [CMS-ID-3]      | [X]     |
| [Member 4 Name]   | [CMS-ID-4]      | [X]     |

> **Note:** Replace the placeholders above with your actual names, IDs, and section before submitting.

---

## Demo Video

📹 **YouTube Link:** [https://drive.google.com/file/d/1QTWrzCAjXXcdmaB5Sx67cO-j8xuVdcQo/view?usp=drive_link)

---

## GitHub Repository

🔗 **Repo URL:** [https://github.com/muhammadsajantunio786-svg/Warehouse-Management-System)

---

## Project Purpose

**Problem:** Warehouses that rely on manual tracking frequently suffer from stock-outs, over-ordering, and a lack of audit trails. Staff have no real-time visibility into what is available or what needs reordering.

**Solution:** This WMS provides a role-based desktop application that:
- Tracks every product (stock levels, prices, categories, suppliers).
- Records stock-in and stock-out transactions with an audit trail.
- Automatically fires visual alerts when stock falls below a configurable minimum threshold.
- Lets managers generate inventory valuation and low-stock reports.

**Users:** Warehouse workers (record stock movements) and managers (full access + reports).

---

## Main Modules

```
src/
├── model/           — Entity classes (data layer)
│   ├── WarehouseEntity.java      Abstract base class (id, getSummary())
│   ├── Stockable.java            Interface: addStock, removeStock, isLowStock…
│   ├── Product.java              Extends WarehouseEntity, implements Stockable
│   ├── Category.java
│   ├── Supplier.java
│   ├── Transaction.java          Extends WarehouseEntity
│   ├── TransactionType.java      Enum: STOCK_IN | STOCK_OUT
│   └── User.java                 Extends WarehouseEntity, Role enum inside
│
├── service/         — Business logic layer
│   ├── InventoryService.java     Core CRUD + stock operations
│   ├── NotificationService.java  Observer pattern — fires low-stock alerts
│   ├── NotificationObserver.java Interface for observers
│   ├── AuthService.java          Login / session management
│   ├── ReportService.java        Report generation (low stock, valuation, transactions)
│   └── DatabaseService.java      CSV file persistence (read/write)
│
├── exception/       — Custom exceptions
│   ├── InsufficientStockException.java
│   ├── ProductNotFoundException.java
│   └── AuthenticationException.java
│
└── ui/              — Presentation layer (Java Swing)
    ├── App.java                  Entry point — wires services, launches login
    ├── LoginDialog.java          Login form
    ├── MainWindow.java           Tabbed main window
    ├── DashboardPanel.java       KPI cards (totals, low-stock count, total value)
    ├── InventoryPanel.java       Product table + stock-in/out actions + search
    ├── AlertsPanel.java          Real-time alert feed (implements NotificationObserver)
    ├── ReportsPanel.java         Text-based report viewer
    └── StockStatusRenderer.java  Custom JTable cell renderer (red/orange rows)

sql/
└── wms_schema.sql   Optional MySQL schema (project uses CSV persistence by default)

data/                (auto-created on first run)
├── products.csv
├── categories.csv
├── suppliers.csv
└── transactions.csv
```

---

## Key OOP Features Used

| Concept | Where |
|---|---|
| **Abstract class** | `WarehouseEntity` — base for Product, Transaction, User |
| **Interface** | `Stockable` (addStock/removeStock/isLowStock), `NotificationObserver` |
| **Inheritance** | Product, Transaction, User all extend WarehouseEntity |
| **Polymorphism** | `StockStatusRenderer` overrides `DefaultTableCellRenderer`; observer list holds any `NotificationObserver` |
| **Encapsulation** | All model fields are `private`; validated through setters |
| **Enum** | `TransactionType`, `User.Role` |
| **Collections** | `ArrayList`, `List`, streams for filtering/searching |
| **File I/O** | `DatabaseService` reads/writes CSV files with BufferedReader/PrintWriter |
| **Exception handling** | `InsufficientStockException`, `ProductNotFoundException`, `AuthenticationException` |
| **Design pattern** | Observer pattern in notification system |

---

## How to Compile & Run

### Requirements
- **JDK 11** or higher (JDK 17 recommended)
- No external libraries required — pure Java SE + Swing

### Compile (from the `src/` directory)

```bash
# Navigate to the src folder
cd WMS/src

# Compile all source files
javac -d ../out model/*.java exception/*.java service/*.java ui/*.java
```

### Run

```bash
# From the WMS folder (one level above src)
cd WMS
java -cp out ui.App
```

> **Windows users:** Replace `/` with `\` in paths if needed. The `data/` directory and CSV files are created automatically on first run.

### Default Login Credentials

| Username | Password    | Role    |
|----------|-------------|---------|
| `admin`  | `admin123`  | Manager |
| `worker` | `worker123` | Worker  |

### Optional: MySQL Setup

If you wish to use the MySQL database instead of CSV files:
1. Run `sql/wms_schema.sql` in your MySQL server.
2. Update `DatabaseService.java` to use JDBC (connection string, username, password).

---

## Academic Integrity

This project was designed and implemented by the group listed above as part of the OOP course semester project. Any tutorials or references consulted are cited in the code comments. AI assistance was used only for guidance; all design decisions and implementations were reviewed and understood by all group members.
