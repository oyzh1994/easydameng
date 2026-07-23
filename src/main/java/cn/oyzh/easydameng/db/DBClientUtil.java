package cn.oyzh.easydameng.db;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.dameng.DamengClient;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2020/6/8
 */
public class DBClientUtil {

    public static DamengClient newClient(DamengConnect info) {
        if (StringUtil.isBlank(info.getType()) || DBDialect.valueOf(info.getType()) == DBDialect.MYSQL) {
            return new DamengClient(info);
        }
        if (DBDialect.valueOf(info.getType()) == DBDialect.DAMENG) {
            return new DamengClient(info);
        }
        return null;
    }

}
