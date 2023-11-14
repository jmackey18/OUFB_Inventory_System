import java.util.ArrayList;

public class OrderList {
    private ArrayList<Order> orderList;

    public OrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    //Specifically for terminal-user aesthetic
    public String easyToString() {
        String str = orderList.get(0).easyToString();
        for(int i = 1; i < orderList.size(); i++) {
            str += " | " + orderList.get(i).easyToString();
        }

        return str;
    }

    public String viewerToString() {
        ArrayList<String> str = new ArrayList<String>();
        str.add(orderList.get(0).easyToString());
        for(int i = 1; i < orderList.size(); i++) {
            if(orderList.get(i).getOrderProductSKU() != 100) {
                str.add(orderList.get(i).easyToString());
            }
        }

        return String.join("\n", str);
    }
    
    public String toString() {
        String str = orderList.get(0).toString();
        for(int i = 1; i < orderList.size(); i++) {
            str += " | " + orderList.get(i).toString();
        }

        return str;
    }
}
