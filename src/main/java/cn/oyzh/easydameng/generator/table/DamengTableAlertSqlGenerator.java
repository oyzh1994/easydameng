package cn.oyzh.easydameng.generator.table;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.check.DamengCheck;
import cn.oyzh.easydameng.dameng.check.DamengChecks;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKey;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKeys;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.dameng.index.DamengIndexes;
import cn.oyzh.easydameng.dameng.table.DamengAlertTableParam;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.easydameng.dameng.trigger.DamengTriggers;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.db.DBObjectList;
import cn.oyzh.easydameng.util.DBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengTableAlertSqlGenerator {

    /**
     * 变更标志位
     */
    private boolean changeFlag;

    private List<String> sqlList;

    private StringBuilder sqlBuilder;

    private void _generate(DamengAlertTableParam param) {
        this.sqlList = new ArrayList<>();
        this.sqlBuilder = new StringBuilder();
        DamengTable table = param.getTable();
        String tableFullName = DBUtil.wrap(param.dbName(), param.tableName(), DBDialect.DAMENG);
        // 修改列、主键、索引、外键、检查 — 每个操作生成独立 ALTER TABLE 语句
        if (param.columnChanged()) {
            // StringBuilder builder = new StringBuilder();
            // builder.append("ALTER TABLE ").append(tableFullName).append(" ");
            // int len = builder.length();
            // this.columnHandle(builder, param);
            // if (builder.length() != len) {
            //     this.sqlList.add(builder.toString());
            // }
            // builder.delete(0, builder.length());
            // builder.append("ALTER TABLE ").append(tableFullName).append(" ");
            // len = builder.length();
            // this.columnHandle1(builder, param);
            // if (builder.length() != len) {
            //     this.sqlList.add(builder.toString());
            // }
            // this.columnHandle2(builder, param);
            this.columnHandle(param);
        }
        if (param.primaryKeyChanged()) {
            StringBuilder builder = new StringBuilder();
            builder.append("ALTER TABLE ").append(tableFullName).append(" ");
            int len = builder.length();
            this.primaryKeyHandle(builder, param);
            if (builder.length() != len) {
                this.sqlList.add(builder.toString());
            }
        }
        if (param.hasIndex()) {
            StringBuilder builder = new StringBuilder();
            builder.append("ALTER TABLE ").append(tableFullName).append(" ");
            int len = builder.length();
            this.indexHandle(builder, param);
            if (builder.length() != len) {
                this.sqlList.add(builder.toString());
            }
        }
        if (param.hasForeignKey()) {
            this.foreignKeyHandle(param);
        }
        if (param.hasCheck()) {
            this.checkHandle(param);
        }
        // 达梦表空间修改
        if (table.hasTableSpace()) {
            StringBuilder builder = new StringBuilder();
            builder.append("ALTER TABLE ").append(tableFullName)
                    .append(" MOVE TABLESPACE ").append(table.getTableSpace()).append(";");
            this.sqlList.add(builder.toString());
        }
        // 表触发器
        if (param.hasTrigger()) {
            this.triggerHandle(param);
        }
        // 达梦表注释
        if (table.hasComment()) {
            StringBuilder builder = new StringBuilder();
            builder.append("COMMENT ON TABLE ").append(tableFullName)
                    .append(" IS ").append(DBUtil.wrapData(table.getComment())).append(";");
            this.sqlList.add(builder.toString());
        }
    }

    public List<String> generate(DamengAlertTableParam param) {
        this._generate(param);
        return this.buildSql();
    }

    public String generateSingle(DamengAlertTableParam param) {
        this._generate(param);
        return this.buildSqlSingle();
    }

    private List<String> buildSql() {
        if (!this.sqlBuilder.isEmpty()) {
            this.sqlList.add(this.sqlBuilder.toString().trim());
        }
        return this.sqlList;
    }

    private String buildSqlSingle() {
        StringBuilder builder = new StringBuilder();
        if (!this.sqlBuilder.isEmpty()) {
            builder.append(this.sqlBuilder);
        }
        for (String sql : sqlList) {
            builder.append("\n").append(sql);
        }
        return builder.toString().trim();
    }

    /**
     * 触发器处理
     *
     * @param param 参数
     */
    protected void triggerHandle(DamengAlertTableParam param) {
        DamengTriggers triggers = param.getTriggers();
        // 删除、变更的语句先执行，否则可能异常
        for (DamengTrigger trigger : triggers) {
            if (DamengTriggers.isDeleted(trigger) || DamengTriggers.isChanged(trigger)) {
                StringBuilder builder = new StringBuilder();
                builder.append("DROP TRIGGER ")
                        .append(DBUtil.wrap(trigger.originalName(), DBDialect.DAMENG))
                        .append(";\n");
                this.sqlList.add(builder.toString());
            }
            if (DamengTriggers.isChanged(trigger) || DamengTriggers.isCreated(trigger)) {
                StringBuilder builder = new StringBuilder();
                String body = trigger.getDefinition().trim();
                if (!body.endsWith(";")) {
                    body = body + ";";
                }
                builder.append("CREATE OR REPLACE TRIGGER ")
                        .append(DBUtil.wrap(trigger.getName(), DBDialect.DAMENG))
                        .append(" ")
                        .append(trigger.getPolicy())
                        .append(" ON ")
                        .append(DBUtil.wrap(param.tableName(), DBDialect.DAMENG))
                        .append(" FOR EACH ROW BEGIN ")
                        .append(body)
                        .append(" END;");
                this.sqlList.add(builder.toString());
            }
        }
    }

    // /**
    //  * 字段处理
    //  *
    //  * @param builder 语句
    //  * @param param   参数
    //  */
    // protected void columnHandle(StringBuilder builder, DamengAlertTableParam param) {
    //     for (DamengColumn column : param.getColumns()) {
    //         // 修改或者新增字段
    //         if (DamengColumns.isChanged(column) && column.isNameChanged()) {
    //             // 达梦 RENAME COLUMN 只支持 old TO new，不含类型信息
    //             builder.append(" RENAME COLUMN ")
    //                     .append(DBUtil.wrap(column.originalName(), DBDialect.DAMENG))
    //                     .append(" TO ")
    //                     .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
    //         }
    //     }
    //     // 删除最后一个字符
    //     StringUtil.deleteLast(builder, ",");
    // }
    //
    // /**
    //  * 字段处理1
    //  *
    //  * @param builder 语句
    //  * @param param   参数
    //  */
    // protected void columnHandle1(StringBuilder builder, DamengAlertTableParam param) {
    //     for (DamengColumn column : param.getColumns()) {
    //         // 修改或者新增字段
    //         if (DamengColumns.isChanged(column) || DamengColumns.isCreated(column)) {
    //             if (column.isCreated()) {
    //                 builder.append(" ADD COLUMN ")
    //                         .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
    //                 this.appendColumnType(builder, column, true);
    //             } else {
    //                 builder.append(" MODIFY ")
    //                         .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
    //                 this.appendColumnType(builder, column, false);
    //             }
    //             builder.append(",");
    //         } else if (DamengColumns.isDeleted(column)) {// 删除字段
    //             builder.append(" DROP COLUMN ")
    //                     .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG))
    //                     .append(",");
    //         }
    //     }
    //     // 删除最后一个字符
    //     StringUtil.deleteLast(builder, ",");
    // }
    //
    // /**
    //  * 字段处理2
    //  *
    //  * @param builder 语句
    //  * @param param   参数
    //  */
    // protected void columnHandle2(StringBuilder builder, DamengAlertTableParam param) {
    //     for (DamengColumn column : param.getColumns()) {
    //         // 修改或者新增字段
    //         if (DamengColumns.isChanged(column) || DamengColumns.isCreated(column)) {
    //             // 达梦列注释使用独立COMMENT ON COLUMN语句，ALTER TABLE中跳过
    //             if (column.hasComment() && column.isCommentChanged()) {
    //                 String commentSql = """
    //                         COMMENT ON COLUMN "$1"."$2"."$3" IS '$4';
    //                         """;
    //                 commentSql = commentSql.replace("$1", column.getDbName());
    //                 commentSql = commentSql.replace("$2", column.getTableName());
    //                 commentSql = commentSql.replace("$3", column.getName());
    //                 commentSql = commentSql.replace("$4", column.getComment());
    //                 this.sqlList.add(commentSql);
    //             }
    //         }
    //     }
    //     // 删除最后一个字符
    //     StringUtil.deleteLast(builder, ",");
    // }

    /**
     * 字段处理
     *
     * @param param 参数
     */
    protected void columnHandle(DamengAlertTableParam param) {
        int len;
        StringBuilder builder = new StringBuilder();
        String tableFullName = DBUtil.wrap(param.dbName(), param.tableName(), DBDialect.DAMENG);

        // 字段重命名
        builder.append("ALTER TABLE ").append(tableFullName).append(" ");
        len = builder.length();
        for (DamengColumn column : param.getColumns()) {
            if (DamengColumns.isChanged(column) && column.isNameChanged()) {
                // 达梦 RENAME COLUMN 只支持 old TO new，不含类型信息
                builder.append(" RENAME COLUMN ")
                        .append(DBUtil.wrap(column.originalName(), DBDialect.DAMENG))
                        .append(" TO ")
                        .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG))
                        .append(",\n");
            }
        }
        // 删除最后一个字符
        StringUtil.deleteLast(builder, ",");
        StringUtil.deleteLast(builder, "\n");
        if (len != builder.length()) {
            builder.append(";");
            this.sqlList.add(builder.toString());
        }

        // 新增、删除字段
        StringUtil.clear(builder);
        builder.append("ALTER TABLE ").append(tableFullName).append(" ");
        len = builder.length();
        for (DamengColumn column : param.getColumns()) {
            if (DamengColumns.isCreated(column)) {
                builder.append(" ADD COLUMN ")
                        .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
                this.appendColumnType(builder, column, true);
                builder.append(",\n");
            } else if (DamengColumns.isDeleted(column)) {// 删除字段
                builder.append(" DROP COLUMN ")
                        .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG))
                        .append(",\n");
            }
        }
        // 删除最后一个字符
        StringUtil.deleteLast(builder, ",");
        StringUtil.deleteLast(builder, "\n");
        if (len != builder.length()) {
            builder.append(";");
            this.sqlList.add(builder.toString());
        }

        // 修改字段
        for (DamengColumn column : param.getColumns()) {
            if (DamengColumns.isChanged(column)) {
                StringUtil.clear(builder);
                if (param.isExistPrimaryKey()) {
                    builder.append("ALTER TABLE ")
                            .append(tableFullName)
                            .append(" DROP IDENTITY;");
                    this.sqlList.add(builder.toString());
                }
                StringUtil.clear(builder);
                builder.append("ALTER TABLE ")
                        .append(tableFullName)
                        .append(" MODIFY ")
                        .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
                this.appendColumnType(builder, column, false);
                builder.append(";");
                this.sqlList.add(builder.toString());
                StringUtil.clear(builder);
                if (column.isPrimaryKey()) {
                    builder.append("ALTER TABLE ")
                            .append(tableFullName)
                            .append(" ADD COLUMN ")
                            .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG))
                            .append(" IDENTITY(1, 1);");
                    this.sqlList.add(builder.toString());
                }
            }
        }

        // 注释
        for (DamengColumn column : param.getColumns()) {
            if (DamengColumns.isChanged(column) || DamengColumns.isCreated(column)) {
                if (column.hasComment() && column.isCommentChanged()) {
                    String commentSql = """
                            COMMENT ON COLUMN "$1"."$2"."$3" IS '$4';;
                            """;
                    commentSql = commentSql.replace("$1", column.getDbName());
                    commentSql = commentSql.replace("$2", column.getTableName());
                    commentSql = commentSql.replace("$3", column.getName());
                    commentSql = commentSql.replace("$4", column.getComment());
                    this.sqlList.add(commentSql);
                }
            }
        }
    }

    protected void primaryKeyHandle(StringBuilder builder, DamengAlertTableParam table) {
        if (table.isExistPrimaryKey()) {
            builder.append(" DROP PRIMARY KEY,");
        }
        List<DamengColumn> keyList = table.primaryKeys();
        if (!keyList.isEmpty()) {
            builder.append(" ADD PRIMARY KEY (");
            for (DamengColumn column : keyList) {
                builder.append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
                if (column.supportKeySize()) {
                    if (column.getPrimaryKeySize() != null) {
                        builder.append("(").append(column.getPrimaryKeySize()).append(")");
                    } else if (column.getSize() != null) {
                        builder.append("(").append(Math.min(column.getSize(), 100)).append(")");
                    } else {
                        builder.append("(").append(100).append(")");
                    }
                }
                builder.append(",");
            }
            // 删除最后一个字符
            StringUtil.deleteLast(builder, ",");
            builder.append("),");
        }
        // 删除最后一个字符
        StringUtil.deleteLast(builder, ",");
    }

    protected void indexHandle(StringBuilder builder, DamengAlertTableParam param) {
        DamengIndexes indexes = param.getIndexes();
        String tableFullName = DBUtil.wrap(param.dbName(), param.tableName(), DBDialect.DAMENG);
        for (DamengIndex index : indexes) {
            // 索引删除用独立DROP INDEX语句（达梦不支持 ALTER TABLE DROP INDEX）
            if (DamengIndexes.isDeleted(index) || DamengIndexes.isChanged(index)) {
                this.sqlList.add("DROP INDEX " + DBUtil.wrap(index.originalName(), DBDialect.DAMENG) + ";");
            }
            // 索引新增用独立CREATE INDEX语句
            if (DamengIndexes.isCreated(index) || DamengIndexes.isChanged(index)) {
                StringBuilder idxBuilder = new StringBuilder("CREATE ");
                // 达梦不支持 FULLTEXT/SPATIAL，仅保留 UNIQUE
                if (index.typeName() != null && !"FULLTEXT".equalsIgnoreCase(index.typeName()) && !"SPATIAL".equalsIgnoreCase(index.typeName())) {
                    idxBuilder.append(index.typeName()).append(" ");
                }
                idxBuilder.append("INDEX ")
                        .append(DBUtil.wrap(index.getName(), DBDialect.DAMENG))
                        .append(" ON ").append(tableFullName)
                        .append(" (");
                for (DamengIndex.IndexColumn column : index.getColumns()) {
                    idxBuilder.append(DBUtil.wrap(column.getColumnName(), DBDialect.DAMENG));
                    idxBuilder.append(",");
                }
                StringUtil.deleteLast(idxBuilder);
                idxBuilder.append(")");
                this.sqlList.add(idxBuilder.toString() + ";");
            }
        }
    }

    /**
     * 添加外键
     *
     * @param param 参数
     */
    protected void foreignKeyHandle(DamengAlertTableParam param) {
        int len;
        DamengForeignKeys foreignKeys = param.getForeignKeys();
        StringBuilder builder = new StringBuilder();
        String tableFullName = DBUtil.wrap(param.dbName(), param.tableName(), DBDialect.DAMENG);

        // 删除外键
        for (DamengForeignKey foreignKey : foreignKeys) {
            if (DBObjectList.isDeleted(foreignKey) || DBObjectList.isChanged(foreignKey)) {
                String fkName = foreignKey.originalName();
                // 名称为null是临时数据
                if (StringUtil.isNotBlank(fkName)) {
                    StringUtil.clear(builder);
                    builder.append("ALTER TABLE ").append(tableFullName).append(" ");
                    builder.append(" DROP CONSTRAINT ")
                            .append(DBUtil.wrap(foreignKey.originalName(), DBDialect.DAMENG))
                            .append(";");
                    this.sqlList.add(builder.toString());
                }
            }
        }

        // 新增外键
        StringUtil.clear(builder);
        builder.append("ALTER TABLE ").append(tableFullName).append(" ");
        len = builder.length();
        for (DamengForeignKey foreignKey : foreignKeys) {
            if (DBObjectList.isCreated(foreignKey) || DBObjectList.isChanged(foreignKey)) {
                // 新增外键
                builder.append(" ADD CONSTRAINT ")
                        .append(DBUtil.wrap(foreignKey.getName(), DBDialect.DAMENG))
                        .append(" FOREIGN KEY (");
                for (String column : foreignKey.getColumns()) {
                    builder.append(DBUtil.wrap(column, DBDialect.DAMENG)).append(",");
                }
                StringUtil.deleteLast(builder, ",");
                builder.append(")")
                        .append(" REFERENCES ")
                        .append(DBUtil.wrap(foreignKey.getPrimaryKeyDatabase(), foreignKey.getPrimaryKeyTable(), DBDialect.DAMENG))
                        .append(" (");
                for (String column : foreignKey.getPrimaryKeyColumns()) {
                    builder.append(DBUtil.wrap(column, DBDialect.DAMENG)).append(",");
                }
                StringUtil.deleteLast(builder, ",");
                builder.append(")")
                        .append(" ON DELETE ").append(foreignKey.getDeletePolicy())
                        .append(" ON UPDATE ").append(foreignKey.getUpdatePolicy());
                // 拼接,
                builder.append(",\n");
                this.changeFlag = true;
            }
        }
        StringUtil.deleteLast(builder, ",");
        StringUtil.deleteLast(builder, "\n");
        if (len != builder.length()) {
            builder.append(";");
            this.sqlList.add(builder.toString());
        }
    }

    // /**
    //  * 添加外键
    //  *
    //  * @param param 参数
    //  */
    // protected void foreignKeyHandle1(DamengAlertTableParam param) {
    //     StringBuilder builder = new StringBuilder("ALTER TABLE ");
    //     String tableFullName = DBUtil.wrap(param.dbName(), param.tableName(), DBDialect.DAMENG);
    //     builder.append("ALTER TABLE ").append(tableFullName).append(" ");
    //     for (DamengForeignKey foreignKey : param.getForeignKeys()) {
    //         if (foreignKey.isCreated() || foreignKey.isChanged()) {
    //             // 新增外键
    //             builder.append(" ADD CONSTRAINT ")
    //                     .append(DBUtil.wrap(foreignKey.getName(), DBDialect.DAMENG))
    //                     .append(" FOREIGN KEY (");
    //             for (String column : foreignKey.getColumns()) {
    //                 builder.append(DBUtil.wrap(column, DBDialect.DAMENG)).append(",");
    //             }
    //             StringUtil.deleteLast(builder, ",");
    //             builder.append(")")
    //                     .append(" REFERENCES ")
    //                     .append(DBUtil.wrap(foreignKey.getPrimaryKeyDatabase(), foreignKey.getPrimaryKeyTable(), DBDialect.DAMENG))
    //                     .append(" (");
    //             for (String column : foreignKey.getPrimaryKeyColumns()) {
    //                 builder.append(DBUtil.wrap(column, DBDialect.DAMENG)).append(",");
    //             }
    //             StringUtil.deleteLast(builder, ",");
    //             builder.append(")")
    //                     .append(" ON DELETE ").append(foreignKey.getDeletePolicy())
    //                     .append(" ON UPDATE ").append(foreignKey.getUpdatePolicy());
    //             // 拼接,
    //             builder.append(",\n");
    //             this.changeFlag = true;
    //         }
    //     }
    //     StringUtil.deleteLast(builder, ",");
    //     builder.append(";");
    //     if (!builder.isEmpty()) {
    //         this.sqlList.add(builder.toString());
    //     }
    // }
    //
    // /**
    //  * 删除外键
    //  *
    //  * @param param 参数
    //  */
    // protected void foreignKeyHandle2(DamengAlertTableParam param) {
    //     DamengForeignKeys foreignKeys = param.getForeignKeys();
    //     if (!foreignKeys.hasChanged() && !foreignKeys.hasDeleted()) {
    //         return;
    //     }
    //     StringBuilder builder = new StringBuilder();
    //     builder.append("ALTER TABLE ")
    //             .append(DBUtil.wrap(param.dbName(), param.tableName(), DBDialect.DAMENG));
    //     for (DamengForeignKey foreignKey : foreignKeys.filterList(DBObjectList.TYPE_DELETED, DBObjectList.TYPE_CHANGED)) {
    //         String fkName = foreignKey.originalName();
    //         // 名称为null是临时数据
    //         if (StringUtil.isNotBlank(fkName)) {
    //             builder.append(" DROP CONSTRAINT ")
    //                     .append(DBUtil.wrap(foreignKey.originalName(), DBDialect.DAMENG))
    //                     .append(",\n");
    //         }
    //     }
    //     StringUtil.deleteLast(builder, ",");
    //     StringUtil.deleteLast(builder, "\n");
    //     builder.append(";");
    //     this.sqlList.add(builder.toString());
    // }

    /**
     * 检查处理
     * @param param 参数
     */
    protected void checkHandle(DamengAlertTableParam param) {
        int len;
        StringBuilder builder = new StringBuilder();
        String tableFullName = DBUtil.wrap(param.dbName(), param.tableName(), DBDialect.DAMENG);
        DamengChecks checks = param.getChecks();

        // 删除检查
        for (DamengCheck check : checks) {
            if (DBObjectList.isDeleted(check) || DBObjectList.isChanged(check)) {
                StringUtil.clear(builder);
                builder.append("ALTER TABLE ").append(tableFullName).append(" ");
                builder.append("DROP CONSTRAINT ")
                        .append(DBUtil.wrap(check.originalName(), DBDialect.DAMENG))
                        .append(";");
                this.sqlList.add(builder.toString());
                this.changeFlag = true;
            }
        }

        // 新增、编辑检查
        StringUtil.clear(builder);
        builder.append("ALTER TABLE ").append(tableFullName).append(" ");
        len = builder.length();
        for (DamengCheck check : checks) {
            if (DamengChecks.isCreated(check) || DamengChecks.isChanged(check)) {
                builder.append(" ADD CONSTRAINT ")
                        .append(DBUtil.wrap(check.getName(), DBDialect.DAMENG))
                        .append(" CHECK (")
                        .append(check.getClause())
                        .append(")");
                // 拼接,
                builder.append(",\n");
                this.changeFlag = true;
            }
        }
        StringUtil.deleteLast(builder, ",");
        StringUtil.deleteLast(builder, "\n");
        if (len != builder.length()) {
            builder.append(";");
            this.sqlList.add(builder.toString());
        }
    }

    /**
     * 追加列类型定义（类型、长度、默认值、NULL、IDENTITY等）
     */
    private void appendColumnType(StringBuilder builder, DamengColumn column, boolean created) {
        builder.append(" ").append(normalizeType(column.getType()));
        if (column.supportSize() && column.getSize() != null) {
            builder.append("(").append(column.getSize());
            if (column.supportDigits() && column.getDigits() != null) {
                builder.append(",").append(column.getDigits());
            }
            builder.append(")");
        } else if (column.supportValue() && column.getValue() != null) {
            builder.append("(").append(column.getValue()).append(")");
        }
        boolean isIdentity = column.supportAutoIncrement() && column.isAutoIncrement();
        if (!isIdentity && column.supportDefaultValue() && column.getDefaultValue() != null) {
            builder.append(" DEFAULT ").append(DBUtil.wrapData(column.getDefaultValueString()));
        }
        if (column.isNullable()) {
            builder.append(" NULL");
        } else {
            builder.append(" NOT NULL");
        }
        if (isIdentity && created) {
            builder.append(" IDENTITY(1,1)");
        }
    }

    /**
     * 规范化字段类型
     */
    private static String normalizeType(String type) {
        if (type == null)
            return "";
        String upper = type.toUpperCase();
        if (upper.matches("(TINYINT|SMALLINT|INT|INTEGER|BIGINT|MEDIUMINT)\\s*\\(\\s*\\d+\\s*\\)")) {
            return upper.replaceAll("\\(.*\\)", "").trim();
        }
        return type;
    }

    public static List<String> generateSql(DamengAlertTableParam param) {
        return new DamengTableAlertSqlGenerator().generate(param);
    }

    public static String generateSqlSingle(DamengAlertTableParam param) {
        return new DamengTableAlertSqlGenerator().generateSingle(param);
    }

}
