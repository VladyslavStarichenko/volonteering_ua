package nure.ua.volunteering_ua.mapper;


import nure.ua.volunteering_ua.dto.request.RequestDto;
import nure.ua.volunteering_ua.model.Aid_Request;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RequestMapper implements Function<Aid_Request, RequestDto> {
    @Override
    public RequestDto apply(Aid_Request aid_request) {
        return new RequestDto(
                aid_request.getId(),
                aid_request.getTitle(),
                aid_request.getDescription(),
                aid_request.getAmount(),
                aid_request.getOrganization().getName(),
                aid_request.getCustomer().getUser().getUserName(),
                aid_request.getRequestStatus(),
                aid_request.getVolunteeringType(),
                aid_request.getReceivingAddress(),
                aid_request.getQueueNumber());
    }
}
