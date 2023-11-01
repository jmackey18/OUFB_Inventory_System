
public class Employee {
    private String userName, password, positiion;

    public Employee(String userName, String password, String position) {
        this.userName = userName;
        this.password = password;
        this.positiion = position;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPosition() {
        return positiion;
    }

    @Override
    public boolean equals(Object obj) {
         if (obj == this)
            return true;
            
        if (obj == null || !(obj instanceof Employee)) 
            return false;
        
        Employee otherEmp = (Employee) obj;
        
        if (!otherEmp.userName.equals(this.userName)) return false;
        if (!otherEmp.password.equals(this.password))   return false;
        if (!otherEmp.positiion.equals(this.positiion)) return false;
        
        return true;
    }
    
    public String toString() {
        return userName + " - " + password + " - " + positiion;
    }
}
