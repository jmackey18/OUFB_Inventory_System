import java.util.Comparator;
public class OrderTimeComparator implements Comparator<OnlineOrder> {

    
    public int compare(OnlineOrder o1, OnlineOrder o2) {
        if(o1.getLocalDateTime().isAfter(o2.getLocalDateTime())) {
            return 1;
        }
        if(o1.getLocalDateTime().isBefore(o2.getLocalDateTime())) {
            return -1;
        }
        return 0;
    }
    
   
    
}
