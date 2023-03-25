package uz.aim.zerikdim5.services.oauth2;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import uz.aim.zerikdim5.dtos.oauth2.OAuth2UserDTO;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService  {

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user =  super.loadUser(userRequest);
		
		return new OAuth2UserDTO(user);
	}

}
