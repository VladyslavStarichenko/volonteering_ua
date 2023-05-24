package nure.ua.volunteering_ua.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.dto.request.RequestGetDto;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerGetDto {

    private long id;
    private String name;
    private String email;
    private String address;
    private List<RequestGetDto> aid_requests;

}
