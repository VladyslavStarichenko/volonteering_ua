package nure.ua.volunteering_ua.service.feedback;

import nure.ua.volunteering_ua.dto.feedback.FeedBackCreateDto;
import nure.ua.volunteering_ua.dto.feedback.FeedBackGetDto;
import nure.ua.volunteering_ua.dto.feedback.FeedBackPageResponse;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.FeedBackMapper;
import nure.ua.volunteering_ua.mapper.FeedBackPageMapper;
import nure.ua.volunteering_ua.model.Feedback;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.Organization;
import nure.ua.volunteering_ua.repository.feedbsck.FeedBackRepository;
import nure.ua.volunteering_ua.service.customer.CustomerService;
import nure.ua.volunteering_ua.service.organization.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FeedBackService {


    private final FeedBackRepository feedBackRepository;
    private final FeedBackMapper feedBackMapper;
    private final CustomerService customerService;
    private final OrganizationService organizationService;

    private final FeedBackPageMapper feedBackPageMapper;


    @Autowired
    public FeedBackService(FeedBackRepository feedBackRepository, FeedBackMapper feedBackMapper, CustomerService customerService, OrganizationService organizationService, FeedBackPageMapper feedBackPageMapper) {
        this.feedBackRepository = feedBackRepository;
        this.feedBackMapper = feedBackMapper;
        this.customerService = customerService;
        this.organizationService = organizationService;
        this.feedBackPageMapper = feedBackPageMapper;
    }

    public FeedBackGetDto createFeedback(FeedBackCreateDto feedBackCreateDto) {
        Customer loggedInCustomer = customerService
                .getCurrentLoggedInCustomerInternal();

        Organization organization = organizationService.
                getOrganizationByNameInternalUsage(feedBackCreateDto.getOrganizationName());

        Feedback feedback = new Feedback(
                feedBackCreateDto.getComment(),
                loggedInCustomer,
                organization,
                feedBackCreateDto.getRating()
        );
        return feedBackMapper.apply(feedBackRepository.save(feedback));
    }

    public FeedBackPageResponse getAllFeedBacks(int pageNumber, int sizeOfPage, String sortBy, Long organizationId) {
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        return feedBackPageMapper.apply(feedBackRepository.getAllByOrganization_id(pageable,organizationId));
    }

    public FeedBackGetDto getFeedBackById(long id) {
        return feedBackMapper.apply(feedBackRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException(
                                "There is no comment with specified Id",
                                HttpStatus.NOT_FOUND
                        )
                ));
    }

    public Feedback getFeedBackByIdInternal(long id) {
        return feedBackRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException(
                                "There is no comment with specified Id",
                                HttpStatus.NOT_FOUND
                        )
                );
    }


}
