package com.lox.authservice.repositories;

import com.lox.authservice.models.Authtoken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthtokenRepository extends JpaRepository<Authtoken, String> {

}
