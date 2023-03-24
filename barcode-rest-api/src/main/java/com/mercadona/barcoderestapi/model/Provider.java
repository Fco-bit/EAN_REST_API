package com.mercadona.barcoderestapi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Builder.Default;

@Data
@Entity
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class Provider {

    @NotNull
    @Size(min = 2, max = 30)
    @Column(nullable = false)
    private String name;

    @Size(min = 4, max = 300)
    @Column(nullable = true)
    private String adress;

    @Id
    @NotNull
    @Column(unique = true, nullable = false)
    private Long barcode;

    @Default
    @Column(nullable = false)
    private LocalDate createAt = LocalDate.now();

    @Default
    @Column(nullable = false)
    private LocalDate updateAt = LocalDate.now();
    
}
