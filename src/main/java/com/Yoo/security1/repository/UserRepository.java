package com.Yoo.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Yoo.security1.model.User;

// CRUD 함수를 JpaRepository가 들고 있다.
// @Repository 라는 어노테이션이 없어도 ioc 가 된다 이유는 JpaRepository를 상속하기 때문..  >> 자동으로 bean에 등록
public interface UserRepository extends JpaRepository<User, Integer>{
	
	//fingBy 는 규칙 -> Username 문법
	//select * from user where username = 1?(파라미터)   쿼리가 발생.  
	public User findByUsername(String username);	// JPA 쿼리 메서드  검색  JPA Query Methods

	
}
