package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 大于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengGtCondition extends DamengCondition {

    public final static DamengGtCondition INSTANCE = new DamengGtCondition();

    public DamengGtCondition() {
        super(I18nHelper.gt(), ">");
    }
}
