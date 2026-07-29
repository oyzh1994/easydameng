package cn.oyzh.easydameng.generator.event;

import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.db.DBDialect;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public abstract class EventCreateSqlGenerator {

    //    private DBDialect dialect;

//    public EventCreateSqlGenerator(DBDialect dialect) {
//        this.dialect = dialect;
//    }

    public abstract String generate(DamengEvent event);

    public static String generateSql(DamengEvent event) {
        return new DamengEventCreateSqlGenerator().generate(event);
    }
    //
    //    public DBDialect getDialect() {
    //        return dialect;
    //    }
    //
    //    public void setDialect(DBDialect dialect) {
    //        this.dialect = dialect;
    //    }
}
