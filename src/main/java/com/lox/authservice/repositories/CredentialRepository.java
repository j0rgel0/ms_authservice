package com.lox.authservice.repositories;

import com.lox.authservice.models.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<Credential, String> {

}