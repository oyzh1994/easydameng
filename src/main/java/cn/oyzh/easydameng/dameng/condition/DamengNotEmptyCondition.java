package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 非空字符串条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengNotEmptyCondition extends DamengCondition {

    /** 非空字符串条件实例 */
    public final static DamengNotEmptyCondition INSTANCE = new DamengNotEmptyCondition();

    /**
     * 构造非空字符串条件
     */
    public DamengNotEmptyCondition() {
        super(I18nHelper.notIsEmpty(), "!=''", false);
    }

    @Override
    public String wrapCondition(Object condition) {
        return condition == null ? null : condition.toString();
    }
}
