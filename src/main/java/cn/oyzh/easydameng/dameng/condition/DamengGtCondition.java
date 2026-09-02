package cn.oyzh.easydameng.dameng.condition;

/**
 * 大于条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengGtCondition extends DamengCondition {

    public final static DamengGtCondition INSTANCE = new DamengGtCondition();

    public DamengGtCondition() {
        super("大于", ">");
    }
}
