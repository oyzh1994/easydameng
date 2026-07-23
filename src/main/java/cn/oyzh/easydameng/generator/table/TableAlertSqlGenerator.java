// package cn.oyzh.easydameng.generator.table;
//
// import cn.oyzh.easydameng.db.DBDialect;
// import cn.oyzh.easydameng.db.table.DamengTable;
// import cn.oyzh.easydameng.db.table.DamengAlertTableParam;
// import lombok.Getter;
//
// /**
//  *
//  * @author oyzh
//  * @since 2024/01/25
//  */
// public abstract class TableAlertSqlGenerator {
//
//     @Getter
//     private DBDialect dialect;
//
//     public TableAlertSqlGenerator(DBDialect dialect) {
//         this.dialect = dialect;
//     }
//
//     public abstract String generate(DamengAlertTableParam table);
//
//     public static String generate(DBDialect dialect, DamengAlertTableParam table) {
//         return switch (dialect) {
//             case MYSQL -> new DamengTableAlertSqlGenerator().generate(table);
//             default -> null;
//         };
//     }
// }
