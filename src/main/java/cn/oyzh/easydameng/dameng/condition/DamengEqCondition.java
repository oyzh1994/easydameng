package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengEqCondition extends DamengCondition {

    /** 等于条件实例 */
    public final static DamengEqCondition INSTANCE = new DamengEqCondition();

    /**
     * 构造等于条件
     */
    public DamengEqCondition() {
        super(I18nHelper.eq(), "=");
    }
}
