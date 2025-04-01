package gr.atc.urbreath.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.enums.NbsStatus;
import gr.atc.urbreath.validation.ValidClimateZone;
import gr.atc.urbreath.validation.ValidNbsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "NbsCreationDataDto", description = "NBS Information when creating a new NBS")
public class NbsCreationDataDto {
    private String id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    private Date dateCreated;

    @ValidClimateZone
    private ClimateZone climateZone;

    @Valid
    private GeoLocationDto geoLocation;

    @ValidNbsStatus
    private NbsStatus status;

    @NotEmpty(message = "Pilot is required")
    private String pilot;

    private boolean isUrBreathNbs;

    private List<String> relatedMaterial;

    // Detailed description fields
    @NotBlank(message = "Area characterization is required")
    private String areaCharacterization;

    @NotBlank(message = "Objective is required")
    private String objective;

    @NotBlank(message = "Challenges is required")
    private String challenges;

    @NotBlank(message = "Potential impacts and benefits is required")
    private String potentialImpactsAndBenefits;

    @NotBlank(message = "Lessons learnt is required")
    private String lessonsLearnt;

    @NotEmpty(message = "Keywords are required")
    private List<String> keywords;

    @NotEmpty(message = "Problems are required")
    private List<String> problems;

    // File references
    @NotNull(message = "Main image is required")
    private MultipartFile mainImage;

    private List<MultipartFile> images;

    private List<MultipartFile> videos;

    // External data
    @NotEmpty(message = "Idra datasets are required")
    private List<String> idraDatasets;

    @NotEmpty(message = "KPIs are required")
    private List<String> kpis;
}
