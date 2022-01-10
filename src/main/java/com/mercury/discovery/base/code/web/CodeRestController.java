package com.mercury.discovery.base.code.web;

import com.mercury.discovery.base.code.model.Code;
import com.mercury.discovery.base.code.model.CodeDiv;
import com.mercury.discovery.base.code.service.CodeService;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.SimpleResponseModel;
import com.mercury.discovery.common.error.exception.BadParameterException;
import com.mercury.discovery.common.model.JsTree;
import com.mercury.discovery.util.IDGenerator;
import com.mercury.discovery.util.MessagesUtils;
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
        List<Code> codes = codeService.findByDiv(divCd, appUser.getCmpnyNo());
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/base/codes")
    public ResponseEntity<?> getCodes(AppUser appUser) {
        Code code = new Code();
        code.setCmpnyNo(appUser.getCmpnyNo());
        List<Code> codes = codeService.findAll(code);
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/base/codes/tree")
    public ResponseEntity<?> getCodesForTree(AppUser appUser) {
        Code code = new Code();
        code.setCmpnyNo(appUser.getCmpnyNo());
        List<JsTree> codes = codeService.findAllForTree(code);
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/base/codes/tree/{divCd}")
    public ResponseEntity<?> getCodesForTree(AppUser appUser, @PathVariable String divCd) {
        Code code = new Code();
        code.setCmpnyNo(appUser.getCmpnyNo());
        code.setDivCd(divCd);
        List<JsTree> codes = codeService.findAllForTree(code);
        return ResponseEntity.ok(codes);
    }

    @GetMapping(value = "/base/codes/scripts", produces = "text/javascript; charset=utf-8")
    public ResponseEntity<String> getCodesForScript(AppUser appUser, Locale locale) {
        Code code = new Code();
        code.setCmpnyNo(appUser.getCmpnyNo());

        return ResponseEntity.ok(codeService.findAllForScript(code, locale));
    }


    @GetMapping(value = "/base/codes/scripts/{cmpnyNo}", produces = "text/javascript; charset=utf-8")
    public ResponseEntity<String> getCodesForScript(@PathVariable Integer cmpnyNo, Locale locale) {
        Code code = new Code();
        code.setCmpnyNo(cmpnyNo);

        return ResponseEntity.ok(codeService.findAllForScript(code, locale));
    }

    @PostMapping("/base/codes")
    public ResponseEntity<?> postCodes(AppUser appUser, @RequestBody Code code) {
        code.setCmpnyNo(appUser.getCmpnyNo());
        code.setRegEmpNo(appUser.getEmpNo());
        code.setRegDt(LocalDateTime.now());
        int affected = codeService.insert(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }


    @PostMapping("/base/codes/{divCd}")
    public ResponseEntity<?> postCodes(AppUser appUser, @RequestBody Code code, @PathVariable String divCd) {
        code.setCd(IDGenerator.getUUID());
        code.setCmpnyNo(appUser.getCmpnyNo());
        code.setRegEmpNo(appUser.getEmpNo());
        code.setRegDt(LocalDateTime.now());
        code.setDivCd(divCd);
        int affected = codeService.insert(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }


    @PatchMapping("/base/codes/{divCd}")
    public ResponseEntity<?> patchCodes(AppUser appUser, @RequestBody Code code, @PathVariable String divCd) {
        code.setCmpnyNo(appUser.getCmpnyNo());
        code.setUpdEmpNo(appUser.getEmpNo());
        code.setUpdDt(LocalDateTime.now());
        code.setDivCd(divCd);
        int affected = codeService.update(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }


    @PatchMapping("/base/codes")
    public ResponseEntity<?> patchCodes(AppUser appUser, @RequestBody Code code) {
        code.setCmpnyNo(appUser.getCmpnyNo());
        code.setUpdEmpNo(appUser.getEmpNo());
        code.setUpdDt(LocalDateTime.now());
        int affected = codeService.update(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/codes")
    public ResponseEntity<?> deleteCodes(AppUser appUser, Code code) {
        if (code.getCmpnyNo() == -1) {
            throw new BadParameterException("해당 코드는 삭제할 수 없습니다. ");
        }

        code.setCmpnyNo(appUser.getCmpnyNo());

        Objects.requireNonNull(code.getCd());
        Objects.requireNonNull(code.getDivCd());

        int affected = codeService.delete(code);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @PostMapping("/base/codeDivs")
    public ResponseEntity<?> postCodesDivs(AppUser appUser, @RequestBody CodeDiv codeDiv) {

        codeDiv.setCmpnyNo(appUser.getCmpnyNo());
        int affected = codeService.insertCodeDiv(codeDiv);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/codeDivs")
    public ResponseEntity<?> patchCodesDivs(AppUser appUser, @RequestBody CodeDiv codeDiv) {

        codeDiv.setCmpnyNo(appUser.getCmpnyNo());

        int affected = codeService.updateCodeDiv(codeDiv);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/codeDivs")
    public ResponseEntity<?> deleteCodesDivs(AppUser appUser, CodeDiv codeDiv) {
        if (codeDiv.getCmpnyNo() == -1 || "N".equals(codeDiv.getUpdEnableYn())) {
            throw new BadParameterException("해당 코드 분류는 삭제할 수 없습니다.");
        }

        codeDiv.setCmpnyNo(appUser.getCmpnyNo());
        Objects.requireNonNull(codeDiv.getDivCd());

        int affected = codeService.deleteCodeDiv(codeDiv);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }
}
