package com.mercury.discovery.base.users.web;

import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.AppUserRequestDto;
import com.mercury.discovery.base.users.service.UserService;
import com.mercury.discovery.common.SimpleResponseModel;
import com.mercury.discovery.common.error.exception.BadParameterException;
import com.mercury.discovery.common.log.security.SecurityLogging;
import com.mercury.discovery.utils.IDGenerator;
import com.mercury.discovery.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${apps.request-mapping}", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {

    private final UserService userService;

    @GetMapping("/base/users/{id}")
    public ResponseEntity<?> getUser(AppUser appUser, @PathVariable Integer id) {
        AppUserRequestDto appUserRequestDto = new AppUserRequestDto();
        appUserRequestDto.setClientId(appUser.getClientId());
        appUserRequestDto.setId(id);

        AppUser targetAppUser = userService.getUser(appUserRequestDto);
        if (targetAppUser != null) {
            return ResponseEntity.ok(targetAppUser);
        } else {
            throw new BadParameterException("해당 유저를 찾을수 없습니다.");
        }
    }

    @PostMapping("/base/users")
    public ResponseEntity<?> postUser(AppUser appUser, @RequestBody AppUser employee) {
        employee.setClientId(appUser.getClientId());
        employee.setCreatedBy(appUser.getId());
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUserKey(IDGenerator.getUUID());
        employee.setNickname(employee.getName());

        int affected = userService.insert(employee);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/users")
    @SecurityLogging()
    public ResponseEntity<?> patchUser(AppUser appUser, @RequestBody AppUser employee) {
        employee.setClientId(appUser.getClientId());
        employee.setUpdatedBy(appUser.getId());
        employee.setUpdatedAt(LocalDateTime.now());

        int affected = userService.update(employee);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/users")
    public ResponseEntity<?> deleteUser(AppUser appUser, @RequestBody AppUser employee) {
        //user를 삭제하면 table join 에 영향이 생길듯
        employee.setClientId(appUser.getClientId());
        int affected = userService.delete(employee);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @PatchMapping("/base/users/{userKey}/password")
    public ResponseEntity<?> patchUsersPassword(AppUser appUser, @PathVariable String userKey) {
        AppUser targetAppUser = userService.getUser(appUser.getClientId(), userKey);
        if (targetAppUser != null) {
            if (!appUser.getClientId().equals(targetAppUser.getClientId())) {
                throw new BadParameterException("해당 유저를 찾을수 없습니다.");
            }

            int affected = userService.resetUserPassword(targetAppUser.getId(), targetAppUser.getUsername());
            return ResponseEntity.ok(new SimpleResponseModel(affected, "패스워드가 아이디로 초기화 되었습니다."));
        } else {
            throw new BadParameterException("해당 유저를 찾을수 없습니다.");
        }
    }


    /**
     * 화면 잠금 체크를 팝업이나 다른 탭에서도 하기위해서 웹소켓을 이용한다.
     *
     * @param userKey 사용자 키
     * @param uuid    유니크 키
     */
    @MessageMapping("/message/active/{userKey}/{uuid}")
    public void active(@DestinationVariable("userKey") String userKey, @DestinationVariable("uuid") String uuid) {
        userService.sendActiveSignal(userKey, uuid);
    }

}
