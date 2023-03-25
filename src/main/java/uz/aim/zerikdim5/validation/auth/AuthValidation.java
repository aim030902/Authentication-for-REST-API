package uz.aim.zerikdim5.validation.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.aim.zerikdim5.dtos.auth.RegisterDTO;
import uz.aim.zerikdim5.exception.ConflictException;
import uz.aim.zerikdim5.repository.UserRepository;

@Component
public class AuthValidation {
    @Autowired
    UserRepository userRepository;
    public void validateOnRegister(RegisterDTO dto) {
        boolean isValid = userRepository.existsByEmail(dto.email());
        if (isValid) {
            throw new ConflictException("This is email already exists");
        }
    }
}
