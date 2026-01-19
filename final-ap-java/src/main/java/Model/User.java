package Model;

public class User implements UserRole {
    private String username;
    private String password;
    private String role;  // مثلا customer یا admin

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public void performRole() {
        if ("customer".equals(role)) {
            System.out.println(username + " is performing customer actions.");
        } else if ("admin".equals(role)) {
            System.out.println(username + " is performing admin actions.");
        } else {
            System.out.println(username + " has an unknown role.");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}