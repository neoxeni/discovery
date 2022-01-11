package com.mercury.discovery.base.organization.web;

import com.mercury.discovery.base.code.model.Code;
import com.mercury.discovery.base.code.service.CodeService;
import com.mercury.discovery.base.organization.model.ChangeDepartmentDto;
import com.mercury.discovery.base.organization.model.Department;
import com.mercury.discovery.base.organization.model.OrganizationSearchDto;
import com.mercury.discovery.base.organization.service.OrganizationService;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.UserRole;
import com.mercury.discovery.common.SimpleResponseModel;
import com.mercury.discovery.common.model.CamelMap;
import com.mercury.discovery.utils.IDGenerator;
import com.mercury.discovery.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${apps.request-mapping}", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganizationRestController {

    private final OrganizationService organizationService;

    private final CodeService codeService;

    @GetMapping("/base/organizations")
    public ResponseEntity<?> getOrganizations(AppUser appUser, OrganizationSearchDto organizationSearchDto) {
        organizationSearchDto.setClientId(appUser.getClientId());
        String str = organizationSearchDto.getStr();
        List<CamelMap> list;
        if (str == null) {
            list = organizationService.findDeptEmpListForTree(organizationSearchDto);
        } else {
            list = organizationService.findDeptEmpListForTreeSearch(organizationSearchDto);
        }

        for (CamelMap map : list) {
            map.put("children", "Y".equals(map.get("isExsitSub")));

            if ("D".equals(map.get("gubun"))) {
                map.put("icon", "mdi mdi-microsoft-teams text-primary");
            } else if ("E".equals(map.get("gubun"))) {
                map.put("icon", "mdi mdi-account text-secondary");
            }

            if ("0".equals(map.get("parent"))) {
                map.put("parent", "#");
                map.put("icon", "mdi mdi-office-building-outline");
            }
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/base/organizations/tree")
    public ResponseEntity<?> getOrganizationsForTree(AppUser appUser) {
        CamelMap organization = organizationService.findDeptEmpListForTreeAll(appUser.getClientId());
        return ResponseEntity.ok(organization);
    }

    @GetMapping("/base/organizations/list")
    public ResponseEntity<?> getOrganizationsForList(AppUser appUser, Boolean department) {
        List<CamelMap> list = new ArrayList<>();
        if (department != null && department) {
            list.addAll(organizationService.findDeptListAll(appUser.getClientId()));
        }

        list.addAll(organizationService.findEmpListAll(appUser.getClientId()));
        return ResponseEntity.ok(list);
    }

    @GetMapping("/base/organizations/departments/{deptNo}")
    public ResponseEntity<?> getDepartment(AppUser appUser, @PathVariable Integer deptNo) {
        Department department = organizationService.findDepartment(appUser.getClientId(), deptNo);

        List<UserRole> list = organizationService.findDepartmentsRoles(appUser.getClientId(), department.getDeptNo());
        List<UserRole> roles = new ArrayList<>();
        List<UserRole> parentsRoles = new ArrayList<>();
        list.forEach(userRole -> {
            if (userRole.getDataNo().equals(department.getDeptNo())) {
                roles.add(userRole);
            } else {
                parentsRoles.add(userRole);
            }
        });

        department.setRoles(roles);
        department.setParentsRoles(parentsRoles);

        return ResponseEntity.ok(department);
    }

    @PostMapping("/base/organizations/departments")
    public ResponseEntity<?> postDepartment(AppUser appUser, @RequestBody Department department) {
        department.setClientId(appUser.getClientId());
        department.setRegEmpNo(appUser.getId());
        department.setRegDt(LocalDateTime.now());

        if(!StringUtils.hasLength(department.getDeptCd())){
            department.setDeptCd(IDGenerator.getUUID());
        }

        if(department.getDpth() == null){
            department.setDpth(0);
        }


        int affected = organizationService.insertDepartment(department);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/organizations/departments")
    public ResponseEntity<?> patchDepartment(AppUser appUser, @RequestBody Department department) {
        department.setClientId(appUser.getClientId());
        department.setUpdEmpNo(appUser.getId());
        department.setUpdDt(LocalDateTime.now());
        int affected = organizationService.updateDepartment(department);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    //직위(OR02)
    @PatchMapping("/base/organizations/posts")
    public ResponseEntity<?> patchPosts(AppUser appUser, @RequestBody List<Code> posts) {
        Integer clientId = appUser.getClientId();

        codeService.setDefault("OR02", clientId, appUser.getId(), posts);
        codeService.deleteCodesByDivCd(clientId,"OR02");
        int affected = posts.stream().mapToInt(codeService::insert).sum();
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update"), posts));
    }

    //직책(OR01)
    @PatchMapping("/base/organizations/jobs")
    public ResponseEntity<?> patchJobs(AppUser appUser, @RequestBody List<Code> jobs) {
        Integer clientId = appUser.getClientId();

        codeService.setDefault("OR01", clientId, appUser.getId(), jobs);
        codeService.deleteCodesByDivCd(clientId,"OR01");
        int affected = jobs.stream().mapToInt(codeService::insert).sum();
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update"), jobs));
    }

    @PatchMapping("/base/organizations/departments/change")
    public ResponseEntity<?> patchMoveDeptEmp(AppUser appUser, @RequestBody ChangeDepartmentDto changeDepartmentDto) {
        changeDepartmentDto.setClientId(appUser.getClientId());
        changeDepartmentDto.setUpdEmpNo(appUser.getId());
        changeDepartmentDto.setUpdDt(LocalDateTime.now());

        String moveType = changeDepartmentDto.getMoveType();
        int affected = 0;
        if ("E".equals(moveType)) {
            affected = organizationService.changeEmployeeDepartment(changeDepartmentDto);
        } else if ("D".equals(moveType)) {
            affected = organizationService.changeDepartmentDepartment(changeDepartmentDto);
        }

        return ResponseEntity.ok(new SimpleResponseModel(affected, "부서가 변경 되었습니다."));
    }

    @GetMapping("/base/organizations/departments/tree")
    public ResponseEntity<?> getDepartmentsTree(AppUser appUser) {
        return ResponseEntity.ok(organizationService.findDepartmentsTree(appUser.getClientId()));
    }

    @GetMapping("/base/organizations/clearCache")
    public ResponseEntity<?> clearCache(@RequestParam String clientId) {
        return ResponseEntity.ok(organizationService.clearCache(clientId));
    }

    @GetMapping("/base/organizations/departments/list")
    public ResponseEntity<?> getDepartmentsList(AppUser appUser) {
        return ResponseEntity.ok(organizationService.findDeptListAll(appUser.getClientId()));
    }

    @GetMapping("/base/organizations/employee/list")
    public ResponseEntity<?> getEmployeeList(AppUser appUser) {
        return ResponseEntity.ok(organizationService.findEmpListAll(appUser.getClientId()));
    }

    @GetMapping("/base/organizations/departments/parent/{pDeptNo}")
    public ResponseEntity<?> getDepartmentByParent(AppUser appUser, @PathVariable int pDeptNo, OrganizationSearchDto organizationSearchDto) {
        String useYn = organizationSearchDto.getUseYn();
        return ResponseEntity.ok(organizationService.findDepartmentByParent(appUser.getClientId(), useYn, pDeptNo));
    }

    @GetMapping("/base/organizations/employee/department/{deptNo}")
    public ResponseEntity<?> getEmployeeByDepartment(AppUser appUser, @PathVariable int deptNo, OrganizationSearchDto organizationSearchDto) {
        String useYn = organizationSearchDto.getUseYn();
        String rtrmntYn = organizationSearchDto.getRtrmntYn();
        String rootYn = organizationSearchDto.getRootYn();
        return ResponseEntity.ok(organizationService.findEmployeeByDepartment(appUser.getClientId(), useYn, rtrmntYn, rootYn, deptNo));
    }

}
