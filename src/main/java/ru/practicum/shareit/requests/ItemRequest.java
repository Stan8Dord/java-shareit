package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 512, nullable = false)
    private String description;
    @Column(name = "requester_id")
    private int requester;
    @Column(name = "created")
    private LocalDateTime created;
}
