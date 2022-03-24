package com.courseApp.services.oauth2customization;

import com.courseApp.models.repositories.RoleRepo;
import com.courseApp.utility.GrantAdminRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };
    private final Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new OAuth2UserRequestEntityConverter();
    private final RestOperations restOperations;
    private final AppUserInitiator githubInitiator;
    private final RoleRepo roleRepo;

    @Autowired
    private final GrantAdminRoles grantAdminRoles;
    public CustomOAuth2UserService(GithubInitiator githubInitiator, RoleRepo roleRepo, GrantAdminRoles grantAdminRoles) {
        this.githubInitiator = githubInitiator;
        this.roleRepo = roleRepo;
        this.grantAdminRoles = grantAdminRoles;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        Assert.notNull(userRequest, "userRequest cannot be null");
        if (!StringUtils.hasText(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())) {
            OAuth2Error oauth2Error =
                    new OAuth2Error("missing_user_info_uri",
                            "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                                    + userRequest.getClientRegistration().getRegistrationId(), null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        } else {
            String userNameAttributeName = userRequest.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
            userRequest.getAdditionalParameters().forEach((s, o) -> System.out.println("Additional param "+s+", "+o.toString()));

            if (!StringUtils.hasText(userNameAttributeName)) {
                OAuth2Error oauth2Error = new OAuth2Error("missing_user_name_attribute",
                        "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                                + userRequest.getClientRegistration().getRegistrationId(), null);
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            } else {
                RequestEntity<?> request = requestEntityConverter.convert(userRequest);
                ResponseEntity<Map<String, Object>> response = getResponse(userRequest, request);
                Map<String, Object> userAttributes = response.getBody();
                Set<GrantedAuthority> authorities = new LinkedHashSet<>();
                assert userAttributes != null;
                var role=grantAdminRoles.getRole(userAttributes);
                var roleObject=roleRepo.findByRole(role).orElseThrow();
                authorities.add(new OAuth2UserAuthority(role.getValue(),userAttributes));
                githubInitiator.saveNewAppUser(userAttributes,userNameAttributeName,roleObject);
               return new DefaultOAuth2User(authorities, userAttributes, userNameAttributeName);
            }
        }
    }

    private ResponseEntity<Map<String, Object>> getResponse(OAuth2UserRequest userRequest, RequestEntity<?> request) {
        OAuth2Error oauth2Error;
        try {
            return this.restOperations.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
        } catch (OAuth2AuthorizationException var6) {
            oauth2Error = var6.getError();
            StringBuilder errorDetails = new StringBuilder();
            errorDetails.append("Error details: [");
            errorDetails.append("UserInfo Uri: ").append(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
            errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
            if (oauth2Error.getDescription() != null) {
                errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
            }
            errorDetails.append("]");
            oauth2Error = new OAuth2Error("invalid_user_info_response",
                    "An error occurred while attempting to retrieve the UserInfo Resource: "
                            + errorDetails, null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), var6);
        } catch (UnknownContentTypeException var7) {
            String errorMessage = "An error occurred while attempting to retrieve the UserInfo Resource from '"
                    + userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri() +
                    "': response contains invalid content type '" + var7.getContentType() +
                    "'. The UserInfo Response should return a JSON object (content type 'application/json') that contains a collection of name and value pairs of the claims about the authenticated End-User. Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '" + userRequest.getClientRegistration().getRegistrationId() + "' conforms to the UserInfo Endpoint, as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'";
            OAuth2Error oauth2Errors = new OAuth2Error("invalid_user_info_response", errorMessage, null);
            throw new OAuth2AuthenticationException(oauth2Errors, oauth2Errors.toString(), var7);
        } catch (RestClientException var8) {
            oauth2Error = new OAuth2Error("invalid_user_info_response", "An error occurred while attempting to retrieve the UserInfo Resource: " + var8.getMessage(), null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), var8);
        }
    }
}

