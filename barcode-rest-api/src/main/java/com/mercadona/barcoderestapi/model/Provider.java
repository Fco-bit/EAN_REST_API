package com.mercadona.barcoderestapi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Provider {

    public Provider(String barcode, String name, String address) {
        this.barcode = barcode;
        this.name = name;
        this.address = address;
    }

    @NotNull(message = "The name cannot be null")
    @Size(min = 2, max = 30, message = "The name must be between 2 and 30 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 300, message = "The adress must be max 300 characters")
    @Column(nullable = true)
    private String address;

    @Id
    @NotNull(message = "The barcode cannot be null")
    @Size(min = 7, max = 7, message = "The barcode must be 7 numbers")
    @Digits(integer = 7, fraction = 0, message = "The barcode must be a number")
    @Column(unique = true, nullable = false)
    private String barcode;

    @Column(nullable = false)
    private LocalDate createAt = LocalDate.now();

    @Column(nullable = false)
    private LocalDate updateAt = LocalDate.now();

}
