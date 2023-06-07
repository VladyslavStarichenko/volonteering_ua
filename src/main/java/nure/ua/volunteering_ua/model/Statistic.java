package nure.ua.volunteering_ua.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nure.ua.volunteering_ua.model.user.Organization;


@Data
public class Statistic {

    private int requestsCount;
    private String deliveredCount;
    private String inVerificationRequestsCount;
    private String approvedRequestsCount;



    public Statistic(int requestsCount, double deliveredCount, double inVerificationRequestsCount,  double approvedRequestsCount) {
        this.requestsCount = requestsCount;
        this.deliveredCount = String.valueOf(deliveredCount) + "%";
        this.inVerificationRequestsCount = String.valueOf(inVerificationRequestsCount) + "%";
        this.approvedRequestsCount = String.valueOf(approvedRequestsCount) + "%";
    }

    public Statistic() {
        this.requestsCount = 0;
        this.deliveredCount = String.valueOf(0 ) + "%";
        this.inVerificationRequestsCount = String.valueOf(0) + "%";
        this.approvedRequestsCount = String.valueOf(0) + "%";
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
            rating = checkStatisticData(countDelivered,requestsCount);
            approvedStatistics = checkStatisticData(countApproved,requestsCount);
            verificationStatistics = checkStatisticData(countVerification,requestsCount);
        }
        return new Statistic(requestsCount,rating, verificationStatistics, approvedStatistics);
    }

    private double checkStatisticData(int data, int requestsCount){
        if(data == 0){
            return  0.0;
        }else {
            return (double) requestsCount / data * 100;
        }
    }

    private static int getCountByRequestStatus(Organization organization, Request_Status requestStatus) {
        return (int) (organization.getRequests()
                .stream()
                .filter(aid_request -> aid_request.getRequestStatus() == requestStatus)
                .count());

    }
}
