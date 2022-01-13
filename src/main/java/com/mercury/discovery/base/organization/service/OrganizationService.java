package com.mercury.discovery.base.organization.service;

import com.mercury.discovery.base.organization.model.ChangeDepartmentDto;
import com.mercury.discovery.base.organization.model.Department;
import com.mercury.discovery.base.users.model.UserGroup;
import com.mercury.discovery.common.model.CamelMap;
import com.mercury.discovery.common.model.JsTree;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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

    public List<JsTree> findAllForTree(Integer clientId) {
        String rootId = "C_" + clientId;

        JsTree clientTree = new JsTree();
        clientTree.setDataType("C");
        clientTree.setType("client");
        clientTree.setIcon("mdi mdi-office-building-outline mid-18px");
        clientTree.setParent("#");
        clientTree.setId(rootId);
        clientTree.setText("머큐리프로젝트");
        clientTree.setData(new CamelMap());
        clientTree.setDivCd("C");
        clientTree.setClientId(clientId);

        List<JsTree> root = new ArrayList<>();
        root.add(clientTree);

        List<CamelMap> departmentList = organizationRepository.findDepartmentAll(clientId);
        Map<Integer, String> departmentKeyMap = new HashMap<>();
        String departmentType = "D";
        departmentList.forEach(item -> {
            JsTree jsTree = new JsTree();
            jsTree.setDataType(departmentType);
            jsTree.setType("department");
            jsTree.setIcon("mdi mdi-microsoft-teams mdi-18px text-primary");

            if ("ROOT".equals(item.get("parentDepartmentKey"))) {
                jsTree.setParent(clientTree.getId());
            } else {
                jsTree.setParent(departmentType + "_" + item.get("parentDepartmentKey"));
            }

            String id = departmentType + "_" + item.get("departmentKey");
            jsTree.setId(id);
            jsTree.setText(item.getString("name"));
            jsTree.setData(item);
            jsTree.setDivCd(departmentType);
            jsTree.setClientId(clientId);

            root.add(jsTree);

            departmentKeyMap.put(item.getInt("id"), id);
        });

        List<CamelMap> employeeList = organizationRepository.findEmployeeAll(clientId);
        String employeeType = "E";
        employeeList.forEach(item -> {
            JsTree jsTree = new JsTree();
            jsTree.setDataType(employeeType);
            jsTree.setType("account");
            jsTree.setIcon("mdi mdi-account mdi-18px text-primary");

            if (item.get("departmentId") != null) {
                String departmentKey = departmentKeyMap.get(item.getInt("departmentId"));
                if (departmentKey != null) {
                    jsTree.setParent(departmentKey);
                } else {
                    jsTree.setParent(rootId);
                }
            } else {
                jsTree.setParent(rootId);
            }

            jsTree.setId(employeeType + "_" + item.get("userKey"));
            jsTree.setText(item.getString("name"));
            jsTree.setData(item);
            jsTree.setDivCd(employeeType);
            jsTree.setClientId(clientId);

            root.add(jsTree);
        });

        return root;
    }

    @Transactional(readOnly = true)
    public List<UserGroup> findDepartmentGroups(Integer clientId, Long departmentId) {
        return organizationRepository.findDepartmentGroups(clientId, departmentId);
    }

    @Transactional(readOnly = true)
    public Department findDepartment(Integer clientId, Long id) {
        return organizationRepository.findDepartment(clientId, id);
    }

    @Transactional(readOnly = true)
    public Department findDepartmentByDepartmentKey(Integer clientId, String departmentKey) {
        return organizationRepository.findDepartmentByDepartmentKey(clientId, departmentKey);
    }

    @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#department.clientId.toString()")
    public int insertDepartment(Department department) {
        return organizationRepository.insertDepartment(department);
    }

    @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#department.clientId.toString()")
    public int updateDepartment(Department department) {
        return organizationRepository.updateDepartment(department);
    }

    @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#changeDepartmentDto.clientId.toString()")
    public int changeEmployeeDepartment(ChangeDepartmentDto changeDepartmentDto) {
        return organizationRepository.changeEmployeeDepartment(changeDepartmentDto);
    }

    @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#changeDepartmentDto.clientId.toString()")
    public int changeDepartmentDepartment(ChangeDepartmentDto changeDepartmentDto) {
        return organizationRepository.changeDepartmentDepartment(changeDepartmentDto);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#clientId")
    })
    public boolean clearCache(String clientId) {
        log.debug("clearCache {}", clientId);
        return true;
    }
}
