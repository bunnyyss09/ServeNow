package com.manvanth.servenow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Role entity for managing user permissions and access control
 * Supports role-based access control (RBAC) system
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"users"})
@ToString(exclude = {"users"})
public class Role extends BaseEntity {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    @Column(name = "description", length = 200)
    private String description;

    // Many-to-many relationship with users
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    // Convenience constructor
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Helper methods
    public boolean hasUser(User user) {
        return users.contains(user);
    }

    public void addUser(User user) {
        users.add(user);
        user.getRoles().add(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.getRoles().remove(this);
    }
}