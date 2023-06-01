package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.event.EventPageResponse;
import nure.ua.volunteering_ua.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventPageMapper implements Function<Page<Event>, EventPageResponse> {

    @Autowired
    EventMapper eventMapper;

    @Override
    public EventPageResponse apply(Page<Event> events) {
        return new EventPageResponse(
                events
                        .getContent()
                        .stream()
                        .map(eventMapper)
                        .collect(Collectors.toList()),
                events.getNumber(),
                events.getSize(),
                events.getTotalElements(),
                events.getTotalPages()
        );
    }
}
