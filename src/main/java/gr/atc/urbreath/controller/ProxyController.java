package gr.atc.urbreath.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gr.atc.urbreath.controller.responses.BaseAppResponse;
import gr.atc.urbreath.service.interfaces.IDataCollectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/proxy")
@Tag(name = "Proxy Controller", description = "Retrieve data from external APIs")
public class ProxyController {

    private final IDataCollectorService dataCollectorService;

    public ProxyController(IDataCollectorService dataCollectorService){
        this.dataCollectorService = dataCollectorService;
    }

    /**
     * Retrieve a Datasets From Idra
     *
     * @return List of Datasets
     */
    @Operation(summary = "Retrieve Idra Datasets", security = @SecurityRequirement(name = "bearerToken"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Idra Datasets retrieved successfully")
            // TODO: Document Error Responses
    })
    @PreAuthorize(value = "hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/datasets")
    public ResponseEntity<BaseAppResponse<List<String>>> retrieveDatasetsFromIdra(@AuthenticationPrincipal Jwt jwt){
        return new ResponseEntity<>(BaseAppResponse.success(dataCollectorService.retrieveDatasetsFromIdra(), "Idra Datasets retrieved successfully"), HttpStatus.OK);
    }

    /**
     * Retrieve a KPIs From KPI Manager
     *
     * @return List of KPIs
     */
    @Operation(summary = "Retrieve KPIs", security = @SecurityRequirement(name = ""))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KPIs retrieved successfully")
            // TODO: Document Error Responses
    })
    @PreAuthorize(value = "hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/kpis")
    public ResponseEntity<BaseAppResponse<List<String>>> retrieveKpisFromKpiManager(@AuthenticationPrincipal Jwt jwt){
        return new ResponseEntity<>(BaseAppResponse.success(dataCollectorService.retrieveKpisFromKpiManager(), "KPIs retrieved successfully"), HttpStatus.OK);
    }

}
