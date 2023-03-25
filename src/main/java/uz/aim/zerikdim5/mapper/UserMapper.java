package uz.aim.zerikdim5.mapper;

import uz.aim.zerikdim5.domains.entities.auth.User;
import uz.aim.zerikdim5.dtos.auth.RegisterDTO;
import uz.aim.zerikdim5.dtos.user.UserDTO;

import java.util.Set;

public interface UserMapper {
    static User toEntityFromRegisterDTO(RegisterDTO dto) {
        return User
                .builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .password(dto.password())
                .build();
    }

    static RegisterDTO toRegisterDTOFromEntity(User user) {
        return new RegisterDTO(user.getEmail(), user.getFullName(), user.getPassword());
    }

    static UserDTO toUserDTOFromEntity(User entity) {
        return UserDTO
                .builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .build();
    }
}
