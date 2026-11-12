package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.fx.db.DBDialect;
import cn.oyzh.fx.db.util.DBUtil;

import java.util.Collection;

/**
 * 不介于条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class DamengNotBetweenCondition extends DamengBetweenCondition {

    /** 不介于条件实例 */
    public final static DamengNotBetweenCondition INSTANCE = new DamengNotBetweenCondition();

    /**
     * 构造不介于条件
     */
    public DamengNotBetweenCondition() {
        super(I18nHelper.notBetween(), "NOT BETWEEN");
    }
}
