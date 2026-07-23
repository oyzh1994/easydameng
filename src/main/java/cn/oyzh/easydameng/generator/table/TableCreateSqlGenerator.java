// package cn.oyzh.easydameng.generator.table;
//
// import cn.oyzh.easydameng.db.DBDialect;
// import cn.oyzh.easydameng.db.table.DamengTable;
// import lombok.Getter;
//
// /**
//  * @author oyzh
//  * @since 2024/01/29
//  */
// public abstract class TableCreateSqlGenerator {
//
//     @Getter
//     private DBDialect dialect;
//
//     public TableCreateSqlGenerator(DBDialect dialect) {
//         this.dialect = dialect;
//     }
//
//     public abstract String generate(DamengTable table);
//
//     public static String generate(DBDialect dialect, DamengTable table) {
//         return switch (dialect) {
//             case MYSQL -> new DamengTableCreateSqlGenerator().generate(table);
//             default -> null;
//         };
//     }
// }
