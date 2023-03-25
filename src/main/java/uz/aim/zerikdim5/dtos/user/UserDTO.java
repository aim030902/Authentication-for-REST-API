package uz.aim.zerikdim5.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import uz.aim.zerikdim5.domains.enums.UserStatus;

@AllArgsConstructor
@Getter
@Builder
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private UserStatus status;
}
