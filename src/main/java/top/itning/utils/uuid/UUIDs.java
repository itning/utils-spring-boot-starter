package top.itning.utils.uuid;

import java.util.UUID;

/**
 * UUID 工具
 *
 * @author itning
 * @date 2019/7/7 13:10
 */
public class UUIDs {
    /**
     * 获取不带 - 的UUID
     *
     * @return UUID
     */
    public static String get() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
