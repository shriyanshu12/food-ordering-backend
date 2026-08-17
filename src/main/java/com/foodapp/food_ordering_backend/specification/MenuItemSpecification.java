package com.foodapp.food_ordering_backend.specification;

import com.foodapp.food_ordering_backend.entity.MenuItem;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuItemSpecification {

    public static Specification<MenuItem> filterMenuItems(
            String keyword,
            Long restaurantId,
            Long categoryId,
            Boolean veg,
            Boolean available,
            Double minPrice,
            Double maxPrice
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {

                Predicate namePredicate = cb.like(
                        cb.lower(root.get("name")),
                        "%" + keyword.toLowerCase() + "%"
                );

                Predicate descriptionPredicate = cb.like(
                        cb.lower(root.get("description")),
                        "%" + keyword.toLowerCase() + "%"
                );

                predicates.add(cb.or(namePredicate, descriptionPredicate));
            }

            if (restaurantId != null) {
                predicates.add(
                        cb.equal(root.get("restaurant").get("id"), restaurantId)
                );
            }

            if (categoryId != null) {
                predicates.add(
                        cb.equal(root.get("category").get("id"), categoryId)
                );
            }

            if (veg != null) {
                predicates.add(cb.equal(root.get("veg"), veg));
            }

            if (available != null) {
                predicates.add(cb.equal(root.get("available"), available));
            }

            if (minPrice != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("price"),
                                BigDecimal.valueOf(minPrice)
                        )
                );
            }

            if (maxPrice != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("price"),
                                BigDecimal.valueOf(maxPrice)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private MenuItemSpecification() {
    }
}