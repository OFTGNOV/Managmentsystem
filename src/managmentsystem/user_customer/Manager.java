package managmentsystem.user_customer;

public class Manager extends User{

	public Manager(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public void manageUserAccounts() {
        System.out.println("Managing all user accounts.");
    }

    public void generateReports() {
        System.out.println("Generating shipment, revenue, and performance reports.");
    }
}
