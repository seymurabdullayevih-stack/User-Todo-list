package com.proyekt.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "completed",nullable = false)
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
