package com.workflex.triprecommendation.repository;

import com.workflex.triprecommendation.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT DISTINCT t.destinationCountry FROM Trip t")
    List<String> findAllDestinationCountries();

    @Query("SELECT DISTINCT t.user.id FROM Trip t WHERE t.destinationCountry = :country")
    List<Long> findUsersByDestinationCountry(@Param("country") String country);

    @Query("SELECT t FROM Trip t WHERE t.user.id IN :userIds AND t.destinationCountry != :excludeCountry")
    List<Trip> findTripsByUsersExcludingCountry(@Param("userIds") List<Long> userIds,
                                                @Param("excludeCountry") String excludeCountry);

    List<Trip> findByUserId(Long userId);

    void deleteByUserEmail(String email);
}