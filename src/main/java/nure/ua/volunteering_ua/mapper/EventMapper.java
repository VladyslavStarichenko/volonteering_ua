package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.event.EventGetDto;
import nure.ua.volunteering_ua.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventMapper implements Function<Event, EventGetDto> {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public EventGetDto apply(Event event) {
        return new EventGetDto(
                event.getName(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getProducts()
                        .stream()
                        .map(productMapper)
                        .collect(Collectors.toList()),
                event.getLocation()
        );
    }
}
