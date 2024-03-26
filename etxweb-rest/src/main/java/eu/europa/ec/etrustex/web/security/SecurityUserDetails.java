package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.etrustex.web.exchange.model.UserDetails;
import eu.europa.ec.etrustex.web.exchange.model.UserPreferencesSpec;
import eu.europa.ec.etrustex.web.exchange.model.UserRole;
import eu.europa.ec.etrustex.web.persistence.entity.UserPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@Hidden
public class SecurityUserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private User user;
    @ToString.Exclude
    private Set<Role> roles;
    @ToString.Exclude
    private Collection<SecurityGrantedAuthority> authorities;
    private String firstName;
    private String lastName;
    private String email;

    @Setter(AccessLevel.NONE)
    private boolean isSystemAdmin;


    @Override
    public Collection<SecurityGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<SecurityGrantedAuthority> securityGrantedAuthorities) {
        this.authorities = securityGrantedAuthorities;
        this.isSystemAdmin = authorities.stream().anyMatch(SecurityGrantedAuthority::isSystemAdmin);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.user.getEcasId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public boolean hasAuthority(String role) {
        return authorities.stream()
                .anyMatch(authority -> Objects.equals(authority.getRole().getName(), RoleName.valueOf(role)));

    }

    public boolean hasAuthority(String role, Long... groupIds) {
        return isSystemAdmin
                || new HashSet<>(authorities.stream()
                .filter(authority -> Objects.equals(authority.getRole().getName(), RoleName.valueOf(role)))
                .map(authority -> authority.getGroup().getId())
                .collect(Collectors.toList()))
                .containsAll(Arrays.asList(groupIds));
    }

    public boolean hasAnyAuthority(String role, Long... groupIds) {
        List<Long> groups = Arrays.asList(groupIds);
        return isSystemAdmin || authorities.stream()
                .anyMatch(authority -> Objects.equals(authority.getRole().getName(), RoleName.valueOf(role))
                        && groups.contains(authority.getGroup().getId()));
    }

    public boolean isOfficialInCharge() {
        return authorities.stream().anyMatch(authority -> Objects.equals(authority.getRole().getName(), RoleName.OFFICIAL_IN_CHARGE));
    }

    public boolean isEntityOrBusinessAdmin(Long... groupIds) {
        return isSystemAdmin
                || new HashSet<>(authorities.stream()
                .filter(authority -> Objects.equals(authority.getRole().getName(), RoleName.GROUP_ADMIN))
                .map(authority -> authority.getGroup().getBusinessId())
                .collect(Collectors.toList()))
                .containsAll(Arrays.asList(groupIds));
    }

    public boolean belongsToEntity(Long businessId) {
        return authorities.stream()
                .anyMatch(authority -> Objects.equals(authority.getGroup().getType(), GroupType.ENTITY) && Objects.equals(authority.getGroup().getId(), businessId));
    }

    public boolean belongsToBusiness(Long businessId) {
        return authorities.stream()
                .anyMatch(authority -> Objects.equals(authority.getGroup().getBusinessId(), businessId));
    }

    public UserDetails toExchangeModel() {
        return UserDetails.builder()
                .id(user.getId())
                .username(getUsername())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userPreferencesSpec(toUserPreferencesSpec(user.getUserPreferences()))
                .roles(getAuthorities().stream()
                        .map(securityGrantedAuthority -> UserRole.builder()
                                .groupId(securityGrantedAuthority.getGroup().getId())
                                .businessId(securityGrantedAuthority.getGroup().getBusinessId())
                                .groupName(securityGrantedAuthority.getGroup().getName())
                                .groupDescription(securityGrantedAuthority.getGroup().getDescription())
                                .groupType(securityGrantedAuthority.getGroup().getType())
                                .role(securityGrantedAuthority.getRole().getName())
                                .groupIsActive(securityGrantedAuthority.getGroup().isActive())
                                .build()
                        )
                        .toArray(UserRole[]::new))
                .build();
    }

    private UserPreferencesSpec toUserPreferencesSpec(UserPreferences userPreferences) {
        return UserPreferencesSpec.builder()
                .paginationSize(userPreferences.getPaginationSize())
                .build();
    }
}
