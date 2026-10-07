package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 小于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengLtCondition extends DamengCondition {

    /** 小于条件实例 */
    public final static DamengLtCondition INSTANCE = new DamengLtCondition();

    /**
     * 构造小于条件
     */
    public DamengLtCondition() {
        super(I18nHelper.lt(), "<");
    }
}
