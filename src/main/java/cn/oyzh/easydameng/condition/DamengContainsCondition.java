package cn.oyzh.easydameng.condition;

/**
 * 包含条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengContainsCondition extends DamengCondition {

    public final static DamengContainsCondition INSTANCE = new DamengContainsCondition();

    public DamengContainsCondition() {
        super("包含", "LIKE");
    }

    @Override
    public String wrapCondition(Object condition) {
        if (condition != null) {
            return super.wrapCondition("%" + condition + "%");
        }
        return super.wrapCondition(condition);
    }
}
