package com.mercury.discovery.base.organization.web;

import com.mercury.discovery.base.code.model.Code;
import com.mercury.discovery.base.code.service.CodeService;
import com.mercury.discovery.base.organization.model.ChangeDepartmentDto;
import com.mercury.discovery.base.organization.model.Department;
import com.mercury.discovery.base.organization.service.OrganizationService;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.UserRole;
import com.mercury.discovery.base.users.service.UserService;
import com.mercury.discovery.common.model.JsTree;
import com.mercury.discovery.common.web.SimpleResponseModel;
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

    private final UserService userService;

    private final CodeService codeService;

    @GetMapping("/base/organizations/tree")
    public ResponseEntity<?> getOrganizationsForTree(AppUser appUser) {
        List<JsTree> codes = organizationService.findAllForTree(appUser.getClientId());
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/base/organizations/employees/{userKey}")
    public ResponseEntity<?> getEmployee(AppUser appUser, @PathVariable String userKey) {
        return ResponseEntity.ok(userService.getUser(appUser.getClientId(), userKey));
    }

    @GetMapping("/base/organizations/departments/{departmentKey}")
    public ResponseEntity<?> getDepartment(AppUser appUser, @PathVariable String departmentKey) {
        Department department = organizationService.findDepartmentByDepartmentKey(appUser.getClientId(), departmentKey);


        List<UserRole> roles = new ArrayList<>();
        List<UserRole> parentsRoles = new ArrayList<>();

        /*
        List<UserRole> list = organizationService.findDepartmentsRoles(appUser.getClientId(), department.getId());
        list.forEach(userRole -> {
            if (userRole.getDataNo().equals(department.getId())) {
                roles.add(userRole);
            } else {
                parentsRoles.add(userRole);
            }
        });
        */

        department.setRoles(roles);
        department.setParentsRoles(parentsRoles);

        return ResponseEntity.ok(department);
    }

    @PostMapping("/base/organizations/departments")
    public ResponseEntity<?> postDepartment(AppUser appUser, @RequestBody Department department) {
        LocalDateTime now = LocalDateTime.now();
        department.setClientId(appUser.getClientId());
        department.setCreatedBy(appUser.getId());
        department.setCreatedAt(now);
        department.setUpdatedBy(appUser.getId());
        department.setUpdatedAt(now);

        if (!StringUtils.hasLength(department.getDepartmentKey())) {
            department.setDepartmentKey(IDGenerator.getUUID());
        }

        if (!StringUtils.hasLength(department.getParentDepartmentKey())) {
            department.setParentDepartmentKey("ROOT");
            ;
        }

        int affected = organizationService.insertDepartment(department);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/organizations/departments")
    public ResponseEntity<?> patchDepartment(AppUser appUser, @RequestBody Department department) {
        department.setClientId(appUser.getClientId());
        department.setUpdatedBy(appUser.getId());
        department.setUpdatedAt(LocalDateTime.now());
        int affected = organizationService.updateDepartment(department);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    //직위(OR02)
    @PatchMapping("/base/organizations/posts")
    public ResponseEntity<?> patchPosts(AppUser appUser, @RequestBody List<Code> posts) {
        Integer clientId = appUser.getClientId();

        codeService.setDefault("OR02", clientId, appUser.getId(), posts);
        codeService.deleteCodesByDivCd(clientId, "OR02");
        int affected = posts.stream().mapToInt(codeService::insert).sum();
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update"), posts));
    }

    //직책(OR01)
    @PatchMapping("/base/organizations/jobs")
    public ResponseEntity<?> patchJobs(AppUser appUser, @RequestBody List<Code> jobs) {
        Integer clientId = appUser.getClientId();

        codeService.setDefault("OR01", clientId, appUser.getId(), jobs);
        codeService.deleteCodesByDivCd(clientId, "OR01");
        int affected = jobs.stream().mapToInt(codeService::insert).sum();
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update"), jobs));
    }

    @PatchMapping("/base/organizations/departments/change")
    public ResponseEntity<?> patchMoveDeptEmp(AppUser appUser, @RequestBody ChangeDepartmentDto changeDepartmentDto) {
        changeDepartmentDto.setClientId(appUser.getClientId());
        changeDepartmentDto.setUpdatedBy(appUser.getId());
        changeDepartmentDto.setUpdatedAt(LocalDateTime.now());

        String moveType = changeDepartmentDto.getMoveType();
        int affected = 0;
        if ("E".equals(moveType)) {
            affected = organizationService.changeEmployeeDepartment(changeDepartmentDto);
        } else if ("D".equals(moveType)) {
            affected = organizationService.changeDepartmentDepartment(changeDepartmentDto);
        }

        return ResponseEntity.ok(new SimpleResponseModel(affected, "부서가 변경 되었습니다."));
    }

    @GetMapping("/base/organizations/clearCache")
    public ResponseEntity<?> clearCache(@RequestParam String clientId) {
        return ResponseEntity.ok(organizationService.clearCache(clientId));
    }
}
