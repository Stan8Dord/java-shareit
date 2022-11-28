package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 255, nullable = false)
    private String name;
    @Email
    @Column(length = 512, nullable = false)
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
