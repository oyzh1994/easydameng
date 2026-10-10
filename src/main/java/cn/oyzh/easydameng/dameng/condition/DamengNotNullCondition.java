package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 不是NULL条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengNotNullCondition extends DamengCondition {

    /** 非空值条件实例 */
    public final static DamengNotNullCondition INSTANCE = new DamengNotNullCondition();

    /**
     * 构造非空值条件
     */
    public DamengNotNullCondition() {
        super(I18nHelper.notIsNull(), "IS NOT NULL", false);
    }

    @Override
    public String wrapCondition(Object condition) {
        return condition == null ? null : condition.toString();
    }
}
