package dev.qeats.restaurant_management_service.model;

import dev.qeats.restaurant_management_service.responseVO.AddressVO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String street;
    private double latitude;
    private double longitude;

    @OneToOne(mappedBy = "address")
    private Branch branch;

    //method for toAddressVO()
    public AddressVO  toAddressVO(){
        AddressVO addressVO = new AddressVO();
        addressVO.setId(this.id);
        addressVO.setCity(this.city);
        addressVO.setCountry(this.country);
        addressVO.setLatitude(this.latitude);
        addressVO.setLongitude(this.longitude);
        addressVO.setStreet(this.street);
        addressVO.setState(this.state);
        addressVO.setZipCode(this.zipCode);
        return addressVO;
    }
}
