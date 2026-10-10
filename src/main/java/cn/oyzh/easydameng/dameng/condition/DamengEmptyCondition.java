package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 空字符串条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengEmptyCondition extends DamengCondition {

    /** 空字符串条件实例 */
    public final static DamengEmptyCondition INSTANCE = new DamengEmptyCondition();

    /**
     * 构造空字符串条件
     */
    public DamengEmptyCondition() {
        super(I18nHelper.isEmpty(), "=''", false);
    }

    @Override
    public String wrapCondition(Object condition) {
        return condition == null ? null : condition.toString();
    }
}
