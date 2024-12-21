package dev.qeats.user_service.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

@Table("addresses") // Map to the addresses table
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(value = "user_id") // Foreign key for User
    private String userId;

    @Column(value = "street")
    private String street;

    @Column(value = "city")
    private String city;

    @Column(value = "state")
    private String state;

    @Column(value = "zip_code")
    private String zipCode;

    @Column(value = "country")
    private String country;
}
