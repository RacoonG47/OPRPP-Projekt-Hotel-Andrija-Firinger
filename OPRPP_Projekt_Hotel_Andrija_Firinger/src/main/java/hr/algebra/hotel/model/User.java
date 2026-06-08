package hr.algebra.hotel.model;


import java.util.Objects;

public class User extends Entity {

    public enum Role {
        ADMINISTRATOR, KORISNIK
    }

    private String username;
    private String password;
    private Role role;

    public User() {}

    public User(Integer id, String username, String password, Role role) {
        super(id);
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isAdmin() {
        return Role.ADMINISTRATOR.equals(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return super.equals(o) && Objects.equals(username, other.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }

    @Override
    public String toString() {
        return username + " [" + role + "]";
    }
}