package top.itning.utils.object;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * 对象工具类
 *
 * @author itning
 * @date 2019/7/7 14:06
 */
public class ObjectUtils {
    /**
     * 检查对象中的属性不为null，String不为空
     *
     * @param t                对象
     * @param ignoreFieldsName 忽略属性
     * @param <T>              对象
     */
    public static <T> void checkObjectFieldsNotBlank(T t, String... ignoreFieldsName) {
        checkObjectFieldsNotBlank(t, msg -> new IllegalArgumentException(msg + " is null or blank"), ignoreFieldsName);
    }

    /**
     * 检查对象中的属性不为null，String不为空
     *
     * @param t                对象
     * @param exception        检查到null对象或空字符串时抛出的异常
     * @param ignoreFieldsName 忽略属性
     * @param <T>              对象
     */
    public static <T> void checkObjectFieldsNotBlank(T t, Function<String, ? extends RuntimeException> exception, String... ignoreFieldsName) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            a:
            for (Field field : fields) {
                for (String ignore : ignoreFieldsName) {
                    if (ignore.equals(field.getName())) {
                        continue a;
                    }
                }
                field.setAccessible(true);
                Object o = field.get(t);
                if (null == o) {
                    throw exception.apply(field.getName());
                } else if (o instanceof CharSequence) {
                    if (isBlank((CharSequence) o)) {
                        throw exception.apply(field.getName());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw exception.apply(e.getMessage());
        }
    }

    /**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     */
    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
