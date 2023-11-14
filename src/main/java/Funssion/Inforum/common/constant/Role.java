package Funssion.Inforum.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER ("ROLE_USER"),
    EMPLOYER("ROLE_EMPLOYER,ROLE_USER"),
    ADMIN ("ROLE_ADMIN,ROLE_EMPLOYER,ROLE_USER"),
    TEMP_EMPLOYER("ROLE_TEMPORARY_EMPLOYER"),
    TEMP_USER ("ROLE_TEMPORARY_USER"),
    EXCEPTION("exception"),
    OAUTH_FIRST_JOIN ("ROLE_FIRST_JOIN_OAUTH_USER");

    private final String roles;

    public boolean isEqualTo(Collection<? extends GrantedAuthority> authorities) {
        return new HashSet<>(authorities.stream().map(Objects::toString).toList())
                .containsAll(Arrays.stream(this.roles.split(",")).toList());
    }

    public static String getIncludingRoles(String role){
        return Role.valueOf(role).getRoles();
    }
    public static String addRole(Role role, String addRole){
        String priorRoles = role.getRoles();
        priorRoles += ","+addRole;
        return priorRoles;
    }
    public static String addRole(String roles, Role role){
        return roles + "," + role.getRoles();

    }
}
