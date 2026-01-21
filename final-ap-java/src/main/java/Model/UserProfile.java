package Model;

public class UserProfile {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private double walletBalance;

    public UserProfile(String username, String firstName, String lastName, String email, String role, double walletBalance) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.walletBalance = walletBalance;
    }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public double getWalletBalance() { return walletBalance; }

    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }
}
