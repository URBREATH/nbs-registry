package gr.atc.urbreath.controller.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PaginationAttributesResponse", description = "Pagination Parameters Response")
public class PaginationAttributesResponse<T> {

    private List<T> results;

    private Integer totalPages;

    private Integer totalElements;

    private boolean lastPage;
}
