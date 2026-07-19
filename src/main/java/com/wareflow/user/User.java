package com.wareflow.user;

import com.wareflow.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import com.wareflow.user.exception.UserMustHaveAtLeastOneRoleException;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            unique = true,
            length = 50
    )
    private String username;

    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;

    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 100
    )
    private String lastName;

    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 254
    )
    private String email;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false,
            insertable = false
    )
    private OffsetDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    nullable = false
            )
    )
    private Set<Role> roles = new HashSet<>();

    protected User() {
        // Required by JPA
    }

    public User(
            String username,
            String passwordHash,
            String firstName,
            String lastName,
            String email
    ) {
        this.username = normalizeUsername(username);
        this.passwordHash = requireNonBlank(passwordHash, "Password hash");
        this.firstName = requireNonBlank(firstName, "First name");
        this.lastName = requireNonBlank(lastName, "Last name");
        this.email = normalizeEmail(email);
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<Role> getRoles() {
        return Set.copyOf(roles);
    }

    public void assignRoles(Collection<Role> roles) {
        Objects.requireNonNull(roles, "Roles must not be null");

        if (roles.isEmpty()) {
            throw new UserMustHaveAtLeastOneRoleException();
        }

        this.roles.forEach(role -> role.removeUser(this));
        this.roles.clear();

        roles.forEach(this::addRole);
    }

    public void addRole(Role role) {
        Objects.requireNonNull(role, "Role must not be null");

        if (roles.add(role)) {
            role.addUser(this);
        }
    }

    public void removeRole(Role role) {
        Objects.requireNonNull(role, "Role must not be null");

        if (roles.size() == 1 && roles.contains(role)) {
            throw new UserMustHaveAtLeastOneRoleException();
        }

        if (roles.remove(role)) {
            role.removeUser(this);
        }
    }

    public void updateProfile(
            String firstName,
            String lastName,
            String email
    ) {
        this.firstName = requireNonBlank(firstName, "First name");
        this.lastName = requireNonBlank(lastName, "Last name");
        this.email = normalizeEmail(email);
    }

    public void changePasswordHash(String passwordHash) {
        this.passwordHash = requireNonBlank(
                passwordHash,
                "Password hash"
        );
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private static String normalizeUsername(String username) {
        return requireNonBlank(username, "Username")
                .toLowerCase(Locale.ROOT);
    }

    private static String normalizeEmail(String email) {
        return requireNonBlank(email, "Email")
                .toLowerCase(Locale.ROOT);
    }

    private static String requireNonBlank(
            String value,
            String fieldName
    ) {
        Objects.requireNonNull(value, fieldName + " must not be null");

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return trimmedValue;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof User user)) {
            return false;
        }

        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}