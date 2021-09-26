package com.Yoo.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.Yoo.security1.config.auth.PrincipalDetails;
import com.Yoo.security1.config.oauth.provider.GoogleUserInfo;
import com.Yoo.security1.config.oauth.provider.NaverUserInfo;
import com.Yoo.security1.config.oauth.provider.OAuth2UserInfo;
import com.Yoo.security1.model.User;
import com.Yoo.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	// loadUser함수에서 후처리가 실행됨 
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override	
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("userRequest : " + userRequest);
		System.out.println("getClientRegistration : " + userRequest.getClientRegistration());	// 기본정보 registrationId로 어떤 OAuth로 로그인 했는지 알 수 있다.
		System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());	//엑세스 토큰
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		// 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴받는다(OAuth-Client 라이브러리가) -> AccessToken 요청
		// userRequest 정보 -> 회원프로필 받아야함(loadUser함수가 호출) -> 회원프로필을받는다(구글로부터) 
		System.out.println("getAttribute : " + oauth2User.getAttributes());	//사용자정보
		
		
		OAuth2UserInfo oAuth2UserInfo = null;

		if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));	// 네이버에서 response 안에 	response={id=BWhRe4lRMORenDyHVucNY3PbN4D3nKoS6kGjdITwfiY, email=kljh1100@naver.com, name=유태규} 와 같이 데이터를 보내준다.
		}else{
			System.out.println("우리는 구글과 네이버만 지원해요");
		}
		
		
		
		// 강제 회원가입 진행.
		String provider = oAuth2UserInfo.getProvider(); // google
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider+"_"+providerId;	//google_1021934128358
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}
		
		return new PrincipalDetails(userEntity,oauth2User.getAttributes()); // PrincipalDeatils 가 OAuth2User 를 상속하고 있기 때문에 가능. 리턴시 Authentication객체에 들어간다.
		//return super.loadUser(userRequest);
	}
}
