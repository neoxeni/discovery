package com.mercury.discovery.organization.service;

import com.mercury.discovery.organization.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentJpaRepository extends JpaRepository<Department, Integer> {
}
