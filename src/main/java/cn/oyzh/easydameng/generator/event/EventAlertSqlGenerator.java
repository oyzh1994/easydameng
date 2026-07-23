package cn.oyzh.easydameng.generator.event;

import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.dameng.event.DamengEvent;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public abstract class EventAlertSqlGenerator {

    private DBDialect dialect;

    public EventAlertSqlGenerator(DBDialect dialect) {
        this.dialect = dialect;
    }

    public abstract String generate(DamengEvent event);

    public static String generate(DBDialect dialect, DamengEvent event) {
        return switch (dialect) {
            case DAMENG -> new DamengEventAlertSqlGenerator().generate(event);
            default -> null;
        };
    }

    public DBDialect getDialect() {
        return dialect;
    }

    public void setDialect(DBDialect dialect) {
        this.dialect = dialect;
    }
}
