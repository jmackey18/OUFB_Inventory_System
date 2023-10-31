import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Employees {
    
    private static final String empCredentialsFile = "OUFB_Inventory_System/txtfiles/EmployeesCredentials.txt";
    private BufferedReader empReader;
    private HashMap<String, EmployeeWrapper> manCreds, distribCreds, empCreds;
    
    public Employees() {
        manCreds = new HashMap<String, EmployeeWrapper>();
        distribCreds = new HashMap<String, EmployeeWrapper>();
        empCreds = new HashMap<String, EmployeeWrapper>();
        String line;
        try {
            empReader = new BufferedReader(new FileReader(empCredentialsFile));
            while((line = empReader.readLine()) != null) {
                if(line.equals(System.lineSeparator())) {
                    break;
                } else if(line.contains("[")) {
                    continue;
                } else {
                    String manName = line.substring(0, line.indexOf("|")-1);
                    String manUser = line.substring(line.indexOf("|")+2, line.indexOf("|", line.indexOf("|")+2)-1);
                    String manPass = line.substring(line.indexOf("|", line.indexOf(manName))+2);

                    manCreds.putIfAbsent(manName, new EmployeeWrapper(manUser, manPass));
                }
            }

            while((line = empReader.readLine()) != null) {
                if(line.equals(System.lineSeparator())) {
                    break;
                } else if(line.contains("[")) {
                    continue;
                } else {
                    String distribName = line.substring(0, line.indexOf("|")-1);
                    String distribUser = line.substring(line.indexOf("|")+2, line.indexOf("|", line.indexOf("|")+2)-1);
                    String distribPass = line.substring(line.indexOf("|", line.indexOf(distribUser))+2);

                    distribCreds.putIfAbsent(distribName, new EmployeeWrapper(distribUser, distribPass));
                }
            }

            while((line = empReader.readLine()) != null) {
                if(line.equals(System.lineSeparator())) {
                    break;
                } else if(line.contains("[")) {
                    continue;
                } else {
                    String empName = line.substring(0, line.indexOf("|")-1);
                    String empUser = line.substring(line.indexOf("|")+2, line.indexOf("|", line.indexOf("|")+2)-1);
                    String empPass = line.substring(line.indexOf("|", line.indexOf(empUser))+2);

                    empCreds.putIfAbsent(empName, new EmployeeWrapper(empUser, empPass));
                }
            }

            System.out.println(manCreds);
            System.out.println(distribCreds);
            System.out.println(empCreds);

        } catch (FileNotFoundException e) {
            System.out.println("Unable to find Employees Credentials file");
        } catch (IOException e) {
            System.out.println("Unable to read from Employees Credentials file.");
        }
    }

    public HashMap<String, EmployeeWrapper> getEmpCreds() {
        return empCreds;
    }

    public HashMap<String, EmployeeWrapper> getDistribCreds() {
        return distribCreds;
    }

    public HashMap<String, EmployeeWrapper> getManCreds() {
        return manCreds;
    }


    public static void main(String[] args) {
        Employees run = new Employees();
    }
}
