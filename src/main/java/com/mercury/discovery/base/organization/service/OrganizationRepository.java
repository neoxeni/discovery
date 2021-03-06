package com.mercury.discovery.base.organization.service;

import com.mercury.discovery.base.organization.model.ChangeDepartmentDto;
import com.mercury.discovery.base.organization.model.Client;
import com.mercury.discovery.base.organization.model.Department;
import com.mercury.discovery.base.users.model.UserGroup;
import com.mercury.discovery.common.model.CamelMap;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrganizationRepository {

    List<CamelMap> findDepartmentAll(int clientId);

    List<CamelMap> findEmployeeAll(int clientId);

    int insertDepartment(Department department);

    int updateDepartment(Department department);

    Department findDepartment(Integer clientId, Long id);

    int changeEmployeeDepartment(ChangeDepartmentDto changeDepartmentDto);

    int changeDepartmentDepartment(ChangeDepartmentDto changeDepartmentDto);

    List<UserGroup> findDepartmentGroups(Integer clientId, Long departmentId);

    Department findDepartmentByDepartmentKey(Integer clientId, String departmentKey);

    Client findClientByClientKey(Integer clientId, String clientKey);

    Client findClientById(Integer clientId);
}
