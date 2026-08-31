package cn.oyzh.easydameng.condition;

import cn.oyzh.easydameng.data.db.DBDialect;
import cn.oyzh.easydameng.util.DBUtil;

/**
 * 不在列表条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class DamengNotInListCondition extends DamengCondition {

    public final static DamengNotInListCondition INSTANCE = new DamengNotInListCondition();

    public DamengNotInListCondition() {
        super("不在列表", "NOT IN");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return this.getValue() + " (" + DBUtil.wrapData(condition, DBDialect.DAMENG) + ")";
        }
        return super.wrapCondition(condition);
    }
}
