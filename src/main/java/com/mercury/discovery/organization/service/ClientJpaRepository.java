package com.mercury.discovery.organization.service;

import com.mercury.discovery.organization.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientJpaRepository extends JpaRepository<Client, Integer> {
}
