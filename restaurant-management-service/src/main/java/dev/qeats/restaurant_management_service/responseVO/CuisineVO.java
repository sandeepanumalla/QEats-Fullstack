package dev.qeats.restaurant_management_service.responseVO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class CuisineVO {

    private Long id;

    @NotEmpty
    private String name;

    private BranchVO branch;

    @NotEmpty
    private String description;
    private String imageUrl;
//    private RestaurantVO restaurant;

    public CuisineVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CuisineVO() {

    }

    // create builder

    public static class CuisineVOBuilder {
        private Long id;
        private String name;
        private BranchVO branch;
        private String description;
        private String imageUrl;

        public CuisineVOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CuisineVOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CuisineVOBuilder branch(BranchVO branch) {
            this.branch = branch;
            return this;
        }

        public CuisineVOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CuisineVOBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public CuisineVO build() {
            CuisineVO cuisineVO = new CuisineVO();
            cuisineVO.id = this.id;
            cuisineVO.name = this.name;
            cuisineVO.branch = this.branch;
            cuisineVO.description = this.description;
            cuisineVO.imageUrl = this.imageUrl;
            return cuisineVO;
        }
    }
}
