package dev.qeats.restaurant_management_service.model;

import dev.qeats.restaurant_management_service.responseVO.AddressVO;
import dev.qeats.restaurant_management_service.responseVO.BranchVO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Branch extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;
    private String contactNumber;
    private String imageUrl;
    private String description;
    private String openingHours;
    private String closingHours;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<MenuItem> menuItems;

    public BranchVO toBranchVO() {
        BranchVO branchVO = new BranchVO();
        branchVO.setId(this.id);
        branchVO.setAddress(this.address.toAddressVO());
        branchVO.setName(this.name);
        return branchVO;
    }
}
