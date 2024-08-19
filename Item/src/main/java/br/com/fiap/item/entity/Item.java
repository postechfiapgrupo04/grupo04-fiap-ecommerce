package br.com.fiap.item.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Double price;
    private String description;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private Category categoryEnum;
}
