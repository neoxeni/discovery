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

    List<CamelMap> findDeptEmpListForTree(OrganizationSearchDto organizationSearchDto);

    List<CamelMap> findDeptEmpListForTreeSearch(OrganizationSearchDto organizationSearchDto);

    List<CamelMap> findDepartmentAll(int cmpnyNo);

    List<CamelMap> findEmployeeAll(int cmpnyNo);

    Department findDepartment(Integer cmpnyNo, Integer deptNo);

    int insertDepartment(Department department);

    int updateDepartment(Department department);

    int changeEmployeeDepartment(ChangeDepartmentDto changeDepartmentDto);

    int changeDepartmentDepartment(ChangeDepartmentDto changeDepartmentDto);

    List<UserRole> findDepartmentsRoles(Integer cmpnyNo, Integer deptNo);

    Set<UserAppRole> findDepartmentsAppRoles(Integer cmpnyNo, Integer deptNo);

    List<CamelMap> findDepartmentsTree(Integer cmpnyNo);

    List<CamelMap> findDepartmentByParent(int cmpnyNo, String useYn, int pDeptNo);

    List<CamelMap> findEmployeeByDepartment(int cmpnyNo, String useYn, String rtrmntYn, String rootYn, int deptNo);
}
