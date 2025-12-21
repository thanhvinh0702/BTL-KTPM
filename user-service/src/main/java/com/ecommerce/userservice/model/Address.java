package com.ecommerce.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String street;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String city;

    @Column(name = "flat_no", nullable = false)
    private String flatNo;

    @Column(nullable = false)
    private String state;

    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;
}
