package com.ctrl_alt_elite.proxy_user_bank_application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ctrl_alt_elite.proxy_user_bank_application.entity.ERole;
import com.ctrl_alt_elite.proxy_user_bank_application.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
