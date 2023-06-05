package nure.ua.volunteering_ua.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.event.EventCreateDto;
import nure.ua.volunteering_ua.dto.event.EventGetDto;
import nure.ua.volunteering_ua.dto.event.EventPageResponse;
import nure.ua.volunteering_ua.dto.feedback.FeedBackCreateDto;
import nure.ua.volunteering_ua.dto.feedback.FeedBackGetDto;
import nure.ua.volunteering_ua.dto.feedback.FeedBackPageResponse;
import nure.ua.volunteering_ua.service.feedback.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "Operations with feedBacks")
@RequestMapping(value = "/feedbacks/")
@Slf4j
public class FeedBackController {

    private final FeedBackService feedBackService;

    @Autowired
    public FeedBackController(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }

    @ApiOperation(value = "Create a feedback")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @PostMapping("/create")
    public ResponseEntity<FeedBackGetDto> createFeedBack(
            @ApiParam(value = "Feedback object to create the feedback", required = true)
            @RequestBody FeedBackCreateDto feedBackCreateDto) {
        return new ResponseEntity<>(feedBackService.createFeedback(feedBackCreateDto), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get all feedbacks by organization")
    @GetMapping("/all-feedbacks/{pageNumber}/{pageSize}/{sortBy}/{organizationId}")
    public ResponseEntity<FeedBackPageResponse> getAllFeedbacks(
            @ApiParam(value = "Page number to show") @PathVariable int pageNumber,
            @ApiParam(value = "Page size") @PathVariable int pageSize,
            @ApiParam(value = "Sort by") @PathVariable String sortBy,
            @ApiParam(value = "Organization Id") @PathVariable long organizationId


    ) {
        if (sortBy.isBlank()) {
            sortBy = "customer";
        }
        return new ResponseEntity<>(feedBackService
                .getAllFeedBacks(pageNumber, pageSize, sortBy,organizationId), HttpStatus.OK);
    }

    @ApiOperation(value = "Get the feedback by id")
    @GetMapping("/feedback/{feedbackId}")
    public ResponseEntity<FeedBackGetDto> getFeedbackById(
            @ApiParam(value = "CustomerId") @PathVariable Long feedbackId) {
        return new ResponseEntity<>(feedBackService
                .getFeedBackById(feedbackId), HttpStatus.OK);
    }

}
