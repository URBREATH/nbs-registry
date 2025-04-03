package gr.atc.urbreath.repository;

import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.models.Nbs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface NbsRepository extends MongoRepository<Nbs, String> {
    Optional<Nbs> findByTitle(String title);

    @Query(value = "{}", fields = "{id: 1, title: 1, climateZone: 1, pilot: 1, mainImage: 1, geoLocation: 1, isUrBreathNbs: 1, keywords: 1, objective: 1}")
    Page<Nbs> findNbsBriefData(Pageable pageable);

    @Query(value = "{'climateZone': ?0}", fields = "{id: 1, title: 1, climateZone: 1, pilot: 1, mainImage: 1, geoLocation: 1, isUrBreathNbs: 1, keywords: 1, objective: 1}")
    Page<Nbs> findNbsBriefDataByClimateZone(ClimateZone climateZone, Pageable pageable);
}
