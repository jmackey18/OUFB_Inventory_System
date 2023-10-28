import java.util.ArrayList;
import java.util.HashMap;

public class ProductManager {
    private InventoryManager invAccess = new InventoryManager();
    private ArrayList<String> productNames;
    private ArrayList<Integer> inventoryNumbers;
    private HashMap<Integer, String> productIdentifiers;
    private static int DEFAULT_SKU = 1000000;

    public ProductManager() {
        productNames = invAccess.getProductNames();
        inventoryNumbers = invAccess.getInventoryNumbers();
        productIdentifiers = new HashMap<Integer, String>();

        for(String name : productNames) {
            DEFAULT_SKU++;
            productIdentifiers.put(DEFAULT_SKU, name);
        }

    }

    public HashMap<Integer, String> getProductIdentifiers() {
        return productIdentifiers;
    }
}
