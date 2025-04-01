package gr.atc.urbreath.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import gr.atc.urbreath.dto.GeoLocationDto;
import gr.atc.urbreath.dto.NbsDataDto;
import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.exception.CustomExceptions.DataMappingException;
import gr.atc.urbreath.exception.CustomExceptions.ResourceNotFoundException;
import gr.atc.urbreath.service.interfaces.INbsService;

@WebMvcTest(NbsController.class)
class NbsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private INbsService nbsService;

    private static NbsDataDto mockNbs;
    private static List<NbsDataDto> mockNbsList;
    private static List<GeoLocationDto> mockGeoLocations;

    @BeforeAll
    static void setup() {
        mockNbs = NbsDataDto.builder()
                .id("mock-id")
                .title("mock-title")
                .pilot("Athens")
                .climateZone(ClimateZone.MEDITERRANEAN.toString())
                .geoLocation(new GeoLocationDto(1.0, 2.0, null))
                .build();

        NbsDataDto mockNbs2 = NbsDataDto.builder()
                .id("mock-id-2")
                .title("mock-title-2")
                .pilot("Madrid")
                .climateZone(ClimateZone.BOREAL.toString())
                .geoLocation(new GeoLocationDto(3.0, 4.0, null))
                .build();
        
        mockNbsList = List.of(mockNbs, mockNbs2);

        mockGeoLocations = List.of(
                new GeoLocationDto(1.0, 2.0, "Athens"),
                new GeoLocationDto(3.0, 4.0, "Madrid")
        );
    }

    @DisplayName("Retrieve NBS by ID: Success")
    @Test
    @WithMockUser
    void givenId_whenRetrieveNbsById_thenReturnNbs() throws Exception {
        // Given
        when(nbsService.retrieveNbsById(anyString())).thenReturn(mockNbs);

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/{id}", "mock-id"))
                                        .andDo(print()); // Print request and response details

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("mock-id"))
                .andExpect(jsonPath("$.data.title").value("mock-title"))
                .andExpect(jsonPath("$.data.pilot").value("Athens"))
                .andExpect(jsonPath("$.data.climateZone").value(ClimateZone.MEDITERRANEAN.toString()))
                .andExpect(jsonPath("$.data.geoLocation.latitude").value(1.0))
                .andExpect(jsonPath("$.data.geoLocation.longitude").value(2.0));
    }

    @DisplayName("Retrieve NBS by ID: Resource Not Found")
    @Test
    @WithMockUser
    void givenNonExistentId_whenRetrieveNbsById_thenReturnNotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        when(nbsService.retrieveNbsById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("NBS with id: " + nonExistentId + " not found in DB"));

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/{id}", nonExistentId))
                                        .andDo(print());

        // Then
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").value("NBS with id: non-existent-id not found in DB"));
    }

    @DisplayName("Retrieve NBS by ID: Data Mapping Exception")
    @Test
    @WithMockUser
    void givenId_whenDataMappingFails_thenReturnInternalServerError() throws Exception {
        // Given
        String id = "mapping-error-id";
        when(nbsService.retrieveNbsById(id))
                .thenThrow(new DataMappingException("Unable to map Nbs object to DTO - Error: Test error"));

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/{id}", id))
                                        .andDo(print());

        // Then
        response.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").value("Unable to map Nbs object to DTO - Error: Test error"));
    }

    @DisplayName("Retrieve NBS by Title: Success")
    @Test
    @WithMockUser
    void givenTitle_whenRetrieveNbsByTitle_thenReturnNbs() throws Exception {
        // Given
        when(nbsService.retrieveNbsByTitle(anyString())).thenReturn(mockNbs);

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/title/{title}", "mock-title"))
                                        .andDo(print());

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("mock-id"))
                .andExpect(jsonPath("$.data.title").value("mock-title"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("NBS retrieved successfully"));
    }

    @DisplayName("Retrieve NBS by Title: Resource Not Found")
    @Test
    @WithMockUser
    void givenNonExistentTitle_whenRetrieveNbsByTitle_thenReturnNotFound() throws Exception {
        // Given
        String nonExistentTitle = "non-existent-title";
        when(nbsService.retrieveNbsByTitle(nonExistentTitle))
                .thenThrow(new ResourceNotFoundException("NBS with title: " + nonExistentTitle + " not found in DB"));

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/title/{title}", nonExistentTitle))
                                        .andDo(print());

        // Then
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").value("NBS with title: non-existent-title not found in DB"));
    }

    @DisplayName("Retrieve All NBS Brief Data: Success")
    @Test
    @WithMockUser
    void whenRetrieveAllNbsBriefData_thenReturnPageOfNbs() throws Exception {
        // Given
        Page<NbsDataDto> mockPage = new PageImpl<>(mockNbsList);
        when(nbsService.retrieveAllNbsBriefData(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs")
                                        .param("page", "0")
                                        .param("size", "10")
                                        .param("sort", "dateCreated")
                                        .param("direction", "desc"))
                                        .andDo(print());

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results", hasSize(2)))
                .andExpect(jsonPath("$.data.lastPage").value(true))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("NBSs retrieved successfully"));
    }

    @DisplayName("Retrieve All NBS Brief Data: Data Mapping Exception")
    @Test
    @WithMockUser
    void whenRetrieveAllNbsBriefData_andMappingFails_thenReturnInternalServerError() throws Exception {
        // Given
        when(nbsService.retrieveAllNbsBriefData(any(Pageable.class)))
                .thenThrow(new DataMappingException("Unable to map Nbs object to DTO - Error: Test error"));

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs")
                                        .param("page", "0")
                                        .param("size", "10"))
                                        .andDo(print());

        // Then
        response.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").value("Unable to map Nbs object to DTO - Error: Test error"));
    }

    @DisplayName("Retrieve NBS by Climate Zone: Success")
    @Test
    @WithMockUser
    void givenClimateZone_whenRetrieveNbsByClimateZone_thenReturnPageOfNbs() throws Exception {
        // Given
        Page<NbsDataDto> mockPage = new PageImpl<>(List.of(mockNbs));
        when(nbsService.retrieveAllNbsBriefDataByClimateZone(eq(ClimateZone.MEDITERRANEAN), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/zone/{climateZone}", "Mediterranean")
                                        .param("page", "0")
                                        .param("size", "10")
                                        .param("sort", "dateCreated")
                                        .param("direction", "desc"))
                                        .andDo(print());

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results", hasSize(1)))
                .andExpect(jsonPath("$.data.lastPage").value(true))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("NBSs for Mediterranean zone retrieved successfully"));
    }

    @DisplayName("Retrieve All NBS Geolocations: Success")
    @Test
    @WithMockUser
    void whenRetrieveNbsGeoLocations_thenReturnListOfGeoLocations() throws Exception {
        // Given
        when(nbsService.retrieveAllNbsGeolocations()).thenReturn(mockGeoLocations);

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/geolocations"))
                                        .andDo(print());

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].latitude").value(1.0))
                .andExpect(jsonPath("$.data[0].longitude").value(2.0))
                .andExpect(jsonPath("$.data[1].latitude").value(3.0))
                .andExpect(jsonPath("$.data[1].longitude").value(4.0))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("NBSs geolocations retrieved successfully"));
    }

    @DisplayName("Retrieve All NBS Geolocations: Data Mapping Exception")
    @Test
    @WithMockUser
    void whenRetrieveNbsGeoLocations_andMappingFails_thenReturnInternalServerError() throws Exception {
        // Given
        when(nbsService.retrieveAllNbsGeolocations())
                .thenThrow(new DataMappingException("Unable to map GeoLocation object to DTO - Error: Test error"));

        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/geolocations"))
                                        .andDo(print());

        // Then
        response.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Data mapping error between Model-DTO"))
                .andExpect(jsonPath("$.errors").value("Unable to map GeoLocation object to DTO - Error: Test error"));
    }


    @DisplayName("Invalid Climate Zone: Bad Request")
    @Test
    @WithMockUser
    void givenInvalidClimateZone_whenRetrieveNbsByClimateZone_thenReturnBadRequest() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/api/nbs/zone/{climateZone}", "INVALID_ZONE")
                                        .param("page", "0")
                                        .param("size", "10"))
                                        .andDo(print());

        // Then
        response.andExpect(status().isBadRequest());
    }
}
