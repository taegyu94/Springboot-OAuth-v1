package com.Yoo.security1.config;

//1. 코드받기(인증), 2. 엑세스토근(권한), 3. 사용자 프로필정보를 가져오기. 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함
//4-2(이메일, 전화번호, 이름, 아이디) 쇼핑몰  -> (집주소), 백화점몰 -> (vip등급, 일반등급) 추가적인 정보가 필요한 경우가 있다면 추가정보를 작성하는 페이지가 필요

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.Yoo.security1.config.oauth.PrincipalOauth2UserService;

@Configuration	//메모리에 띄우기 위해
@EnableWebSecurity		//활성화  ,  스프링시큐리티 필터가 스프링 필터체인에 등록이 된다. 필터가 권한이 있는 요청들이 서버에 들어올수 있도록 하는 문지기역할
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)		//secured 어노테이션 활성화  prePostEnabled >  preAuthorize, postAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	// 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
		.antMatchers("/user/**").authenticated()	// /user/** 주소로 오는 요청은 모두 인증을 받아야한다.
		.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")	// /manager/** 주소로 오는 요청은 ROLE_ADMIN or ROLE_MANAGER 의 권한을 가지고 있어야한다.
		.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")	// /admin/** 주소로 오는 요청은 ROLE_ADMIN 권한을 가지고 있어야한다.
		.anyRequest().permitAll()	//그외의 요청은 모두 허용.
		.and()		// 권한이 없는 페이지로 이동하는 경우 login 페이지로 이동  -  localhost:8080/user 로 접근 시 localhost:8080/user/login 으로 자동 이동
		.formLogin()
		.loginPage("/loginForm")
		.loginProcessingUrl("/login")	//  /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
		.defaultSuccessUrl("/")
		.and()
		.oauth2Login()
		.loginPage("/loginForm")	//구글 로그인이 완료된 뒤의 후처리가 필요함.  oaouth client 라이브러리를 쓰면. Tip. 코드X, (엑세스토큰+사용자프로필정보 O)
		.userInfoEndpoint()
		.userService(principalOauth2UserService);
	}
}
