package gr.atc.urbreath.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import gr.atc.urbreath.enums.NbsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "NbsDataDto", description = "NBS Information")
public class NbsDataDto {

    /*
     * Brief Data Fields
     */
    private String id;

    private String climateZone;

    private String title;

    private String pilot;

    private String mainImage;

    private GeoLocationDto geoLocation;

    private boolean isUrBreathNbs;

    private List<String> keywords;

    /*
     * Detailed Data Fields
     */
    private Date dateCreated;

    private NbsStatus status;

    private List<String> relatedMaterial;

    private String objective;

    private String challenges;

    private String potentialImpactsAndBenefits;

    private String lessonsLearnt;

    private List<String> problems;

    private List<String> images;

    private List<String> videos;

    private List<String> idraDatasets;

    private List<String> kpis;
}
