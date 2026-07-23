package cn.oyzh.easydameng.condition;

/**
 * 包含条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class DamengEmptyCondition extends DamengCondition {

    public final static DamengEmptyCondition INSTANCE = new DamengEmptyCondition();

    public DamengEmptyCondition() {
        super("是空的", "=''", false);
    }

}
