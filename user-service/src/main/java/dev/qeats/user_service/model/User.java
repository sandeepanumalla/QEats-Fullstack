package dev.qeats.user_service.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Table("users") // Map to the users table
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @Column("first_name") // Map to column first_name
    private String firstName;

    @Column("last_name") // Map to column last_name
    private String lastName;

    @Column("email")
    private String email;

    @Column("phone_number")
    private String phoneNumber;

//    // One-to-Many relationship with Address
//    @MappedCollection(idColumn = "user_id") // Explicitly map the foreign key
//    private List<Address> addresses;
////
//    // One-to-One relationship with Cart
//    @MappedCollection(idColumn = "user_id") // Explicitly map the foreign key
//    private Cart cart;

}
