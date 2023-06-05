package nure.ua.volunteering_ua.dto.feedback;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackCreateDto {

    private String organizationName;
    private String comment;
    private int rating;
}
