
public class Order {
    private int orderProductSKU, orderProductAmount;

    public Order() {
        orderProductSKU = -1;
        orderProductAmount = -1;
    }
    
    public Order(int orderProductSKU, int orderProductAmount) {
        this.orderProductSKU = orderProductSKU;
        this.orderProductAmount = orderProductAmount;
    }

    public int getOrderProductSKU() {
        return orderProductSKU;
    }

    public int getOrderProductAmount() {
        return orderProductAmount;
    }

    //Specifically for terminal-user aesthetics
    public String easyToString() {
        ProductManager access = new ProductManager();
        return access.getProductIdentifiers().get(orderProductSKU).getProductName() + ", Qty: " + orderProductAmount;
    }
    
    //Supports for requested product(s) in one order
    public String toString() {
        return orderProductSKU + ", " + orderProductAmount;
    }

}
