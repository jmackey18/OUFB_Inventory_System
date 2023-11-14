import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class OnlineOrder extends Order {

    private String firstName, lastName;
    private OrderList orderList;

    private LocalDateTime time;
    private DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    public OnlineOrder(OrderList orderList, String firstName, String lastName, String timeString) {
        this.orderList = orderList;

        this.firstName = firstName;
        this.lastName = lastName;

        time = LocalDateTime.parse(timeString);        
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public OrderList getOrderList() {
        return orderList;
    }

    public LocalDateTime getLocalDateTime() {
        return time;
    }

    public String getFormattedTime() {
        return dtf.format(time);
    }
    
    //Specifically for terminal-user aesthetics
    public String easyToString() {
        String str = "";

        str += "Full Name: " + getFirstName() + " " + getLastName();
        str += "\nOrder Created: " + getFormattedTime(); 
        str += "\nOrder Desc: " + orderList.easyToString();
        str += "\n";
        
        return str;
    }
    
    @Override
    public String toString() {
        String str = "";

        str += "N: " + getFirstName() + " " + getLastName();
        str += "\nT: " + getLocalDateTime(); 
        str += "\nO: " + orderList.toString();
        str += "\n";
        
        return str;
    }

}
