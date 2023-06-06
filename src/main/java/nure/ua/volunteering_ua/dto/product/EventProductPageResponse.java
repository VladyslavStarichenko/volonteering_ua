package nure.ua.volunteering_ua.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventProductPageResponse {

    List<EventProductGetDto> products;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
