package br.com.fiap.item.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Double price;

    private String description;

    @NotNull
    private Integer quantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;
}
