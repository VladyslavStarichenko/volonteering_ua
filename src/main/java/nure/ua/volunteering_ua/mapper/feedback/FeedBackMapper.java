package nure.ua.volunteering_ua.mapper.feedback;

import nure.ua.volunteering_ua.dto.feedback.FeedBackGetDto;
import nure.ua.volunteering_ua.model.Feedback;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class FeedBackMapper implements Function<Feedback, FeedBackGetDto> {
    @Override
    public FeedBackGetDto apply(Feedback feedback) {
        return new FeedBackGetDto(
                feedback.getId(),
                feedback.getComment(),
                feedback.getCustomer().getUser().getUserName(),
                feedback.getOrganization().getName(),
                feedback.getRating(),
                feedback.isEdited()
        );
    }
}
