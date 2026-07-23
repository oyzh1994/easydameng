package cn.oyzh.easydameng.condition;

/**
 * 大于等于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengGtEqCondition extends DamengCondition {

    public final static DamengGtEqCondition INSTANCE = new DamengGtEqCondition();

    public DamengGtEqCondition() {
        super("大于等于", ">=");
    }
}
