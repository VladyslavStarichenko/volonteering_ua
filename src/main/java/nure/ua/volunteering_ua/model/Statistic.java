package nure.ua.volunteering_ua.model;

import lombok.Data;
import nure.ua.volunteering_ua.model.user.Organization;


@Data

public class Statistic {

    private int requestsCount;
    private String deliveredCount;
    private String inVerificationRequestsCount;
    private String approvedRequestsCount;

    public Statistic() {
        this.requestsCount = 0;
        this.deliveredCount = 0 + "%";
        this.inVerificationRequestsCount = 0 + "%";
        this.approvedRequestsCount = 0 + "%";
    }

    public Statistic getStatistic(Organization organization) {
        int countDelivered = getCountByRequestStatus(organization, Request_Status.DELIVERED);
        int countApproved = getCountByRequestStatus(organization, Request_Status.APPROVED);
        int countVerification = getCountByRequestStatus(organization, Request_Status.VERIFICATION);
        int requestsCount = organization.getRequests().size();
        double rating = 0.0;
        double approvedStatistics = 0.0;
        double verificationStatistics = 0.0;
        if (requestsCount != 0) {
            rating = (double) requestsCount / countDelivered * 100;
            approvedStatistics = (double) requestsCount / countApproved * 100;
            verificationStatistics = (double) requestsCount / countVerification * 100;
        }
        Statistic statistic = new Statistic();
        statistic.setDeliveredCount(rating + "%");
        statistic.setRequestsCount(requestsCount);
        statistic.setApprovedRequestsCount(approvedStatistics + "%");
        statistic.setInVerificationRequestsCount(verificationStatistics + "%");
        return statistic;
    }

    private static int getCountByRequestStatus(Organization organization, Request_Status requestStatus) {
        return (int) (organization.getRequests()
                .stream()
                .filter(aid_request -> aid_request.getRequestStatus() == requestStatus)
                .count());
    }
}
