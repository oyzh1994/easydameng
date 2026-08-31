package cn.oyzh.easydameng.condition;

import cn.oyzh.easydameng.data.db.DBDialect;
import cn.oyzh.easydameng.util.DBUtil;

/**
 * 在列表条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class DamengInListCondition extends DamengCondition {

    public final static DamengInListCondition INSTANCE = new DamengInListCondition();

    public DamengInListCondition() {
        super("在列表", "IN");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return this.getValue() + " (" + DBUtil.wrapData(condition, DBDialect.DAMENG) + ")";
        }
        return super.wrapCondition(condition);
    }
}
