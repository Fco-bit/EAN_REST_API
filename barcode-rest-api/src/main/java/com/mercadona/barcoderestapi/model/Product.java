package com.mercadona.barcoderestapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = { "description" })
@EqualsAndHashCode(exclude = { "description" })
@NoArgsConstructor
public class Product {

    public Product(String barcode, String name, BigDecimal price, String description) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    @NotNull
    @Size(min = 2, max = 30)
    @Column(nullable = false)
    private String name;

    @NotNull(message = "The price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "The price must be greater than 0")
    @Digits(integer = 4, fraction = 2, message = "The price must be a number with 2 decimal places")
    @Column(nullable = false)
    private BigDecimal price;

    @Size(min = 4, max = 300, message = "The description must be between 4 and 300 characters")
    @Column(nullable = true)
    private String description;

    @Id
    @NotNull
    @Size(min = 5, max = 5, message = "The barcode must be 5 characters")
    @Digits(integer = 5, fraction = 0, message = "The barcode must be a number")
    @Column(unique = true, nullable = false)
    private String barcode;

    @Column(nullable = false)
    private LocalDate createAt = LocalDate.now();

    @Column(nullable = false)
    private LocalDate updateAt = LocalDate.now();

}
