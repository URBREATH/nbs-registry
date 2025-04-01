package gr.atc.urbreath.service;

import gr.atc.urbreath.dto.GeoLocationDto;
import gr.atc.urbreath.dto.NbsCreationDataDto;
import gr.atc.urbreath.dto.NbsDataDto;
import gr.atc.urbreath.enums.ClimateZone;
import gr.atc.urbreath.repository.NbsRepository;
import gr.atc.urbreath.service.interfaces.INbsService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

import static gr.atc.urbreath.exception.CustomExceptions.*;

@Service
@Slf4j
public class NbsService implements INbsService {

    @Value("${minio.bucket}")
    private String minioBucket;

    // Common Strings
    private static final String MAPPING_ERROR = "Unable to map Nbs object to DTO - Error: ";

    private final NbsRepository nbsRepository;

    private final ModelMapper modelMapper;


    public NbsService(NbsRepository nbsRepository, ModelMapper modelMapper) {
        this.nbsRepository = nbsRepository;
        this.modelMapper = modelMapper;
    }

    /*
     * Manage Nature-Based Solutions - CRUD Operations
     */

    /**
     * Retrieve NBS by Id
     *
     * @param nbsId : ID of NBS
     * @return NbsDto if exists
     */
    public NbsDataDto retrieveNbsById(String nbsId){
        try{
            return nbsRepository.findById(nbsId)
                    .map(nbs -> modelMapper.map(nbs, NbsDataDto.class))
                    .orElseThrow(() -> new ResourceNotFoundException("NBS with id: " + nbsId + " not found in DB"));
        } catch (MappingException e){
            throw new DataMappingException(MAPPING_ERROR + e.getMessage());
        }
    }

    /**
     * Retrieve NBS by Title
     *
     * @param nbsTitle : Name of NBS
     * @return NbsDto if exists
     */
    public NbsDataDto retrieveNbsByTitle(String nbsTitle){
        try{
            return nbsRepository.findByTitle(nbsTitle)
                    .map(nbs -> modelMapper.map(nbs, NbsDataDto.class))
                    .orElseThrow(() -> new ResourceNotFoundException("NBS with title: " + nbsTitle + " not found in DB"));
        } catch (MappingException e){
            throw new DataMappingException(MAPPING_ERROR + e.getMessage());
        }
    }

    /**
     * Return the List of Geolocations for all NBS
     *
     * @return List of Geolocations
     */
    @Override
    public List<GeoLocationDto> retrieveAllNbsGeolocations() {
        try{
            return nbsRepository.findAll()
                    .stream()
                    .map(nbs -> modelMapper.map(nbs.getGeoLocation(), GeoLocationDto.class))
                    .toList();
        } catch (MappingException e){
            throw new DataMappingException("Unable to map GeoLocation object to DTO - Error: " + e.getMessage());
        }
    }

    /**
     * Retrieve all NBS paginated
     *
     * @param pageable : Pagination parameters
     * @return Page of NbsRecords
     */
    public Page<NbsDataDto> retrieveAllNbsBriefData(Pageable pageable){
        try{
            return nbsRepository.findNbsBriefData(pageable)
                    .map(nbs -> modelMapper.map(nbs, NbsDataDto.class));
        } catch (MappingException e){
            throw new DataMappingException(MAPPING_ERROR + e.getMessage());
        }
    }

    /**
     * Retrieve NBS per Climate Zone paginated
     *
     * @param climateZone : Climate Zone
     * @param pageable : Pagination parameters
     * @return Page of NbsRecords
     */
    public Page<NbsDataDto> retrieveAllNbsBriefDataByClimateZone(ClimateZone climateZone, Pageable pageable){
        try{
            return nbsRepository.findNbsBriefDataByClimateZone(climateZone, pageable)
                    .map(nbs -> modelMapper.map(nbs, NbsDataDto.class));
        } catch (MappingException e){
            throw new DataMappingException(MAPPING_ERROR + e.getMessage());
        }
    }

    @Override
    public String createNbs(NbsCreationDataDto nbsData) {
        return "";
    }
}
