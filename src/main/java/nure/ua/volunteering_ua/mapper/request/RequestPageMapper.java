package nure.ua.volunteering_ua.mapper.request;

import nure.ua.volunteering_ua.dto.request.AidRequestPageResponse;
import nure.ua.volunteering_ua.model.Aid_Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestPageMapper implements Function<Page<Aid_Request>, AidRequestPageResponse> {

    @Autowired
    RequestMapper requestMapper;


    @Override
    public AidRequestPageResponse apply(Page<Aid_Request> requests) {
        return new AidRequestPageResponse(
                requests.getContent()
                        .stream()
                        .map(requestMapper)
                        .collect(Collectors.toList()),
                requests.getNumber(),
                requests.getContent().size(),
                requests.getTotalElements(),
                requests.getTotalPages()
        );
    }
}
