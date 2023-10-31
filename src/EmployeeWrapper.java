public class EmployeeWrapper {
    private String userName, password;

    public EmployeeWrapper(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String toString() {
        return userName + " - " + password;
    }
}
