package com.mercury.discovery.base.users.web;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.AppUserRequestDto;
import com.mercury.discovery.base.users.service.UserService;
import com.mercury.discovery.common.SimpleResponseModel;
import com.mercury.discovery.common.error.exception.BadParameterException;
import com.mercury.discovery.common.log.security.SecurityLogging;
import com.mercury.discovery.util.DateTimeUtils;
import com.mercury.discovery.util.IDGenerator;
import com.mercury.discovery.util.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${apps.request-mapping}", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {

    private final UserService userService;

    @PostMapping("/base/users/authentication/api")
    public ResponseEntity<?> apiLogin(AppUser appUser) {
        String jwt = userService.getApiToken(appUser);
        Map<String, String> result = new HashMap<>();
        result.put("jwt", jwt);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/base/users/{empNo}")
    public ResponseEntity<?> getUser(AppUser appUser, @PathVariable Integer empNo) {
        AppUserRequestDto appUserRequestDto = new AppUserRequestDto();
        appUserRequestDto.setCmpnyNo(appUser.getCmpnyNo());
        appUserRequestDto.setEmpNo(empNo);

        AppUser targetAppUser = userService.getUser(appUserRequestDto);
        if (targetAppUser != null) {
            return ResponseEntity.ok(targetAppUser);
        } else {
            throw new BadParameterException("해당 유저를 찾을수 없습니다.");
        }
    }

    @PostMapping("/base/users")
    public ResponseEntity<?> postUser(AppUser appUser, @RequestBody AppUser employee) {
        employee.setCmpnyNo(appUser.getCmpnyNo());
        employee.setRegEmpNo(appUser.getEmpNo());
        employee.setRegDt(LocalDateTime.now());
        employee.setUserKey(IDGenerator.getUUID());
        employee.setRealEmpNm(employee.getEmpNm());

        int affected = userService.insert(employee);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/users")
    @SecurityLogging()
    public ResponseEntity<?> patchUser(AppUser appUser, @RequestBody AppUser employee) {
        employee.setCmpnyNo(appUser.getCmpnyNo());
        employee.setUpdEmpNo(appUser.getEmpNo());
        employee.setUpdDt(LocalDateTime.now());

        if (employee.getRtrmntDd() == null && "Y".equals(employee.getRtrmntYn())) {
            employee.setRtrmntDd(DateTimeUtils.getToday("yyyyMMdd"));
        } else if (employee.getRtrmntDd() != null && "N".equals(employee.getRtrmntYn())) {
            employee.setRtrmntDd(null);
        }

        int affected = userService.update(employee);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/users")
    public ResponseEntity<?> deleteUser(AppUser appUser, @RequestBody AppUser employee) {
        //user를 삭제하면 table join 에 영향이 생길듯
        employee.setCmpnyNo(appUser.getCmpnyNo());
        int affected = userService.delete(employee);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @PatchMapping("/base/users/{userKey}/password")
    public ResponseEntity<?> patchUsersPassword(AppUser appUser, @PathVariable String userKey) {
        AppUser targetAppUser = userService.getUser(appUser.getCmpnyNo(), userKey);
        if (targetAppUser != null) {
            if (!appUser.getCmpnyNo().equals(targetAppUser.getCmpnyNo())) {
                throw new BadParameterException("해당 유저를 찾을수 없습니다.");
            }

            int affected = userService.resetUserPassword(targetAppUser.getEmpNo(), targetAppUser.getUsername());
            return ResponseEntity.ok(new SimpleResponseModel(affected, "패스워드가 아이디로 초기화 되었습니다."));
        } else {
            throw new BadParameterException("해당 유저를 찾을수 없습니다.");
        }
    }



    /**
     * 화면 잠금 체크를 팝업이나 다른 탭에서도 하기위해서 웹소켓을 이용한다.
     * @param userKey 사용자 키
     * @param uuid 유니크 키
     */
    @MessageMapping("/message/active/{userKey}/{uuid}")
    public void active(@DestinationVariable("userKey") String userKey, @DestinationVariable("uuid") String uuid) {
        userService.sendActiveSignal(userKey, uuid);
    }

}
