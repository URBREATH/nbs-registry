package gr.atc.urbreath.service.interfaces;

import gr.atc.urbreath.dto.GeoLocationDto;
import gr.atc.urbreath.dto.NbsCreationDataDto;
import gr.atc.urbreath.dto.NbsDataDto;
import gr.atc.urbreath.enums.ClimateZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INbsService {

    // Retrieve Operations
    NbsDataDto retrieveNbsById(String id);

    NbsDataDto retrieveNbsByTitle(String nbsTitle);

    Page<NbsDataDto> retrieveAllNbsBriefData(Pageable pageable);

    Page<NbsDataDto> retrieveAllNbsBriefDataByClimateZone(ClimateZone climateZone, Pageable pageable);

    List<GeoLocationDto> retrieveAllNbsGeolocations();

    // Manage Methods
    String createNbs(NbsCreationDataDto nbsData);
}
