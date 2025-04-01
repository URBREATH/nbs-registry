package gr.atc.urbreath.models;

import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.enums.NbsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "nbs")
public class Nbs {

    @MongoId(FieldType.OBJECT_ID)
    private String id;

    @Field(value = "title")
    private String title;

    @Field(value = "climateZone")
    private ClimateZone climateZone;

    @Field(value = "geoLocation")
    private GeoLocation geoLocation;

    @Field(value = "status")
    private NbsStatus status;

    @Field(value = "pilot")
    private String pilot;

    @Field(value = "isUrBreathNbs")
    private boolean isUrBreathNbs;

    @Field(value = "relatedMaterial")
    private List<String> relatedMaterial;

    @Field(value = "dateCreated")
    private Date dateCreated;

    /*
     * Description Texts
     */
    @Field(value = "areaCharacterization")
    private String areaCharacterization;

    @Field(value = "objective")
    private String objective;

    @Field(value = "challenges")
    private String challenges;

    @Field(value = "potentialImpactsAndBenefits")
    private String potentialImpactsAndBenefits;

    @Field(value = "lessonsLearnt")
    private String lessonsLearnt;

    @Field(value = "keywords")
    private List<String> keywords;

    @Field(value = "problems")
    private List<String> problems;

    /*
     * Files (Images - Videos) - Stored in MinIO
     */
    @Field(value = "mainImage")
    private String mainImage;

    @Field(value = "images")
    private List<String> images;

    @Field(value = "videos")
    private List<String> videos;

    /*
     * External Data from Components
     */
    @Field(value = "idraDatasets")
    private List<String> idraDatasets;

    @Field(value = "kpi")
    private List<String> kpis;
}
