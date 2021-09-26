package com.Yoo.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Yoo.security1.config.auth.PrincipalDetails;
import com.Yoo.security1.model.User;
import com.Yoo.security1.repository.UserRepository;

@Controller	// View를 리턴하겠다.
public class IndexController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
			Authentication authentication,
			@AuthenticationPrincipal PrincipalDetails userDetails) {	//DI(의존성 주입)   @AuthenticationPrincipal 어노테이션을 통해 세션정보에 접근할 수 있다.
		System.out.println("/test/login ==========");
		
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();	//1번방법  구글로그인시 오류남
		System.out.println("authentication : " + principalDetails.getUser());
		
		System.out.println("userDetails : " + userDetails.getUser());	//2번방법
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) {	//DI(의존성 주입)   @AuthenticationPrincipal 어노테이션을 통해 세션정보에 접근할 수 있다.
		System.out.println("/test/oauth/login ==========");
		
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();	// 구글 로그인정보를 가져오려면 (OAuth2User) 형으로 다운캐스트를 해야 가져올 수 있다.
		System.out.println("authentication : " + oauth2User.getAttributes());
		System.out.println("oauth2User : "+oauth.getAttributes());
		return "OAuth 세션 정보 확인하기";
	}

	// locallhost:8080/
	// localhost:8080
	@GetMapping({"" , "/"})
	public String index() {
		//	머스테치 스프링이 권장하는 템플릿 >> 기본폴더 src/main/resources/
		//	뷰리졸버 설정 : 	templates (prefix), .mustache (suffix) >>  의존성 설정에 mustache 를 했다면 생략 가능하다.
		return "index";	//	src/main/resources/templates/index.mustache 를 찾게 된다. 
	}
	
	//OAuth 로그인을 해도 PrincipalDetails 
	// 일반 로그인을 해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : " + principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	//	스프링시큐리티가 해당주소를 낚아채 시큐리티 로그인 페이지로 넘어간다. - SecurityConfig 파일 생성 후 작동안함.
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);	// 회원가입이 잘됨.  단 비밀번호 : 1234  =>  시큐리티로 로그인을 할 수 없다. 이유는 패스워드가 암호화가 안되어있기 때문.
		return "redirect:/loginForm";	// redirect: 을 붙이면, /loginForm 를 호출
	}
	
	@Secured("ROLE_ADMIN")	//권한을 하나만 설정하고 싶을때, 간단하게 Secured 사용
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")	//data함수가 실행되기 전에,  권한을 여러가지 설정하고 싶을때,  hasRole 사용
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
	
	
	
}
