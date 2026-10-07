package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 不等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengNotEqCondition extends DamengCondition {

    /** 不等于条件实例 */
    public final static DamengNotEqCondition INSTANCE = new DamengNotEqCondition();

    /**
     * 构造不等于条件
     */
    public DamengNotEqCondition() {
        super(I18nHelper.notEq(), "!=");
    }
}
