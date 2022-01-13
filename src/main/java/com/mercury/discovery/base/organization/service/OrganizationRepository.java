package com.mercury.discovery.base.organization.service;

import com.mercury.discovery.base.organization.model.ChangeDepartmentDto;
import com.mercury.discovery.base.organization.model.Department;
import com.mercury.discovery.base.organization.model.OrganizationSearchDto;
import com.mercury.discovery.base.users.model.UserAppRole;
import com.mercury.discovery.base.users.model.UserRole;
import com.mercury.discovery.common.model.CamelMap;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Mapper
@Repository
public interface OrganizationRepository {

    List<CamelMap> findDepartmentAll(int clientId);

    List<CamelMap> findEmployeeAll(int clientId);

    int insertDepartment(Department department);

    int updateDepartment(Department department);

    List<CamelMap> findDeptEmpListForTree(OrganizationSearchDto organizationSearchDto);

    List<CamelMap> findDeptEmpListForTreeSearch(OrganizationSearchDto organizationSearchDto);

    Department findDepartment(Integer clientId, Integer deptNo);

    int changeEmployeeDepartment(ChangeDepartmentDto changeDepartmentDto);

    int changeDepartmentDepartment(ChangeDepartmentDto changeDepartmentDto);

    List<UserRole> findDepartmentsRoles(Integer clientId, Long id);

    Set<UserAppRole> findDepartmentsAppRoles(Integer clientId, Long deptNo);
}
