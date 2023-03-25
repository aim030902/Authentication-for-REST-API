package uz.aim.zerikdim5.services.auth;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.aim.zerikdim5.domains.entities.auth.ActivationCode;
import uz.aim.zerikdim5.domains.entities.auth.Role;
import uz.aim.zerikdim5.domains.entities.auth.User;
import uz.aim.zerikdim5.domains.enums.UserStatus;
import uz.aim.zerikdim5.dtos.auth.LoginDTO;
import uz.aim.zerikdim5.dtos.auth.RegisterDTO;
import uz.aim.zerikdim5.dtos.jwt.JwtResponseDto;
import uz.aim.zerikdim5.dtos.jwt.RefreshTokenDTO;
import uz.aim.zerikdim5.dtos.response.ApiResponse;
import uz.aim.zerikdim5.dtos.user.UserDTO;
import uz.aim.zerikdim5.exception.GenericInvalidTokenException;
import uz.aim.zerikdim5.exception.GenericNotFoundException;
import uz.aim.zerikdim5.exception.GenericRuntimeException;
import uz.aim.zerikdim5.mapper.UserMapper;
import uz.aim.zerikdim5.repository.RoleRepository;
import uz.aim.zerikdim5.repository.UserRepository;
import uz.aim.zerikdim5.services.mail.MailService;
import uz.aim.zerikdim5.services.jwt.TokenService;
import uz.aim.zerikdim5.validation.auth.AuthValidation;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class AuthService implements UserDetailsService {
    @Value("${activation.link.base.path}")
    private String basePath;
    @Autowired
    private AuthValidation authValidation;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ActivationCodeService activationCodeService;
    @Autowired
    private MailService mailService;
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;
    @Autowired
    @Qualifier("accessTokenService")
    private TokenService accessTokenService;
    @Autowired
    @Qualifier("refreshTokenService")
    private TokenService refreshTokenService;

    @SneakyThrows
    @Transactional
    public ApiResponse<User> register(@NonNull RegisterDTO dto) {
        authValidation.validateOnRegister(dto);
        User createUser = UserMapper.toEntityFromRegisterDTO(dto);
        Role roleUser = roleRepository.findByName("USER");
        createUser.setPassword(passwordEncoder.encode(createUser.getPassword()));
        createUser.setRoles(Set.of(roleUser));
        User savedUser = userRepository.save(createUser);
        UserDTO userDTO = UserMapper.toUserDTOFromEntity(savedUser);
        ActivationCode activationCode = activationCodeService.generateCode(userDTO);
        String link = basePath.formatted(activationCode.getActivationLink());
        mailService.sendEmail(userDTO, link);
        return new ApiResponse<>("User successfully registered, please confirm email!", savedUser, true);
    }

    @Transactional(noRollbackFor = GenericRuntimeException.class)
    public Boolean activateUser(String activationCode) {
        ActivationCode activationLink = activationCodeService.findByActivationLink(activationCode);
        if (activationLink.getValidTill().isBefore(LocalDateTime.now())) {
            activationCodeService.delete(activationLink.getId());
            throw new GenericRuntimeException("Activation Code is not active");
        }
        User authUser = userRepository.findById(activationLink.getUserId()).orElseThrow(() -> {
            throw new GenericNotFoundException("User not found");
        });

        authUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(authUser);
        return true;
    }

    public JwtResponseDto login(LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = accessTokenService.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateToken(userDetails);
        return new JwtResponseDto(accessToken, refreshToken, "Bearer");
    }

    public JwtResponseDto refreshToken(@NonNull RefreshTokenDTO dto) {
        String token = dto.token();
        if (!accessTokenService.isValid(token)) {
            throw new GenericInvalidTokenException("Refresh Token invalid");
        }
        String subject = refreshTokenService.getSubject(token);
        UserDetails userDetails = loadUserByUsername(subject);
        String accessToken = accessTokenService.generateToken(userDetails);
        return new JwtResponseDto(accessToken, dto.token(), "Bearer");
    }

    public User getCurrentAuthUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username).orElseThrow(() -> {
            throw new GenericNotFoundException("User not found!");
        });
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> exception = () ->
                new UsernameNotFoundException("Bad credentials");
        User authUser = userRepository.findByEmail(email).orElseThrow(exception);
        return new uz.aim.zerikdim5.configs.security.UserDetails(authUser);
    }
}
