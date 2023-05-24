package nure.ua.volunteering_ua.dto.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.model.Request_Status;
import nure.ua.volunteering_ua.model.user.VolunteeringType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestGetDto {

    private long id;
    private String title;
    private String description;
    private int amount;
    private String organization;
    private String customer;
    private Request_Status requestStatus;
    private VolunteeringType volunteeringType;
    private String receivingAddress;
    private int queueNumber;

}
