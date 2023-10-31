import java.util.ArrayList;
import java.util.HashMap;

public class ProductManager {
    private InventoryManager invAccess;
    private ArrayList<String> productNames;
    private ArrayList<Integer> inventoryNumbers, invTargetNumbers;
    private HashMap<Integer, String> productNameIdentifiers;
    private HashMap<Integer, Integer> productStockIdentifiers, productTargetIdentifiers;
    private static int DEFAULT_SKU;

    public ProductManager() {
        invAccess = new InventoryManager();

        productNames = invAccess.getProductNames();
        inventoryNumbers = invAccess.getInventoryNumbers();
        invTargetNumbers = invAccess.getInvTargetNumbers();

        productNameIdentifiers = new HashMap<Integer, String>();
        productStockIdentifiers = new HashMap<Integer, Integer>();
        productTargetIdentifiers = new HashMap<Integer, Integer>();
        DEFAULT_SKU = 1000000;

        for(int i = 0; i < productNames.size(); i++) {
            DEFAULT_SKU++;
            productNameIdentifiers.putIfAbsent(DEFAULT_SKU, productNames.get(i));
            productStockIdentifiers.putIfAbsent(DEFAULT_SKU, inventoryNumbers.get(i));
            productTargetIdentifiers.putIfAbsent(DEFAULT_SKU, invTargetNumbers.get(i));
        }

    }

    public HashMap<Integer, String> getProductNameIdentifiers() {
        return productNameIdentifiers;
    }

    public HashMap<Integer, Integer> getProductStockIdentifiers() {
        return productStockIdentifiers;
    }

    public HashMap<Integer, Integer> getProductTargetIdentifiers() { 
        return productTargetIdentifiers;
    }

    public ArrayList<String> getProductNames() {
        return productNames;
    }

    public ArrayList<Integer> getInventoryNumbers() {
        return inventoryNumbers;
    }
}
