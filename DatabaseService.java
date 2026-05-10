package service;

import model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Handles file-based persistence using CSV files.
 * Demonstrates file I/O and exception handling.
 */
public class DatabaseService {
    private static final String DATA_DIR        = "data/";
    private static final String PRODUCTS_FILE   = DATA_DIR + "products.csv";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "transactions.csv";
    private static final String CATEGORIES_FILE = DATA_DIR + "categories.csv";
    private static final String SUPPLIERS_FILE  = DATA_DIR + "suppliers.csv";

    public DatabaseService() {
        new File(DATA_DIR).mkdirs();
        initDefaultData();
    }

    private void initDefaultData() {
        if (!new File(CATEGORIES_FILE).exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(CATEGORIES_FILE))) {
                pw.println("1,Electronics,Electronic components and devices");
                pw.println("2,Office Supplies,Stationery and office materials");
                pw.println("3,Furniture,Office and warehouse furniture");
            } catch (IOException e) { e.printStackTrace(); }
        }
        if (!new File(SUPPLIERS_FILE).exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(SUPPLIERS_FILE))) {
                pw.println("1,TechCorp,Ali Raza,ali@techcorp.com,+92-300-1111111");
                pw.println("2,SupplyCo,Sara Khan,sara@supplyco.com,+92-300-2222222");
            } catch (IOException e) { e.printStackTrace(); }
        }
        if (!new File(PRODUCTS_FILE).exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
                pw.println("1,SKU001,USB Cables,50,10,5.99,1,1");
                pw.println("2,SKU002,A4 Paper Ream,8,15,3.49,2,2");
                pw.println("3,SKU003,Office Chair,3,5,89.99,3,2");
                pw.println("4,SKU004,HDMI Cables,0,5,12.00,1,1");
            } catch (IOException e) { e.printStackTrace(); }
        }
        if (!new File(TRANSACTIONS_FILE).exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
                // empty file with header
                pw.println("id,productId,type,qty,timestamp,notes,performedBy");
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    // ---- Load Methods ----

    public List<Category> loadCategories() {
        List<Category> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CATEGORIES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    Category c = new Category(p[1].trim(), p[2].trim());
                    c.setCategoryId(Integer.parseInt(p[0].trim()));
                    list.add(c);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: could not load categories: " + e.getMessage());
        }
        return list;
    }

    public List<Supplier> loadSuppliers() {
        List<Supplier> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 5) {
                    Supplier s = new Supplier(p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim());
                    s.setSupplierId(Integer.parseInt(p[0].trim()));
                    list.add(s);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: could not load suppliers: " + e.getMessage());
        }
        return list;
    }

    public List<Product> loadProducts(List<Category> cats, List<Supplier> sups) {
        List<Product> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 8) {
                    int catId = Integer.parseInt(p[6].trim());
                    int supId = Integer.parseInt(p[7].trim());
                    Category cat = cats.stream().filter(c -> c.getCategoryId() == catId).findFirst().orElse(null);
                    Supplier  sup = sups.stream().filter(s -> s.getSupplierId() == supId).findFirst().orElse(null);
                    Product prod = new Product(
                        Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim(),
                        Integer.parseInt(p[3].trim()), Integer.parseInt(p[4].trim()),
                        Double.parseDouble(p[5].trim()), cat, sup
                    );
                    list.add(prod);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: could not load products: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> loadTransactions(List<Product> products) {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] p = line.split(",", -1);
                if (p.length >= 7) {
                    int prodId = Integer.parseInt(p[1].trim());
                    products.stream().filter(pr -> pr.getId() == prodId).findFirst().ifPresent(prod -> {
                        TransactionType type = TransactionType.valueOf(p[2].trim());
                        Transaction t = new Transaction(
                            Integer.parseInt(p[0].trim()), prod, type,
                            Integer.parseInt(p[3].trim()),
                            LocalDateTime.parse(p[4].trim()),
                            p[5].trim(), p[6].trim()
                        );
                        list.add(t);
                    });
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: could not load transactions: " + e.getMessage());
        }
        return list;
    }

    // ---- Save Methods ----

    public void saveProduct(Product p) {
        List<Product> all = loadProducts(loadCategories(), loadSuppliers());
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == p.getId()) { all.set(i, p); found = true; break; }
        }
        if (!found) all.add(p);
        writeProducts(all);
    }

    public void deleteProduct(int productId) {
        List<Product> all = loadProducts(loadCategories(), loadSuppliers());
        all.removeIf(p -> p.getId() == productId);
        writeProducts(all);
    }

    private void writeProducts(List<Product> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : list) {
                pw.printf("%d,%s,%s,%d,%d,%.2f,%d,%d%n",
                    p.getId(), p.getSku(), p.getName(), p.getCurrentStock(),
                    p.getMinThreshold(), p.getUnitPrice(),
                    p.getCategory() != null ? p.getCategory().getCategoryId() : 0,
                    p.getSupplier()  != null ? p.getSupplier().getSupplierId()  : 0);
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }

    public void saveTransaction(Transaction t) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            pw.printf("%d,%d,%s,%d,%s,%s,%s%n",
                t.getId(), t.getProduct().getId(), t.getType().name(),
                t.getQuantity(), t.getTimestamp(),
                t.getNotes() == null ? "" : t.getNotes(),
                t.getPerformedBy());
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }
}
