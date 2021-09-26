package com.Yoo.security1.config.oauth.provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

	private Map<String,Object> attributes;  // oauth2User.getAttribues() 사용자정보

	// {id=BWhRe4lRMORenDyHVucNY3PbN4D3nKoS6kGjdITwfiY, email=kljh1100@naver.com, name=유태규}
	public NaverUserInfo(Map<String,Object> attributes){
		this.attributes = attributes;
	}
	
	@Override
	public String getProviderId() {
		return (String) attributes.get("id");
	}

	@Override
	public String getProvider() {
		return "naver";
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	
}
