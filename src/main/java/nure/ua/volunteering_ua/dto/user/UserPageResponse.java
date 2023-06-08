package nure.ua.volunteering_ua.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPageResponse {
    List<UserGetDto> users;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
