package nure.ua.volunteering_ua.dto.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.model.Request_Status;
import nure.ua.volunteering_ua.model.user.Volunteering_Type;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private Long id;
    private String title;
    private String description;
    private int amount;
    private String organization;
    private String customer;
    private Request_Status requestStatus;
    private Volunteering_Type volunteering_type;
    private String receivingAddress;
    private int queueNumber;

}
