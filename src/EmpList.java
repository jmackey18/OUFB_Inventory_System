import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class EmpList {
    
    private static final String empCredentialsFile = "OUFB_Inventory_System/txtfiles/EmployeesCredentials.txt";
    private BufferedReader empReader;
    private HashMap<String, Employee> allCreds;
    
    public EmpList() {
        allCreds = new HashMap<String, Employee>();

        String line;
        try {
            empReader = new BufferedReader(new FileReader(empCredentialsFile));
            while((line = empReader.readLine()) != null) {
                if(line.contains("Manager:")) {
                    String manName = line.substring(line.indexOf(": ")+2, line.indexOf(" |"));
                    String manUser = line.substring(line.indexOf("| ")+2, line.lastIndexOf(" |"));
                    String manPass = line.substring(line.lastIndexOf("| ")+2);

                    allCreds.putIfAbsent(manName, new Employee(manUser, manPass, "Manager"));
                } else if(line.contains("Distributor:")) {
                    String distribName = line.substring(line.indexOf(": ")+2, line.indexOf(" |"));
                    String distribUser = line.substring(line.indexOf("| ")+2, line.lastIndexOf(" |"));
                    String distribPass = line.substring(line.lastIndexOf("| ")+2);

                    allCreds.putIfAbsent(distribName, new Employee(distribUser, distribPass, "Distributor"));
                } else if(line.contains("Staff:")) {
                    String empName = line.substring(line.indexOf(": ")+2, line.indexOf(" |"));
                    String empUser = line.substring(line.indexOf("| ")+2, line.lastIndexOf(" |"));
                    String empPass = line.substring(line.lastIndexOf("| ")+2);

                    allCreds.putIfAbsent(empName, new Employee(empUser, empPass, "Staff"));
                } else {
                    continue;
                }
            }
            empReader.close();
        } catch (FileNotFoundException e) {
               System.out.println("Unable to find Employees Credentials file");
        } catch (IOException e) {
              System.out.println("Unable to read from Employees Credentials file.");
        }
     }

     public HashMap<String, Employee> getAllCreds() {
        return allCreds;
     }
}
