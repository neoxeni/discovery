package com.mercury.discovery.common.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

/**
 * mybatis에 mybatis-config.xml 에 mapUnderscoreToCamelCase 속성은
 * 자바빈즈(DTO/VO)만 적용되고 컬렉션속성(map)은 적용되지 않는다.
 * <p>
 * https://ini8262.tistory.com/97
 */
@Slf4j
public class CamelMap extends DataMap {
    private static final long serialVersionUID = -7700790403928325865L;

    public static String convertUnderscoreNameToPropertyName(@Nullable String name) {
        StringBuilder result = new StringBuilder();
        boolean nextIsUpper = false;
        if (name != null && name.length() > 0) {
            if (name.length() > 1 && name.charAt(1) == '_') {
                result.append(Character.toUpperCase(name.charAt(0)));
            } else {
                result.append(Character.toLowerCase(name.charAt(0)));
            }

            for (int i = 1; i < name.length(); ++i) {
                char c = name.charAt(i);
                if (c == '_') {
                    nextIsUpper = true;
                } else if (nextIsUpper) {
                    result.append(Character.toUpperCase(c));
                    nextIsUpper = false;
                } else {
                    //result.append(Character.toLowerCase(c));//createdAt과 같은경우 createdat 으로 변경되어버림
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    @Override
    public Object put(Object key, Object value) {
        String strKey = (String) key;
        if (strKey != null) {//Oracle은 무조건 대문자로 넘어 온다..
            strKey = strKey.toLowerCase();
        }

        return super.put(convertUnderscoreNameToPropertyName(strKey), value);
    }
}
