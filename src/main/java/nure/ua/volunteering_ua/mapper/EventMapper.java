package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.event.EventGetDto;
import nure.ua.volunteering_ua.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventMapper implements Function<Event, EventGetDto> {

    @Autowired
    private EventProductMapper productMapper;
    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public EventGetDto apply(Event event) {
        return new EventGetDto(
                event.getId(),
                event.getOrganization().getOrganization_admin().getUserName(),
                event.getName(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getProducts()
                        .stream()
                        .map(productMapper)
                        .collect(Collectors.toList()),
                event.getCustomers()
                                .stream()
                        .map(customerMapper)
                                .collect(Collectors.toList()),
                locationMapper.apply(event.getLocation()),
                event.getCapacity(),
                event.getOrganization().getName()

        );
    }
}
