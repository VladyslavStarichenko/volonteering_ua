package nure.ua.volunteering_ua.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPageResponse {
    private List<CustomerGetDto> customers;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
