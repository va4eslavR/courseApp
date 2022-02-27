package com.courseApp.services.oauth2customization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.converter.ClaimConversionService;
import org.springframework.security.oauth2.core.converter.ClaimTypeConverter;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
@Service
public class CustomOidcService extends OidcUserService {

    private final AppUserInitiator googleInitiator;
    private final GrantAdminRoles grantAdminRoles;
    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";
    private static final Converter<Map<String, Object>, Map<String, Object>> DEFAULT_CLAIM_TYPE_CONVERTER =
            new ClaimTypeConverter(createDefaultClaimTypeConverters());
    private final Set<String> accessibleScopes = new HashSet<>(Arrays.asList("profile", "email", "address", "phone"));
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = new DefaultOAuth2UserService();
    private final Function<ClientRegistration, Converter<Map<String, Object>, Map<String, Object>>> claimTypeConverterFactory =
            (clientRegistration) -> DEFAULT_CLAIM_TYPE_CONVERTER;
    @Autowired
    public CustomOidcService(GoogleInitiator googleInitiator, GrantAdminRoles grantAdminRoles) {
        this.googleInitiator = googleInitiator;
        this.grantAdminRoles = grantAdminRoles;
    }


    private static Converter<Object, ?> getConverter(TypeDescriptor targetDescriptor) {
        TypeDescriptor sourceDescriptor = TypeDescriptor.valueOf(Object.class);
        return (source) -> {
            return ClaimConversionService.getSharedInstance().convert(source, sourceDescriptor, targetDescriptor);
        };
    }

    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");
        OidcUserInfo userInfo = null;
        if (this.shouldRetrieveUserInfo(userRequest)) {
            OAuth2User oauth2User = this.oauth2UserService.loadUser(userRequest);
            Map<String, Object> claims = this.getClaims(userRequest, oauth2User);
            userInfo = new OidcUserInfo(claims);
            OAuth2Error oauth2Error;
            if (userInfo.getSubject() == null) {
                oauth2Error = new OAuth2Error("invalid_user_info_response");
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            }

            if (!userInfo.getSubject().equals(userRequest.getIdToken().getSubject())) {
                oauth2Error = new OAuth2Error("invalid_user_info_response");
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            }
        }
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        var role=grantAdminRoles.getRole(userRequest.getIdToken().getClaims());

        authorities.add(new OidcUserAuthority(role.getValue(),userRequest.getIdToken(), userInfo));
        googleInitiator.saveNewAppUser(userRequest.getIdToken().getClaims(),userRequest.getIdToken().getEmail(),role);
        return this.getUser(userRequest, userInfo, authorities);
    }

    private Map<String, Object> getClaims(OidcUserRequest userRequest, OAuth2User oauth2User) {
        Converter<Map<String, Object>, Map<String, Object>> converter =
                this.claimTypeConverterFactory.apply(userRequest.getClientRegistration());
        return converter != null ? converter.convert(oauth2User.getAttributes())
                : DEFAULT_CLAIM_TYPE_CONVERTER.convert(oauth2User.getAttributes());
    }

    private OidcUser getUser(OidcUserRequest userRequest, OidcUserInfo userInfo, Set<GrantedAuthority> authorities) {
        ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        return StringUtils.hasText(userNameAttributeName) ?
                new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName) :
                new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
    }

    private boolean shouldRetrieveUserInfo(OidcUserRequest userRequest) {
        ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        if (StringUtils.isEmpty(providerDetails.getUserInfoEndpoint().getUri())) {
            return false;
        } else if (!AuthorizationGrantType.AUTHORIZATION_CODE.equals(userRequest.getClientRegistration()
                .getAuthorizationGrantType())) {
            return false;
        } else {
            return this.accessibleScopes.isEmpty()
                    || CollectionUtils.containsAny(userRequest.getAccessToken().getScopes(), this.accessibleScopes);
        }
    }
}
