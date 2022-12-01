package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Column(length = 512)
    private String description;
    @Column(name = "is_available")
    private Boolean isAvailable;
    @Column(name = "owner_id")
    private int owner;
    @Column(name = "request_id")
    private long request;

    public Item(String name, String description, boolean available, int owner, long request) {
        this.name = name;
        this.description = description;
        this.isAvailable = available;
        this.owner = owner;
        this.request = request;
    }
}
