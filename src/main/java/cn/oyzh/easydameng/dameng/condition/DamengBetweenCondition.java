package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.fx.db.DBDialect;
import cn.oyzh.fx.db.util.DBUtil;

import java.util.Collection;

/**
 * 介于条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class DamengBetweenCondition extends DamengCondition {

    /** 介于条件实例 */
    public final static DamengBetweenCondition INSTANCE = new DamengBetweenCondition();

    /**
     * 构造介于条件
     */
    public DamengBetweenCondition() {
        super(I18nHelper.between(), "BETWEEN");
    }

    @Override
    public String wrapCondition(Object condition) {
        return super.wrapBetweenCondition(condition);
    }
}
