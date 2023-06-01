package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.location.LocationDto;
import nure.ua.volunteering_ua.model.user.Location;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class LocationMapper implements Function<Location, LocationDto> {
    @Override
    public LocationDto apply(Location location) {
        return new LocationDto(
                location.getLatitude(),
                location.getLongitude(),
                location.getAddress()
        );
    }
}
