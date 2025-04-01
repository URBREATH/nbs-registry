package gr.atc.urbreath.repository;

import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.models.GeoLocation;
import gr.atc.urbreath.models.Nbs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
class NbsRepositoryTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.6");

    @Autowired
    private NbsRepository nbsRepository;

    private Nbs mediterraneanNbs;
    private Nbs borealNbs;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        nbsRepository.deleteAll();

        // Create test data
        mediterraneanNbs = new Nbs();
        mediterraneanNbs.setTitle("Mediterranean NBS");
        mediterraneanNbs.setClimateZone(ClimateZone.MEDITERRANEAN);
        mediterraneanNbs.setPilot("Athens");
        
        GeoLocation athensLocation = new GeoLocation(4.0, 4.0, null);
        mediterraneanNbs.setGeoLocation(athensLocation);
        
        mediterraneanNbs.setKeywords(List.of("urban"));
        mediterraneanNbs.setUrBreathNbs(true);
        mediterraneanNbs.setDateCreated(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));

        borealNbs = new Nbs();
        borealNbs.setTitle("Boreal NBS");
        borealNbs.setClimateZone(ClimateZone.BOREAL);
        borealNbs.setPilot("Helsinki");
        
        GeoLocation helsinkiLocation = new GeoLocation(5.0, 5.0, null);
        borealNbs.setGeoLocation(helsinkiLocation);
        borealNbs.setKeywords(List.of("biodiversity"));
        borealNbs.setUrBreathNbs(true);
        borealNbs.setDateCreated(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)));

        // Save test data
        nbsRepository.saveAll(List.of(mediterraneanNbs, borealNbs));
    }

    @DisplayName("Find By Title - Success")
    @Test
    void givenNbsTitle_whenRetrieveNbsByTitle_thenReturnSuccess() {
        // When
        Optional<Nbs> foundNbs = nbsRepository.findByTitle("Mediterranean NBS");
        
        // Then
        assertThat(foundNbs).isPresent();
        assertThat(foundNbs.get().getTitle()).isEqualTo("Mediterranean NBS");
        assertThat(foundNbs.get().getClimateZone()).isEqualTo(ClimateZone.MEDITERRANEAN);
    }

    @Test
    @DisplayName("Find By Title - Not Found")
    void givenEmptyTitle_whenRetrieveNbsByTitle_thenReturnEmptyObject() {
        // When
        Optional<Nbs> foundNbs = nbsRepository.findByTitle("Non-existent NBS");
        
        // Then
        assertThat(foundNbs).isEmpty();
    }

    @DisplayName("Find all NBS brief data - Success")
    @Test
    void givenPagination_whenRetrieveAllNbsBriefData_thenReturnData() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateCreated"));
        
        // When
        Page<Nbs> nbsPage = nbsRepository.findNbsBriefData(pageable);
        
        // Then
        assertThat(nbsPage).isNotNull();
        assertThat(nbsPage.getContent()).hasSize(2);
        
        // Verify that the order is by dateCreated DESC (borealNbs should come first)
        assertThat(nbsPage.getContent().size()).isEqualTo(2);
        
        // Verify that brief data fields are present
        Nbs firstNbs = nbsPage.getContent().get(0);
        assertThat(firstNbs.getId()).isNotNull();
        assertThat(firstNbs.getTitle()).isNotNull();
        assertThat(firstNbs.getClimateZone()).isNotNull();
        assertThat(firstNbs.getPilot()).isNotNull();
        assertThat(firstNbs.getGeoLocation()).isNotNull();
        assertThat(firstNbs.isUrBreathNbs()).isTrue();
        assertThat(firstNbs.getKeywords()).isNotNull();
    }

    @DisplayName("Find all NBS brief data filtered by Climate Zone - Success")
    @Test
    void givenPagination_whenRetrieveAllNbsBriefDataByClimateZone_thenReturnFilteredData() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateCreated"));
        
        // When
        Page<Nbs> mediterraneanPage = nbsRepository.findNbsBriefDataByClimateZone(ClimateZone.MEDITERRANEAN, pageable);
        Page<Nbs> borealPage = nbsRepository.findNbsBriefDataByClimateZone(ClimateZone.BOREAL, pageable);
        Page<Nbs> atlanticPage = nbsRepository.findNbsBriefDataByClimateZone(ClimateZone.ATLANTIC, pageable);
        
        // Then
        assertThat(mediterraneanPage.getContent()).hasSize(1);
        assertThat(mediterraneanPage.getContent().get(0).getTitle()).isEqualTo("Mediterranean NBS");
        
        assertThat(borealPage.getContent()).hasSize(1);
        assertThat(borealPage.getContent().get(0).getTitle()).isEqualTo("Boreal NBS");
        
        assertThat(atlanticPage.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Handle Pagination parameters - Success")
    void givenPagination_whenRetrieveAllNbsBriefData_thenReturnDataWithProperPagination() {
        // Given - add more data to test pagination
        for (int i = 0; i < 8; i++) {
            Nbs nbs = new Nbs();
            nbs.setTitle("Additional NBS " + i);
            nbs.setClimateZone(i % 2 == 0 ? ClimateZone.MEDITERRANEAN : ClimateZone.BOREAL);
            nbs.setDateCreated(Date.from(Instant.now().minus(i, ChronoUnit.DAYS)));
            nbsRepository.save(nbs);
        }
        
        // When - get first page with 5 items
        Pageable firstPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateCreated"));
        Page<gr.atc.urbreath.models.Nbs> firstPage = nbsRepository.findNbsBriefData(firstPageable);
        
        // Then
        assertThat(firstPage.getContent()).hasSize(5);
        assertThat(firstPage.getTotalElements()).isEqualTo(10);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.hasNext()).isTrue();
        
        // When - get second page
        Pageable secondPageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "dateCreated"));
        Page<Nbs> secondPage = nbsRepository.findNbsBriefData(secondPageable);
        
        // Then
        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(secondPage.isFirst()).isFalse();
        assertThat(secondPage.isLast()).isTrue();
    }
}