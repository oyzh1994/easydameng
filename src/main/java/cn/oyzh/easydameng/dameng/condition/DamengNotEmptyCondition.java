package cn.oyzh.easydameng.dameng.condition;

import cn.oyzh.i18n.I18nHelper;

/**
 * 包含条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengNotEmptyCondition extends DamengCondition {

    public final static DamengNotEmptyCondition INSTANCE = new DamengNotEmptyCondition();

    public DamengNotEmptyCondition() {
        super(I18nHelper.notIsEmpty(), "!=''", false);
    }

}
