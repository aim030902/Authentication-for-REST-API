package uz.aim.zerikdim5.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.aim.zerikdim5.domains.entities.auth.Role;
import uz.aim.zerikdim5.domains.entities.auth.User;
import uz.aim.zerikdim5.domains.enums.Permission;
import uz.aim.zerikdim5.domains.enums.RoleCode;
import uz.aim.zerikdim5.domains.enums.UserStatus;
import uz.aim.zerikdim5.mapper.UserMapper;
import uz.aim.zerikdim5.repository.RoleRepository;
import uz.aim.zerikdim5.repository.UserRepository;
import uz.aim.zerikdim5.services.auth.AuthService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @Value("${spring.sql.init.mode}")
    private String initialMode;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")) {
            Role roleAdmin = Role
                    .builder()
                    .name("ADMIN")
                    .code(RoleCode.ROLE_ADMIN)
                    .permissions(Set.of(Permission.values()))
                    .build();
            Role roleUser = Role
                    .builder()
                    .name("USER")
                    .code(RoleCode.ROLE_USER)
                    .permissions(Set.of(Permission.values()))
                    .build();

            User admin = User
                    .builder()
                    .email("aim030902@gmail.com")
                    .fullName("AIM")
                    .password(passwordEncoder.encode("030902"))
                    .roles(Set.of(roleAdmin, roleUser))
                    .status(UserStatus.ACTIVE)
                    .build();


//            User user = User
//                    .builder()
//                    .fullName("User")
//                    .username("user")
//                    .password(passwordEncoder.encode("123"))
//                    .email("user@gmail.com")
//                    .roles(Set.of(roleUser))
//                    .build();

            roleRepository.saveAll(new ArrayList<>(List.of(roleAdmin, roleUser)));
            userRepository.save(admin);
        }
    }
}
