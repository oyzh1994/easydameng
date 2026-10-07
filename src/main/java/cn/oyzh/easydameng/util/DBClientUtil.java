package cn.oyzh.easydameng.util;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.fx.db.DBDialect;

/**
 * 达梦数据库客户端工具类。
 *
 * @author oyzh
 * @since 2020/6/8
 */
public class DBClientUtil {

    /**
     * 根据连接信息创建客户端
     *
     * @param info 连接信息
     * @return 客户端实例，不支持的连接类型时返回 null
     */
    public static ShellDamengClient newClient(DamengConnect info) {
        if (StringUtil.isBlank(info.getType()) || DBDialect.valueOf(info.getType()) == DBDialect.DAMENG) {
            return new ShellDamengClient(info);
        }
        if (DBDialect.valueOf(info.getType()) == DBDialect.DAMENG) {
            return new ShellDamengClient(info);
        }
        return null;
    }

}
