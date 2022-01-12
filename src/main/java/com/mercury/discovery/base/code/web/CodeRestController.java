package com.mercury.discovery.base.code.web;

import com.mercury.discovery.base.code.model.Code;
import com.mercury.discovery.base.code.model.CodeDiv;
import com.mercury.discovery.base.code.service.CodeService;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.web.SimpleResponseModel;
import com.mercury.discovery.common.error.exception.BadParameterException;
import com.mercury.discovery.common.model.JsTree;
import com.mercury.discovery.utils.IDGenerator;
import com.mercury.discovery.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${apps.request-mapping}", produces = MediaType.APPLICATION_JSON_VALUE)
public class CodeRestController {
    private final CodeService codeService;

    @GetMapping("/base/codes/{divCd}")
    public ResponseEntity<?> getCodes(AppUser appUser, @PathVariable String divCd) {
        List<Code> codes = codeService.findByDiv(divCd, appUser.getClientId());
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/base/codes")
    public ResponseEntity<?> getCodes(AppUser appUser) {
        Code code = new Code();
        code.setClientId(appUser.getClientId());
        List<Code> codes = codeService.findAll(code);
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/base/codes/tree")
    public ResponseEntity<?> getCodesForTree(AppUser appUser) {
        Code code = new Code();
        code.setClientId(appUser.getClientId());
        List<JsTree> codes = codeService.findAllForTree(code);
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/base/codes/tree/{divCd}")
    public ResponseEntity<?> getCodesForTree(AppUser appUser, @PathVariable String divCd) {
        Code code = new Code();
        code.setClientId(appUser.getClientId());
        code.setDivCd(divCd);
        List<JsTree> codes = codeService.findAllForTree(code);
        return ResponseEntity.ok(codes);
    }

    @GetMapping(value = "/base/codes/scripts", produces = "text/javascript; charset=utf-8")
    public ResponseEntity<String> getCodesForScript(AppUser appUser, Locale locale) {
        Code code = new Code();
        code.setClientId(appUser.getClientId());

        return ResponseEntity.ok(codeService.findAllForScript(code, locale));
    }


    @GetMapping(value = "/base/codes/scripts/{clientId}", produces = "text/javascript; charset=utf-8")
    public ResponseEntity<String> getCodesForScript(@PathVariable Integer clientId, Locale locale) {
        Code code = new Code();
        code.setClientId(clientId);

        return ResponseEntity.ok(codeService.findAllForScript(code, locale));
    }

    @PostMapping("/base/codes")
    public ResponseEntity<?> postCodes(AppUser appUser, @RequestBody Code code) {
        LocalDateTime now = LocalDateTime.now();
        code.setClientId(appUser.getClientId());
        code.setCreatedBy(appUser.getId());
        code.setCreatedAt(now);
        code.setUpdatedBy(appUser.getId());
        code.setUpdatedAt(now);
        int affected = codeService.insert(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }


    @PostMapping("/base/codes/{divCd}")
    public ResponseEntity<?> postCodes(AppUser appUser, @RequestBody Code code, @PathVariable String divCd) {
        LocalDateTime now = LocalDateTime.now();
        code.setCode(IDGenerator.getUUID());
        code.setClientId(appUser.getClientId());
        code.setCreatedBy(appUser.getId());
        code.setCreatedAt(LocalDateTime.now());
        code.setUpdatedBy(appUser.getId());
        code.setUpdatedAt(now);
        code.setDivCd(divCd);
        int affected = codeService.insert(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }


    @PatchMapping("/base/codes/{divCd}")
    public ResponseEntity<?> patchCodes(AppUser appUser, @RequestBody Code code, @PathVariable String divCd) {
        code.setClientId(appUser.getClientId());
        code.setUpdatedBy(appUser.getId());
        code.setUpdatedAt(LocalDateTime.now());
        code.setDivCd(divCd);
        int affected = codeService.update(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }


    @PatchMapping("/base/codes")
    public ResponseEntity<?> patchCodes(AppUser appUser, @RequestBody Code code) {
        code.setClientId(appUser.getClientId());
        code.setUpdatedBy(appUser.getId());
        code.setUpdatedAt(LocalDateTime.now());
        int affected = codeService.update(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/codes")
    public ResponseEntity<?> deleteCodes(AppUser appUser, Code code) {
        if (code.getClientId() == -1) {
            throw new BadParameterException("해당 코드는 삭제할 수 없습니다. ");
        }

        code.setClientId(appUser.getClientId());

        Objects.requireNonNull(code.getCode());
        Objects.requireNonNull(code.getDivCd());

        int affected = codeService.delete(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @PostMapping("/base/codeDivs")
    public ResponseEntity<?> postCodesDivs(AppUser appUser, @RequestBody CodeDiv codeDiv) {

        codeDiv.setClientId(appUser.getClientId());
        int affected = codeService.insertCodeDiv(codeDiv);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/codeDivs")
    public ResponseEntity<?> patchCodesDivs(AppUser appUser, @RequestBody CodeDiv codeDiv) {

        codeDiv.setClientId(appUser.getClientId());

        int affected = codeService.updateCodeDiv(codeDiv);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/codeDivs")
    public ResponseEntity<?> deleteCodesDivs(AppUser appUser, CodeDiv codeDiv) {
        if (codeDiv.getClientId() == -1 || "N".equals(codeDiv.getUpdEnableYn())) {
            throw new BadParameterException("해당 코드 분류는 삭제할 수 없습니다.");
        }

        codeDiv.setClientId(appUser.getClientId());
        Objects.requireNonNull(codeDiv.getDivCd());

        int affected = codeService.deleteCodeDiv(codeDiv);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }
}
