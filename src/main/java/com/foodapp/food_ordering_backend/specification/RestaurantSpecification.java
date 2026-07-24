package com.foodapp.food_ordering_backend.specification;

import com.foodapp.food_ordering_backend.entity.Restaurant;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSpecification {

    public static Specification<Restaurant> filterRestaurants(
            String keyword,
            String city,
            String state,
            Boolean open,
            Double minRating
    ) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

//            // Search by restaurant name OR description
//            if (keyword != null && !keyword.isBlank()) {
//
//                Predicate namePredicate =
//                        criteriaBuilder.like(
//                                criteriaBuilder.lower(root.get("name")),
//                                "%" + keyword.toLowerCase() + "%"
//                        );
//
//                Predicate descriptionPredicate =
//                        criteriaBuilder.like(
//                                criteriaBuilder.lower(root.get("description")),
//                                "%" + keyword.toLowerCase() + "%"
//                        );
//
//                predicates.add(
//                        criteriaBuilder.or(
//                                namePredicate,
//                                descriptionPredicate
//                        )
//                );
//            }

            // Global keyword search (Name, Description, City, State)
            if (keyword != null && !keyword.isBlank()) {

                String searchKeyword = "%" + keyword.toLowerCase() + "%";

                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        searchKeyword
                );

                Predicate descriptionPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        searchKeyword
                );

                Predicate cityPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("city")),
                        searchKeyword
                );

                Predicate statePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("state")),
                        searchKeyword
                );

                predicates.add(
                        criteriaBuilder.or(
                                namePredicate,
                                descriptionPredicate,
                                cityPredicate,
                                statePredicate
                        )
                );
            }

            // Filter by city
            if (city != null && !city.isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("city")),
                                city.toLowerCase()
                        )
                );
            }

            // Filter by state
            if (state != null && !state.isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("state")),
                                state.toLowerCase()
                        )
                );
            }

            // Filter by open/closed
            if (open != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("open"), open)
                );
            }

            // Filter by minimum rating
            if (minRating != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("rating"),
                                minRating
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}