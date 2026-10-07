package cn.oyzh.easydameng.util;

import cn.oyzh.common.system.ProcessUtil;
import cn.oyzh.fx.plus.window.StageManager;

import java.io.IOException;

/**
 * 达梦数据库客户端进程工具。
 *
 * @author oyzh
 * @since 2024-12-18
 */
public class DamengProcessUtil {

    /**
     * 重启应用
     */
    public static void restartApplication() {
        try {
            ProcessUtil.restartApplication(100, StageManager::exit);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
