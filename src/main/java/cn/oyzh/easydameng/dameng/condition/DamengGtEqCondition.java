package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 大于等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengGtEqCondition extends DamengCondition {

    /** 大于等于条件实例 */
    public final static DamengGtEqCondition INSTANCE = new DamengGtEqCondition();

    /**
     * 构造大于等于条件
     */
    public DamengGtEqCondition() {
        super(I18nHelper.gtEq(), ">=");
    }
}
