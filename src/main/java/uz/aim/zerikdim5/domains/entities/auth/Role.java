package uz.aim.zerikdim5.domains.entities.auth;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import uz.aim.zerikdim5.domains.enums.Permission;
import uz.aim.zerikdim5.domains.enums.RoleCode;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleCode code;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions;
    private boolean deleted;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name;
    }
}
