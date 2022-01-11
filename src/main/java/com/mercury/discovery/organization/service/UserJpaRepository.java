package com.mercury.discovery.organization.service;

import com.mercury.discovery.organization.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Integer> {
}
