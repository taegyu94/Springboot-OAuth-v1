package com.Yoo.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Yoo.security1.model.User;
import com.Yoo.security1.repository.UserRepository;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername 함수가 실행 (규칙) 

@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	// 시큐리티 session = Authentication = UserDetails(PrincipalDetails)
	// 시큐리티 session = Authentication(내부UserDetails(PrincipalDetails))
	// 시큐리티 session(내부Authentication(내부UserDetails(PrincipalDetails)))  !!!
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUsername(username);	// UserRepository 는 기본적인 CRUD만 들고 있기때문에 findByUsername() 함수는 없다 따라서 작성해야한다.
		
		if(userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		return null;
	}

}
