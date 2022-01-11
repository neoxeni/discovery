package com.mercury.discovery.base.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercury.discovery.base.code.model.Code;
import com.mercury.discovery.base.code.model.CodeDiv;
import com.mercury.discovery.common.model.JsTree;
import com.mercury.discovery.utils.IDGenerator;
import com.mercury.discovery.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class CodeService {
    private final CodeRepository codeRepository;

    private final CacheManager cacheManager;

    private final ObjectMapper objectMapper;

    private final MessageSourceAccessor msgAccessor;

    private String codeScriptEn = "";

    private String codeScriptKo = "";

    @Value("${apps.packages}")
    private String packages;

    @PostConstruct
    private void init() {
        try {

            Map<String, List<Code>> enumCodeMapKo = new HashMap<>();

            Map<String, List<Code>> enumCodeMapEn = new HashMap<>();

            StringBuilder sb = new StringBuilder();

            Locale localeKo = Locale.KOREA;
            Locale localeEn = Locale.ENGLISH;
            LocalDateTime now = LocalDateTime.now();


            List<Class> enumClassList = new ArrayList<>();

            String[] packagesArr = packages.split(",");
            for (String packageStr : packagesArr) {
                if (StringUtils.hasLength(packageStr)) {
                    Class[] classes = ObjectUtils.getClasses(packageStr.trim(), Enum.class);
                    for (Class clazz : classes) {
                        enumClassList.add(clazz);
                    }
                }
            }

            for (Class clazz : enumClassList) {
                String name = ObjectUtils.getSimpleFullName(clazz);

                sb.append(String.format("%-38s", name));

                List<Code> codesKo = new ArrayList<>();
                List<Code> codesEn = new ArrayList<>();
                Object[] enumConstants = clazz.getEnumConstants();
                int sortNo = 1;
                for (Object e : enumConstants) {
                    Enum<?> enumObject = (Enum<?>) e;

                    String cd = enumObject.name();

                    String messageCode = "enum." + name + "." + cd;
                    String ko = msgAccessor.getMessage(messageCode, localeKo);
                    String en = null;
                    if (ko.startsWith("enum.")) {
                        ko = null;
                    } else {
                        en = msgAccessor.getMessage(messageCode, localeEn);
                    }

                    if (ko == null) {
                        try {
                            Method method = enumObject.getDeclaringClass().getDeclaredMethod("getLabel");
                            ko = (String) method.invoke(enumObject);
                        } catch (Exception ex) {
                            ko = cd;
                        }
                    }

                    if (en == null) {
                        en = ko;
                    }

                    String etc1 = getEnumInvokeMethod(enumObject, "getEtc1");
                    String etc2 = getEnumInvokeMethod(enumObject, "getEtc2");
                    String etc3 = getEnumInvokeMethod(enumObject, "getEtc3");
                    String etc4 = getEnumInvokeMethod(enumObject, "getEtc4");
                    String useYn = getEnumInvokeMethod(enumObject, "getUseYn");
                    String prntCd = getEnumInvokeMethod(enumObject, "getPrntCd");


                    sb.append("[").append(cd).append(":").append(ko).append(":").append(en).append("]");


                    codesKo.add(makeEnumCode(name, cd, ko, etc1, etc2, etc3, etc4, sortNo, useYn, prntCd, now));
                    codesEn.add(makeEnumCode(name, cd, en, etc1, etc2, etc3, etc4, sortNo, useYn, prntCd, now));
                }
                sb.append("\n");
                enumCodeMapKo.put(name, codesKo);
                enumCodeMapEn.put(name, codesEn);
            }

            try {
                codeScriptKo = objectMapper.writeValueAsString(enumCodeMapKo);
                codeScriptKo = codeScriptKo.substring(1, codeScriptKo.length() - 1);//앞뒤{ } 제거
            } catch (JsonProcessingException e) {
                log.error("getCodeScript error {}", enumCodeMapEn, e);
                codeScriptKo = "";
            }

            try {
                codeScriptEn = objectMapper.writeValueAsString(enumCodeMapEn);
                codeScriptEn = codeScriptEn.substring(1, codeScriptEn.length() - 1);//앞뒤{ } 제거
            } catch (JsonProcessingException e) {
                log.error("getCodeScript error {}", enumCodeMapEn, e);
                codeScriptEn = "";
            }

            log.info("package com.mercury.discovery enum constant\n{}", sb.toString());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private Code makeEnumCode(String divCd, String cd, String cdNm, String etc1, String etc2, String etc3, String etc4,
                              int sortNo, String useYn, String prntCd, LocalDateTime now) {
        Code code = new Code();
        code.setDivCd(divCd);
        code.setCd(cd);
        code.setCdNm(cdNm);
        code.setEtc1(etc1 == null ? "" : etc1);
        code.setEtc2(etc2 == null ? "" : etc2);
        code.setEtc3(etc3 == null ? "" : etc3);
        code.setEtc4(etc4 == null ? "" : etc4);
        code.setUseYn(useYn == null ? "Y" : useYn);//null 인경우 Y
        code.setSortNo(sortNo);
        code.setPrntCd(prntCd == null ? "ROOT" : prntCd);

        code.setRegDt(now);
        code.setRegEmpNo(-1);
        code.setClientId(-1);

        return code;
    }

    private String getEnumInvokeMethod(Enum<?> enumObject, String methodName) {
        try {
            Method method = enumObject.getDeclaringClass().getDeclaredMethod(methodName);
            return (String) method.invoke(enumObject);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private List<Code> getData(Code code) {
        int clientId = code.getClientId();

        List<Code> items = new ArrayList<>();
        Cache cache = cacheManager.getCache("code");
        if (cache != null) {
            items = cache.get(clientId, List.class);
            if (items == null) {
                items = findAll(code);
                cache.put(clientId, items);
            }
        }

        return items;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "code", key = "#code.clientId")
    public List<Code> findAll(Code code) {
        return codeRepository.findAll(code);
    }

    @Transactional(readOnly = true)
    public List<JsTree> findAllForTree(Code code) {
        List<CodeDiv> codeDivs = codeRepository.findCodeDivAll(code);
        List<Code> codes = getData(code);

        List<JsTree> root = new ArrayList<>();

        codeDivs.forEach(item -> {
            JsTree jsTree = new JsTree();
            jsTree.setDataType("codeDiv");
            jsTree.setType("folder");
            jsTree.setParent("#");
            jsTree.setId(item.getDivCd());
            jsTree.setText(item.getDivNm());
            jsTree.setData(item);
            jsTree.setDivCd(item.getDivCd());
            jsTree.setClientId(item.getClientId());

            root.add(jsTree);
        });

        codes.forEach(item -> {
            JsTree jsTree = new JsTree();
            jsTree.setDataType("code");
            jsTree.setType("code");

            if ("ROOT".equals(item.getPrntCd())) {
                jsTree.setParent(item.getDivCd());
            } else {
                jsTree.setParent(item.getDivCd() + "_" + item.getPrntCd());
            }

            jsTree.setId(item.getDivCd() + "_" + item.getCd());
            jsTree.setText(item.getCdNm());
            jsTree.setData(item);
            jsTree.setDivCd(item.getDivCd());
            jsTree.setClientId(item.getClientId());

            root.add(jsTree);
        });

        return root;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Cacheable(cacheNames = "codeJs", key = "{#code.clientId, #locale.toString()}")
    public String findAllForScript(Code code, Locale locale) {
        List<Code> codes = getData(code);

        Map<String, List<Code>> childrenMap = new HashMap<>();
        for (Code lCode : codes) {
            String pid = lCode.getPrntCd();
            if ("ROOT".equals(pid)) {
                pid = lCode.getDivCd();
            }
            List<Code> childrenList = childrenMap.computeIfAbsent(pid, k -> new ArrayList<>());
            childrenList.add(lCode);
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        String staticCodeScript;
        if (locale == Locale.KOREAN) {
            staticCodeScript = codeScriptKo;
        } else {
            staticCodeScript = codeScriptEn;
        }


        String codeString;
        try {
            codeString = objectMapper.writeValueAsString(childrenMap);
            if (codeString.length() > 2) {
                codeString = codeString.substring(0, codeString.length() - 1);
                codeString = codeString + "," + staticCodeScript + "}";
            } else {
                codeString = "{" + staticCodeScript + "}";
            }
        } catch (JsonProcessingException e) {
            log.error("getCodeScript error {}", childrenMap, e);
            codeString = "{" + staticCodeScript + "}";
        }


        StringBuilder sb = new StringBuilder();

        sb.append("(function (mercury, $) {");
        sb.append("    window.mercury = mercury;");
        sb.append("    mercury.base = mercury.base || {};");
        sb.append("    mercury.base.company = mercury.base.company || {};");

        sb.append("    const self = mercury.base.company.code = {");
        sb.append("        CODES:").append(codeString);

        sb.append("        ,getCode: function(code, options){");
        sb.append("            const dOptions = {");
        sb.append("                type : 'object',   /*option*/");
        sb.append("                useYn : 'Y' ");
        sb.append("            };");

        sb.append("            options = Object.assign(dOptions, options || {});");
        sb.append("            let children = self.CODES[code] || [];");

        sb.append("            if(options.type === 'option'){");
        sb.append("                let option = '';");
        sb.append("                children.forEach(child=>{");
        sb.append("                    option += `<option value=\"${child.cd}\" data-div-cd=\"${child.divCd}\" data-etc1=\"${child.etc1}\" data-etc2=\"${child.etc2}\" data-etc3=\"${child.etc3}\" data-etc4=\"${child.etc4}\">${child.cdNm}</option>`;");
        sb.append("                });");
        sb.append("                return option;");
        sb.append("            }");

        sb.append("            return children;");
        sb.append("        }");// end of getCode

        sb.append("    };");
        sb.append("})(window.mercury || {}, jQuery);");

        return sb.toString();
    }

    @CacheEvict(cacheNames = {"code", "codeJs"}, key = "#code.clientId", allEntries = true)
    public int insert(Code code) {
        return codeRepository.insert(code);
    }

    @CacheEvict(cacheNames = {"code", "codeJs"}, key = "#code.clientId", allEntries = true)
    public int update(Code code) {
        return codeRepository.update(code);
    }

    @CacheEvict(cacheNames = {"code", "codeJs"}, key = "#code.clientId", allEntries = true)
    public int delete(Code code) {
        return codeRepository.delete(code);
    }

    @CacheEvict(cacheNames = {"code", "codeJs"}, key = "#clientId", allEntries = true)
    public int deleteCodesByDivCd(Integer clientId, String divCd) {
        CodeDiv codeDiv = new CodeDiv();
        codeDiv.setClientId(clientId);
        codeDiv.setDivCd(divCd);
        return codeRepository.deleteCodesByDivCd(codeDiv);
    }

    @CacheEvict(cacheNames = {"code", "codeJs"}, key = "#codeDiv.clientId", allEntries = true)
    public int insertCodeDiv(CodeDiv codeDiv) {
        return codeRepository.insertCodeDiv(codeDiv);
    }

    @CacheEvict(cacheNames = {"code", "codeJs"}, key = "#codeDiv.clientId", allEntries = true)
    public int updateCodeDiv(CodeDiv codeDiv) {
        return codeRepository.updateCodeDiv(codeDiv);
    }


    @CacheEvict(cacheNames = {"code", "codeJs"}, key = "#codeDiv.clientId", allEntries = true)
    public int deleteCodeDiv(CodeDiv codeDiv) {
        int affected = codeRepository.deleteCodeDiv(codeDiv);
        if (affected > 0) {
            codeRepository.deleteCodesByDivCd(codeDiv);
        }

        return affected;
    }

    public void setDefault(String divCd, Integer clientId, Integer empNo, List<Code> codes) {
        LocalDateTime now = LocalDateTime.now();
        codes.forEach(post -> {
            post.setDivCd(divCd);
            post.setClientId(clientId);

            if (post.getCd() == null) {
                post.setCd(IDGenerator.getUUID());
                post.setRegEmpNo(empNo);
                post.setRegDt(now);
            } else {
                post.setUpdEmpNo(empNo);
                post.setUpdDt(now);
            }

            if (post.getPrntCd() == null) {
                post.setPrntCd("ROOT");
            }

            if (post.getUseYn() == null) {
                post.setUseYn("Y");
            }
        });
    }

    public List<Code> findByDiv(String divCd, Integer clientId) {
        return codeRepository.findByDiv(divCd, clientId);
    }
}
