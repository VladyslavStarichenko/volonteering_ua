package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.feedback.FeedBackPageResponse;
import nure.ua.volunteering_ua.model.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FeedBackPageMapper implements Function<Page<Feedback>, FeedBackPageResponse> {


    private final FeedBackMapper feedBackMapper;

    @Autowired
    public FeedBackPageMapper(FeedBackMapper feedBackMapper) {
        this.feedBackMapper = feedBackMapper;
    }

    @Override
    public FeedBackPageResponse apply(Page<Feedback> feedbacks) {
        return new FeedBackPageResponse(
                feedbacks.getContent()
                        .stream()
                        .map(feedBackMapper)
                        .collect(Collectors.toList()),
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages()
        );
    }
}
