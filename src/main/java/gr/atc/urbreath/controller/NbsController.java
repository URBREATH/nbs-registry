package gr.atc.urbreath.controller;

import gr.atc.urbreath.controller.responses.BaseAppResponse;
import gr.atc.urbreath.controller.responses.PaginationAttributesResponse;
import gr.atc.urbreath.dto.GeoLocationDto;
import gr.atc.urbreath.dto.NbsCreationDataDto;
import gr.atc.urbreath.dto.NbsDataDto;
import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.exception.CustomExceptions.DataMappingException;
import gr.atc.urbreath.exception.CustomExceptions.ResourceNotFoundException;
import gr.atc.urbreath.service.interfaces.INbsService;
import gr.atc.urbreath.validation.ValidClimateZone;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nbs")
@Tag(name = "NBS Management Controller", description = "Manage Nature-Based Solutions")
public class NbsController {

    private final INbsService nbsService;

    public NbsController(INbsService nbsService){
        this.nbsService = nbsService;
    }

    /*
     * Manage Nature-Based Solutions - Retrieve Operations
     */

    /**
     * Retrieve a NBS by ID
     *
     * @param id : NBS ID
     * @return NbsDataDto if exists
     */
    @Operation(summary = "Retrieve a NBS by ID", security = @SecurityRequirement(name = ""))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NBS retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "NBS not found", 
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BaseAppResponse.class))),
            @ApiResponse(responseCode = "500", description = "Invalid Data Mapping", 
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = BaseAppResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseAppResponse<NbsDataDto>> retrieveNbsInformationById(@PathVariable @NotEmpty(message = "ID is required") String id) throws ResourceNotFoundException, DataMappingException{
        return new ResponseEntity<>(BaseAppResponse.success(nbsService.retrieveNbsById(id), "NBS retrieved successfully"), HttpStatus.OK);
    }

    /**
     * Retrieve a NBS by Title
     *
     * @param title : NBS Title
     * @return NbsDataDto if exists
     */
    @Operation(summary = "Retrieve a NBS by Title", security = @SecurityRequirement(name = ""))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NBS retrieved successfully")
    })
    @GetMapping("/title/{title}")
    public ResponseEntity<BaseAppResponse<NbsDataDto>> retrieveNbsInformationByTitle(@PathVariable @NotEmpty(message = "NBS title is required") String title){
        return new ResponseEntity<>(BaseAppResponse.success(nbsService.retrieveNbsByTitle(title), "NBS retrieved successfully"), HttpStatus.OK);
    }

    /**
     * Retrieve all NBS Brief Information Paginated
     *
     * @param page : Requested page
     * @param size : Size of returned elements
     * @param sort : Sorting Field
     * @param direction : Direction of Sorting
     * @return Page of NbsDataDto if exists
     */
    @Operation(summary = "Retrieve all NBS Brief Information", security = @SecurityRequirement(name = ""))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NBSs retrieved successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = NbsDataDto.class)))
            // TODO: Document Error Responses
    })
    @GetMapping("/")
    public ResponseEntity<BaseAppResponse<PaginationAttributesResponse<NbsDataDto>>> retrieveAllNbsBriefData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size, @RequestParam(defaultValue = "dateCreated") String sort, @RequestParam(defaultValue = "desc") String direction){
        Page<NbsDataDto> nbsPage = nbsService.retrieveAllNbsBriefData(generatePageableObject(page, size, sort, direction));

        return new ResponseEntity<>(BaseAppResponse.success(formulatePaginatedResponse(nbsPage), "NBSs retrieved successfully"), HttpStatus.OK);
    }

    /**
     * Retrieve all NBS Brief Information Paginated
     *
     * @param climateZone : Climate Zone
     * @param page : Requested page
     * @param size : Size of returned elements
     * @param sort : Sorting Field
     * @param direction : Direction of Sorting
     * @return Page of NbsDataDto if exists for specific climate Zone
     */
    @Operation(summary = "Retrieve all NBS Brief Information filtered by Climate Zone", security = @SecurityRequirement(name = ""))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NBSs for {climateZone} zone retrieved successfully")
            // TODO: Document Error Responses
    })
    @GetMapping("/zone/{climateZone}")
    public ResponseEntity<BaseAppResponse<PaginationAttributesResponse<NbsDataDto>>> retrieveAllNbsBriefDataByClimateZone(@PathVariable @ValidClimateZone String climateZone, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size, @RequestParam(defaultValue = "dateCreated") String sort, @RequestParam(defaultValue = "desc") String direction){
        Page<NbsDataDto> nbsPage = nbsService.retrieveAllNbsBriefDataByClimateZone(ClimateZone.fromString(climateZone), generatePageableObject(page, size, sort, direction));

        return new ResponseEntity<>(BaseAppResponse.success(formulatePaginatedResponse(nbsPage), "NBSs for " + climateZone + " zone retrieved successfully"), HttpStatus.OK);
    }

    /**
     * Retrieve all NBS Geolocations
     *
     * @return List of Geolocations
     */
    @Operation(summary = "Retrieve all NBS GeoLocations", security = @SecurityRequirement(name = ""))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NBSs geolocations retrieved successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = GeoLocationDto.class)))
                // TODO: Document Error Responses
    })
    @GetMapping("/geolocations")
    public ResponseEntity<BaseAppResponse<List<GeoLocationDto>>> retrieveNbsGeoLocations(){
        return new ResponseEntity<>(BaseAppResponse.success(nbsService.retrieveAllNbsGeolocations(), "NBSs geolocations retrieved successfully"), HttpStatus.OK);
    }

    /*
     * Manage Nature-Based Solutions - Manage Operations
     */
    @Operation(summary = "Create a new NBS", security = @SecurityRequirement(name = "bearerToken"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "NBS created successfully")
            // TODO: Document Error Responses
    })
    @PreAuthorize(value = "hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<BaseAppResponse<String>> retrieveNbsInformationById(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid NbsCreationDataDto nbsData){
        return new ResponseEntity<>(BaseAppResponse.success(nbsService.createNbs(nbsData), "NBS created successfully"), HttpStatus.CREATED);
    }

    /*
     * Supporting Methods
     */

    /**
     * Generate Pageable Object
     * @param page : Requested page
     * @param size : Size of returned elements
     * @param sort : Sorting Field
     * @param direction : Direction of Sorting
     * @return Pageable
     */
    private Pageable generatePageableObject(int page, int size, String sort, String direction){
        return PageRequest.of(page, size, Sort.Direction.fromString(direction), sort);
    }

    /**
     * Formulate Pagination Attributes Response
     *
     * @param nbsPage : Page of NbsDataDto
     * @return PaginationAttributesResponse<NbsDataDto>
     */
    private PaginationAttributesResponse<NbsDataDto> formulatePaginatedResponse(Page<NbsDataDto> nbsPage){
        return new PaginationAttributesResponse<>(
                nbsPage.getContent(),
                nbsPage.getTotalPages(),
                (int) nbsPage.getTotalElements(),
                nbsPage.isLast()
        );
    }
}