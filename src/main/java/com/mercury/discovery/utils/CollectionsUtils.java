package com.mercury.discovery.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class CollectionsUtils {
    private CollectionsUtils() {
        //prevent new
    }

    /**
     * User resultUser = users.stream().filter(user -> user.getId() > 0).collect(toSingleton());
     * */
    public static <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                int size = list.size();
                if(size == 0){
                    return null;
                }else if (size != 1) {
                    throw new IllegalStateException("multiple elements in list");
                }
                return list.get(0);
            }
        );
    }


    public static <T> Page<T> transToPage(List<T> list) {
        return transToPage(list, null);
    }

    public static <T> Page<T> transToPage(List<T> list, Pageable pageable) {
        return transToPage(list, pageable, -1);
    }

    public static <T> Page<T> transToPage(List<T> list, Pageable pageable, int totalCount) {
        int listSize = list.size();
        if (pageable == null) {
            pageable = PageRequest.of(0, listSize);
        }

        if (totalCount == -1) {
            totalCount = listSize;
        }

        return new PageImpl<>(list, pageable, totalCount);
    }



    public static <T> T findOne(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(toSingleton());
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");list.add("b");list.add("c");

        String a = findOne(list, (s-> s.equals("d")));
        System.out.println(a);
    }


    /**
     * {
     *     key1:value1,
     *     key2:value2
     * }
     */
    public static Map<String, Object> extractKeyValue(List<?> list, String key, String value) {
        Map<String, Object> result = new HashMap<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        for (Object obj : list) {
            result.put(getValue(key, obj).toString(), getValue(value, obj));
        }

        return result;
    }


    /**
     * 해당 리스트에 포함되어 있는 객체에서 keys로 입력한 필드의 값만을 뽑아낸다.
     * 불필요한 데이터를 화면에 넘길 필요가 없는 경우 이 메서드를 이용해 사용할 데이터만 넘기려고 할때 사용한다.
     * [
     *     {
     *         keys1:"",
     *         keys2:""
     *     },
     *     {
     *         keys1:"",
     *         keys2:""
     *     }
     * ]
     */
    public static List<Map<String, Object>> extractKeysToList(List<?> list, String... keys) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        for (Object obj : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (String key : keys) {
                map.put(key, getValue(key, obj).toString());
            }
            result.add(map);
        }

        return result;
    }



    /**
     * {
     *     "mapKey1":{
     *         key1:"",
     *         key2:""
     *     },
     *     "mapKey2":{
     *         key1:"",
     *         key2:""
     *     }
     * }
     */
    public static Map<String, Map<String, Object>> extractKeysToMap(List<?> list, String mapKey, String... keys) {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        for (Object obj : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (String key : keys) {
                map.put(key, getValue(key, obj).toString());
            }

            result.put(getValue(mapKey, obj).toString(), map);
        }

        return result;
    }

    public static <T> Map<String, T> transToMapByKey(List<T> list, String mapKey) {
        Map<String, T> result = new LinkedHashMap<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        for (T obj : list) {
            result.put(getValue(mapKey, obj).toString(), obj);
        }

        return result;
    }



    private static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static Object getValue(String key, Object from) {
        Class<?> clazz = from.getClass();
        try {
            if (from instanceof Map) {
                return ((Map) from).get(key);
            } else {
                return clazz.getMethod("get" + capitalizeFirstLetter(key)).invoke(from);
            }
        } catch (NoSuchMethodException e) {
            throw new DataTransformException("NoSuchMethodException " + clazz.getCanonicalName() + " " + key);
        } catch (IllegalAccessException e) {
            throw new DataTransformException("IllegalAccessException " + clazz.getCanonicalName() + " " + key);
        } catch (InvocationTargetException e) {
            throw new DataTransformException("InvocationTargetException " + clazz.getCanonicalName() + " " + key);
        }
    }

    private static class DataTransformException extends RuntimeException {
        DataTransformException(String message) {
            super(message);
        }
    }
}
