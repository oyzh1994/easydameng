package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 大于等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengGtEqCondition extends DamengCondition {

    public final static DamengGtEqCondition INSTANCE = new DamengGtEqCondition();

    public DamengGtEqCondition() {
        super(I18nHelper.gtEq(), ">=");
    }
}
