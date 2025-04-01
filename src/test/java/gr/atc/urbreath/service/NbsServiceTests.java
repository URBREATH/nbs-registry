package gr.atc.urbreath.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gr.atc.urbreath.dto.GeoLocationDto;
import gr.atc.urbreath.dto.NbsDataDto;
import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.models.GeoLocation;
import gr.atc.urbreath.models.Nbs;

import static gr.atc.urbreath.exception.CustomExceptions.*;
import gr.atc.urbreath.repository.NbsRepository;

@ExtendWith(MockitoExtension.class)
class NbsServiceTests {

    @Mock
    private NbsRepository nbsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NbsService nbsService;

    private gr.atc.urbreath.models.Nbs mockNbs;
    private NbsDataDto mockNbsDto;
    private GeoLocation mockGeoLocation;
    private GeoLocationDto mockGeoLocationDto;
    private List<Nbs> mockNbsList;
    private Pageable mockPageable;

    @BeforeEach
    void setUp() {
        // Mock Data
        mockGeoLocation = new GeoLocation(1.0, 2.0, null);

        mockGeoLocationDto = new GeoLocationDto(1.0, 2.0, null);

        mockNbs = new Nbs();
        mockNbs.setId("mock-id");
        mockNbs.setTitle("mock-title");
        mockNbs.setPilot("Athens");
        mockNbs.setClimateZone(ClimateZone.MEDITERRANEAN);
        mockNbs.setGeoLocation(mockGeoLocation);

        mockNbsDto = NbsDataDto.builder()
                .id("mock-id")
                .title("mock-title")
                .pilot("Athens")
                .climateZone(ClimateZone.MEDITERRANEAN.toString())
                .geoLocation(mockGeoLocationDto)
                .build();

        // Setup mock NbsList
        Nbs mockNbs2 = new Nbs();
        mockNbs2.setId("mock-id-2");
        mockNbs2.setTitle("mock-title-2");
        mockNbs2.setPilot("Madrid");
        mockNbs2.setClimateZone(ClimateZone.BOREAL);
        
        GeoLocation geoLocation2 = new GeoLocation(3.0, 4.0, null);
        mockNbs2.setGeoLocation(geoLocation2);
        
        mockNbsList = List.of(mockNbs, mockNbs2);

        // Setup mock Pageable
        mockPageable = PageRequest.of(0, 10, Sort.Direction.DESC, "dateCreated");
    }

    @Test
    @DisplayName("Retrieve NBS by ID: Success")
    void givenId_whenRetrieveNbsById_thenReturnNbsDto() {
        // Given
        when(nbsRepository.findById("mock-id")).thenReturn(Optional.of(mockNbs));
        when(modelMapper.map(mockNbs, NbsDataDto.class)).thenReturn(mockNbsDto);

        // When
        NbsDataDto result = nbsService.retrieveNbsById("mock-id");

        // Then
        assertNotNull(result);
        assertEquals("mock-id", result.getId());
        assertEquals("mock-title", result.getTitle());
        assertEquals("Athens", result.getPilot());
        assertEquals(ClimateZone.MEDITERRANEAN.toString(), result.getClimateZone());
        assertNotNull(result.getGeoLocation());
        assertEquals(1.0, result.getGeoLocation().getLatitude());
        assertEquals(2.0, result.getGeoLocation().getLongitude());
    }

    @Test
    @DisplayName("Retrieve NBS by ID: Resource Not Found Exception")
    void givenNonExistentId_whenRetrieveNbsById_thenThrowResourceNotFoundException() {
        // Given
        String nonExistentId = "non-existent-id";
        when(nbsRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> nbsService.retrieveNbsById(nonExistentId));
        
        assertEquals("NBS with id: " + nonExistentId + " not found in DB", exception.getMessage());
    }

    @Test
    @DisplayName("Retrieve NBS by ID: Data Mapping Exception")
    void givenId_whenModelMapperFails_thenThrowDataMappingException() {
        // Given
        when(nbsRepository.findById("mock-id")).thenReturn(Optional.of(mockNbs));
        when(modelMapper.map(mockNbs, NbsDataDto.class)).thenThrow(new MappingException(List.of(new ErrorMessage("Test mapping error"))));

        // When & Then
        assertThrows(DataMappingException.class, 
                () -> nbsService.retrieveNbsById("mock-id"));
    }

    @Test
    @DisplayName("retrieveNbsByTitle: Success Case")
    void givenTitle_whenRetrieveNbsByTitle_thenReturnNbsDto() {
        // Given
        when(nbsRepository.findByTitle("mock-title")).thenReturn(Optional.of(mockNbs));
        when(modelMapper.map(mockNbs, NbsDataDto.class)).thenReturn(mockNbsDto);

        // When
        NbsDataDto result = nbsService.retrieveNbsByTitle("mock-title");

        // Then
        assertNotNull(result);
        assertEquals("mock-id", result.getId());
        assertEquals("mock-title", result.getTitle());
    }

    @Test
    @DisplayName("retrieveNbsByTitle: Resource Not Found Exception")
    void givenNonExistentTitle_whenRetrieveNbsByTitle_thenThrowResourceNotFoundException() {
        // Given
        String nonExistentTitle = "non-existent-title";
        when(nbsRepository.findByTitle(nonExistentTitle)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> nbsService.retrieveNbsByTitle(nonExistentTitle));
        
        assertEquals("NBS with title: " + nonExistentTitle + " not found in DB", exception.getMessage());
    }

    @Test
    @DisplayName("Retrieve all NBS Geolocations: Success")
    void whenRetrieveAllNbsGeolocations_thenReturnListOfGeoLocationDto() {
        // Given
        when(nbsRepository.findAll()).thenReturn(mockNbsList);
        when(modelMapper.map(any(GeoLocation.class), eq(GeoLocationDto.class)))
                .thenReturn(mockGeoLocationDto)
                .thenReturn(new GeoLocationDto(3.0, 4.0, "Madrid"));

        // When
        List<GeoLocationDto> result = nbsService.retrieveAllNbsGeolocations();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1.0, result.get(0).getLatitude());
        assertEquals(2.0, result.get(0).getLongitude());
        assertEquals(3.0, result.get(1).getLatitude());
        assertEquals(4.0, result.get(1).getLongitude());
    }

    @Test
    @DisplayName("Retrieve all NBS Geolocations: Data Mapping Exception")
    void whenModelMapperFailsForGeoLocations_thenThrowDataMappingException() {
        // Given
        when(nbsRepository.findAll()).thenReturn(mockNbsList);
        when(modelMapper.map(any(GeoLocation.class), eq(GeoLocationDto.class)))
                .thenThrow(new MappingException(List.of(new ErrorMessage("Test mapping error"))));

        // When & Then
        assertThrows(DataMappingException.class, 
                () -> nbsService.retrieveAllNbsGeolocations());
    }

    @Test
    @DisplayName("Retrieve all NBS (Brief Data): Success")
    void whenRetrieveAllNbsBriefData_thenReturnPageOfNbsDto() {
        // Given
        Page<Nbs> mockPage = new PageImpl<>(mockNbsList);
        when(nbsRepository.findNbsBriefData(mockPageable)).thenReturn(mockPage);
        
        NbsDataDto mockNbsDto2 = NbsDataDto.builder()
                .id("mock-id-2")
                .title("mock-title-2")
                .pilot("Madrid")
                .climateZone(ClimateZone.BOREAL.toString())
                .build();
        
        when(modelMapper.map(mockNbs, NbsDataDto.class)).thenReturn(mockNbsDto);
        when(modelMapper.map(mockNbsList.get(1), NbsDataDto.class)).thenReturn(mockNbsDto2);

        // When
        Page<NbsDataDto> result = nbsService.retrieveAllNbsBriefData(mockPageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("mock-id", result.getContent().get(0).getId());
        assertEquals("mock-title", result.getContent().get(0).getTitle());
        assertEquals("mock-id-2", result.getContent().get(1).getId());
        assertEquals("mock-title-2", result.getContent().get(1).getTitle());
    }

    @Test
    @DisplayName("Retrieve NBS by Climate Zone: Success")
    void givenClimateZone_whenRetrieveAllNbsBriefDataByClimateZone_thenReturnPageOfNbsDto() {
        // Given
        List<Nbs> mediterraneanNbs = List.of(mockNbs);
        Page<Nbs> mockPage = new PageImpl<>(mediterraneanNbs);
        
        when(nbsRepository.findNbsBriefDataByClimateZone(ClimateZone.MEDITERRANEAN, mockPageable)).thenReturn(mockPage);
        when(modelMapper.map(mockNbs, NbsDataDto.class)).thenReturn(mockNbsDto);

        // When
        Page<NbsDataDto> result = nbsService.retrieveAllNbsBriefDataByClimateZone(ClimateZone.MEDITERRANEAN, mockPageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("mock-id", result.getContent().get(0).getId());
        assertEquals("mock-title", result.getContent().get(0).getTitle());
        assertEquals(ClimateZone.MEDITERRANEAN.toString(), result.getContent().get(0).getClimateZone());
    }

    @Test
    @DisplayName("Retrieve NBS by Climate Zone: Data Mapping Exception")
    void givenClimateZone_whenModelMapperFails_thenThrowDataMappingException() {
        // Given
        Page<Nbs> mockPage = new PageImpl<>(List.of(mockNbs));
        when(nbsRepository.findNbsBriefDataByClimateZone(ClimateZone.MEDITERRANEAN, mockPageable)).thenReturn(mockPage);
        when(modelMapper.map(mockNbs, NbsDataDto.class)).thenThrow(new MappingException(List.of(new ErrorMessage("Test mapping error"))));

        // When & Then
        assertThrows(DataMappingException.class, 
                () -> nbsService.retrieveAllNbsBriefDataByClimateZone(ClimateZone.MEDITERRANEAN, mockPageable));
        
    }
}