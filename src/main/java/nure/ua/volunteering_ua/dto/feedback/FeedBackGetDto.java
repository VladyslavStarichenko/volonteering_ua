package nure.ua.volunteering_ua.dto.feedback;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackGetDto {

    private String comment;
    private String customerName;
    private String organizationName;
    private int rating;
}
