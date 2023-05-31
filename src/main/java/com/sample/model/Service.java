package com.sample.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "messages")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sessionID;

    @Column(nullable = false)
    private String message;


}
