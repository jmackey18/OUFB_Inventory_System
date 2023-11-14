public class Product {
    private String productName;
    private int productStock, productTarget;

    public Product(String productName, int productStock, int productTarget) {
        this.productName = productName;
        this.productStock = productStock;
        this.productTarget = productTarget;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductStock() {
        return productStock;
    }

    public int getProductTarget() {
        return productTarget;
    }

    public String toString() {
        return productName + " | In stock: " + productStock + " | Target: " + productTarget;
    }
}
