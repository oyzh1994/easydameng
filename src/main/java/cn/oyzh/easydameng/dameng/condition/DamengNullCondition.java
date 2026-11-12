package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 是NULL条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengNullCondition extends DamengCondition {

    /** 空值条件实例 */
    public final static DamengNullCondition INSTANCE = new DamengNullCondition();

    /**
     * 构造空值条件
     */
    public DamengNullCondition() {
        super(I18nHelper.isNull(), "IS NULL", false);
    }
}
