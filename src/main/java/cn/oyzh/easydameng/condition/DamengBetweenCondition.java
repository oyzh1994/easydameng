package cn.oyzh.easydameng.condition;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.util.DBUtil;

import java.util.Collection;

/**
 * 介于条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class DamengBetweenCondition extends DamengCondition {

    public final static DamengBetweenCondition INSTANCE = new DamengBetweenCondition();

    public DamengBetweenCondition() {
        super("介于", "BETWEEN");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition instanceof Object[] arr) {
            return this.getValue() + " " + DBUtil.wrapData(arr[0]) + " AND " + DBUtil.wrapData(arr[1]);
        }
        if (condition instanceof Collection coll) {
            return this.getValue() + " " + DBUtil.wrapData(CollectionUtil.get(coll, 0)) + " AND " + DBUtil.wrapData(CollectionUtil.get(coll, 1));
        }
        return super.wrapCondition(condition);
    }
}
