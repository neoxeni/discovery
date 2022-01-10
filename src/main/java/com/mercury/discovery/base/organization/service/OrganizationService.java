package com.mercury.discovery.base.organization.service;

import com.mercury.discovery.base.organization.model.ChangeDepartmentDto;
import com.mercury.discovery.base.organization.model.Department;
import com.mercury.discovery.base.organization.model.OrganizationSearchDto;
import com.mercury.discovery.base.users.model.UserRole;
import com.mercury.discovery.common.model.CamelMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;


    @Transactional(readOnly = true)
    public List<CamelMap> findDeptEmpListForTree(OrganizationSearchDto organizationSearchDto) {
        return organizationRepository.findDeptEmpListForTree(organizationSearchDto);
    }

    @Transactional(readOnly = true)
    public List<CamelMap> findDeptEmpListForTreeSearch(OrganizationSearchDto organizationSearchDto) {
        return organizationRepository.findDeptEmpListForTreeSearch(organizationSearchDto);
    }

    @Transactional(readOnly = true)
    public List<CamelMap> findEmpListAll(int cmpnyNo) {
        List<CamelMap> employeeList = organizationRepository.findEmployeeAll(cmpnyNo);
        for (CamelMap employee : employeeList) {
            int empNo = employee.getInt("empNo");
            employee.put("type", "E");
            employee.put("no", empNo);
            employee.put("id", "E" + empNo);
            employee.put("name", employee.getString("empNm"));
        }

        return employeeList;
    }

    @Transactional(readOnly = true)
    public List<CamelMap> findDeptListAll(int cmpnyNo) {
        List<CamelMap> departmentList = organizationRepository.findDepartmentAll(cmpnyNo);
        for (CamelMap dept : departmentList) {
            int deptNo = dept.getInt("deptNo");
            dept.put("type", "D");
            dept.put("no", deptNo);
            dept.put("id", "D" + deptNo);
            dept.put("name", dept.getString("deptNm"));
        }
        return departmentList;
    }


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "deptEmpListForTreeAll", key = "#cmpnyNo.toString()")
    public CamelMap findDeptEmpListForTreeAll(Integer cmpnyNo) {
        CamelMap organization = new CamelMap();


        Map<Integer, List<CamelMap>> employeeByDeptCdMap = new HashMap<>();//<empNo, 직원>
        List<CamelMap> employeeList = organizationRepository.findEmployeeAll(cmpnyNo);
        for (CamelMap employee : employeeList) {
            int empNo = employee.getInt("empNo");
            employee.put("type", "E");
            employee.put("no", empNo);
            employee.put("id", "E" + empNo);
            employee.put("name", employee.getString("empNm"));

            List<CamelMap> childrenList = employeeByDeptCdMap.computeIfAbsent(employee.getInt("deptNo", -1), k -> new ArrayList<>());
            childrenList.add(employee);
        }

        Map<Integer, List<CamelMap>> departmentMap = new HashMap<>();//<pDeptNo, 부서>
        List<CamelMap> departmentList = organizationRepository.findDepartmentAll(cmpnyNo);
        for (CamelMap dept : departmentList) {
            int parentDeptNo = dept.getInt("PDeptNo");
            int deptNo = dept.getInt("deptNo");
            dept.put("type", "D");
            dept.put("no", deptNo);
            dept.put("id", "D" + deptNo);
            dept.put("name", dept.getString("deptNm"));

            if (dept.getInt("PDeptNo") == 0) {
                organization = dept;
            }

            List<CamelMap> childrenList = departmentMap.computeIfAbsent(parentDeptNo, k -> new ArrayList<>());
            childrenList.add(dept);
        }

        List<CamelMap> menuList = departmentMap.get(organization.getInt("deptNo"));
        if (menuList != null) {
            organization.put("children", menuList);
            for (CamelMap menu : menuList) {
                makeMenuTree(departmentMap, menu, 1, employeeByDeptCdMap);
            }
        }

        //부서가 없거나 분류되지 못한 사람은 전부 루트에 추가
        List<CamelMap> orgChildren = (List<CamelMap>) organization.get("children");
        if(orgChildren == null){
            orgChildren = new ArrayList<>();
            organization.put("children", orgChildren);
        }
        for (Map.Entry<Integer, List<CamelMap>> elem : employeeByDeptCdMap.entrySet()) {
            orgChildren.addAll(elem.getValue());
        }
        

        return organization;
    }

    private void makeMenuTree(Map<Integer, List<CamelMap>> childrenDepartmentsMap, CamelMap department, int depth, Map<Integer, List<CamelMap>> employeeByDeptCdMap) {
        Integer deptNo = department.getInt("deptNo", null);
        List<CamelMap> employeeList = employeeByDeptCdMap.remove(deptNo);
        List<CamelMap> childrenDepartments = childrenDepartmentsMap.get(deptNo);
        if (childrenDepartments != null) {
            //부서가 있고
            department.put("children", childrenDepartments);
            for (CamelMap child : childrenDepartments) {
                makeMenuTree(childrenDepartmentsMap, child, depth + 1, employeeByDeptCdMap);
            }

            //직원만 있는 경우
            if (employeeList != null) {
                childrenDepartments.addAll(employeeList);
            }
        } else {
            //부서는 없고 직원만 있는 경우
            if (employeeList != null) {
                department.put("children", employeeList);
            }
        }
    }

    @Transactional(readOnly = true)
    public Department findDepartment(Integer cmpnyNo, Integer deptNo) {
        return organizationRepository.findDepartment(cmpnyNo, deptNo);
    }

    @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#department.cmpnyNo.toString()")
    public int insertDepartment(Department department) {
        return organizationRepository.insertDepartment(department);
    }

    @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#department.cmpnyNo.toString()")
    public int updateDepartment(Department department) {
        return organizationRepository.updateDepartment(department);
    }

    @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#changeDepartmentDto.cmpnyNo.toString()")
    public int changeEmployeeDepartment(ChangeDepartmentDto changeDepartmentDto) {
        return organizationRepository.changeEmployeeDepartment(changeDepartmentDto);
    }

    @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#changeDepartmentDto.cmpnyNo.toString()")
    public int changeDepartmentDepartment(ChangeDepartmentDto changeDepartmentDto) {
        return organizationRepository.changeDepartmentDepartment(changeDepartmentDto);
    }

    @Transactional(readOnly = true)
    public List<UserRole> findDepartmentsRoles(Integer cmpnyNo, Integer deptNo) {
        return organizationRepository.findDepartmentsRoles(cmpnyNo, deptNo);
    }

    @Transactional(readOnly = true)
    public List<CamelMap> findDepartmentsTree(Integer cmpnyNo) {
        return organizationRepository.findDepartmentsTree(cmpnyNo);
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#cmpnyNo")
    })
    public boolean clearCache(String cmpnyNo) {
        log.debug("clearCache {}", cmpnyNo);
        return true;
    }

    @Transactional(readOnly = true)
    public List<CamelMap> findDepartmentByParent(int cmpnyNo, String useYn, int pDeptNo) {
        List<CamelMap> departmentList = organizationRepository.findDepartmentByParent(cmpnyNo, useYn, pDeptNo);
        for (CamelMap dept : departmentList) {
            int deptNo = dept.getInt("deptNo");
            dept.put("type", "D");
            dept.put("no", deptNo);
            dept.put("id", "D" + deptNo);
            dept.put("name", dept.getString("deptNm"));
        }

        return departmentList;
    }

    @Transactional(readOnly = true)
    public List<CamelMap> findEmployeeByDepartment(int cmpnyNo, String useYn, String rtrmntYn, String rootYn, int deptNo) {
        List<CamelMap> employeeList = organizationRepository.findEmployeeByDepartment(cmpnyNo, useYn, rtrmntYn, rootYn, deptNo);
        for (CamelMap employee : employeeList) {
            int empNo = employee.getInt("empNo");
            employee.put("type", "E");
            employee.put("no", empNo);
            employee.put("id", "E" + empNo);
            employee.put("name", employee.getString("empNm"));
        }

        return employeeList;
    }
}
