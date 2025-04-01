package gr.atc.urbreath.service.interfaces;

import java.util.List;

public interface IDataCollectorService {

    List<String> retrieveDatasetsFromIdra();

    List<String> retrieveKpisFromKpiManager();
}
