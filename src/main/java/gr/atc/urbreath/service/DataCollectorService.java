package gr.atc.urbreath.service;

import com.fasterxml.jackson.databind.JsonNode;

import static gr.atc.urbreath.exception.CustomExceptions.*;

import gr.atc.urbreath.service.interfaces.IDataCollectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DataCollectorService implements IDataCollectorService {

    @Value("${urbreath.kpi.manager.url}")
    private String kpiManagerUrl;

    @Value("${urbreath.idra.url}")
    private String idraUrl;

    private final WebClient webClient;

    public DataCollectorService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Retrieve Datasets From Idra Component
     *
     * @return List of Datasets
     */
    @Retryable(retryFor = {
            HttpServerErrorException.ServiceUnavailable.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200)
    )
    @Override
    public List<String> retrieveDatasetsFromIdra() {
        try {
            JsonNode response = webClient.get()
                    .uri(idraUrl)
                    .retrieve()
                    .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(), resp -> {
                        log.error("Unable to retrieve datasets from Idra. Status Code: {}", resp.statusCode());
                        return Mono.error(new WebClientRequestException("Unable to retrieve datasets from Idra"));
                    })
                    .bodyToMono(JsonNode.class)
                    .cache(Duration.ofMinutes(5))
                    .block();

            return extractDatasetIds(response);
        } catch (WebClientException e) {
            log.error("Error retrieving datasets from Idra", e);
            throw new WebClientRequestException("Failed to retrieve datasets: " + e.getMessage());
        }
    }

    /**
     * Retrieve KPIs from KPI Manager Component
     *
     * @return List of KPIs
     */
    @Retryable(retryFor = {
            HttpServerErrorException.ServiceUnavailable.class, WebClientRequestException.class, WebClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200)
    )
    @Override
    public List<String> retrieveKpisFromKpiManager() {
        try {
            JsonNode response = webClient.get()
                    .uri(kpiManagerUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(), resp -> {
                        log.error("Unable to retrieve KPIs from KPI Manager. Status Code: {}", resp.statusCode());
                        return Mono.error(new WebClientRequestException("Unable to retrieve KPIs from KPI Manager"));
                    })
                    .bodyToMono(JsonNode.class)
                    .cache(Duration.ofMinutes(5))
                    .block();

            return extractKpiIds(response);
        } catch (WebClientException e) {
            log.error("Error retrieving KPIs from KPI Manager", e);
            throw new WebClientRequestException("Failed to retrieve KPIs: " + e.getMessage());
        }
    }

    // Helper method to extract dataset IDs
    private List<String> extractDatasetIds(JsonNode response) {
        return new ArrayList<>();
    }

    // Helper method to extract KPI IDs
    private List<String> extractKpiIds(JsonNode response) {
        return new ArrayList<>();
    }
}
