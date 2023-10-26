package Funssion.Inforum.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER ("ROLE_USER"),
    EMPLOYER("ROLE_EMPLOYER,ROLE_USER"),
    ADMIN ("ROLE_ADMIN,ROLE_EMPLOYER,ROLE_USER"),
    TEMP_EMPLOYER("ROLE_TEMPORARY_EMPLOYER"),
    TEMP_USER ("ROLE_TEMPORARY_USER"),
    OAUTH_FIRST_JOIN ("ROLE_FIRST_JOIN_OAUTH_USER");

    private final String roles;
    public static String getIncludingRoles(String role){
        return Role.valueOf(role).getRoles();
    }
    public static String addRole(Role role, String addRole){
        String priorRoles = role.getRoles();
        priorRoles += ","+addRole;
        return priorRoles;
    }
    public static String addRole(String roles, String addRole){
        roles += ","+addRole;
        return roles;
    }
}
