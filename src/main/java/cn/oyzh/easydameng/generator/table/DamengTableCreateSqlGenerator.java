package cn.oyzh.easydameng.generator.table;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.check.DamengCheck;
import cn.oyzh.easydameng.dameng.check.DamengChecks;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKey;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKeys;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.dameng.index.DamengIndexes;
import cn.oyzh.easydameng.dameng.table.DamengCreateTableParam;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.easydameng.util.DBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 达梦建表SQL生成器
 *
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengTableCreateSqlGenerator {

    private List<String> sqlList;

    private StringBuilder sqlBuilder;

    public String _generate(DamengCreateTableParam param) {
        this.sqlList = new ArrayList<>();
        this.sqlBuilder = new StringBuilder();
        String dbName = param.dbName();
        String tableName = param.tableName();
        DamengTable table = param.getTable();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ")
                .append(DBUtil.wrap(dbName, tableName))
                .append(" ( ");
        // 字段
        if (param.hasColumns()) {
            this.columnHandle(builder, param);
        }
        // 主键
        this.primaryKeyHandle(builder, param);
        // 索引
        if (param.hasIndex()) {
            this.indexHandle(builder, param);
        }
        // 外键
        if (param.hasForeignKey()) {
            this.foreignKeyHandle(builder, param);
        }
        // 检查
        if (param.hasCheck()) {
            this.checkHandle(builder, param);
        }
        builder.append(" )");
        builder.append(";");
        // 表注释（达梦独立语句）
        if (table.hasComment()) {
            builder.append("\nCOMMENT ON TABLE ")
                    .append(DBUtil.wrap(dbName, tableName))
                    .append(" IS ").append(DBUtil.wrapData(table.getComment()))
                    .append(";");
        }
        // 表触发器
        if (param.hasTrigger()) {
            this.triggerHandle(builder, param);
        }
        String sql = builder.toString();
        sql = sql.replaceAll(",\\)", ")");
        sql = sql.replaceAll(", \\)", ")");
        sql = sql.replaceAll(",;", ";");
        return sql;
    }

    public String generate(DamengCreateTableParam param) {
        String dbName = param.dbName();
        DamengTable table = param.getTable();
        String tableName = param.tableName();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ")
                .append(DBUtil.wrap(dbName, tableName))
                .append(" ( ");
        // 字段
        if (param.hasColumns()) {
            this.columnHandle(builder, param);
        }
        // 主键
        this.primaryKeyHandle(builder, param);
        // 索引
        if (param.hasIndex()) {
            this.indexHandle(builder, param);
        }
        // 外键
        if (param.hasForeignKey()) {
            this.foreignKeyHandle(builder, param);
        }
        // 检查
        if (param.hasCheck()) {
            this.checkHandle(builder, param);
        }
        builder.append(" )");
        builder.append(";");
        // 表注释（达梦独立语句）
        if (table.hasComment()) {
            builder.append("\nCOMMENT ON TABLE ")
                    .append(DBUtil.wrap(dbName, tableName))
                    .append(" IS ").append(DBUtil.wrapData(table.getComment()))
                    .append(";");
        }
        // 表触发器
        if (param.hasTrigger()) {
            this.triggerHandle(builder, param);
        }
        String sql = builder.toString();
        sql = sql.replaceAll(",\\)", ")");
        sql = sql.replaceAll(", \\)", ")");
        sql = sql.replaceAll(",;", ";");
        return sql;
    }
    public String generateSingle(DamengCreateTableParam param) {
        String dbName = param.dbName();
        DamengTable table = param.getTable();
        String tableName = param.tableName();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ")
                .append(DBUtil.wrap(dbName, tableName))
                .append(" ( ");
        // 字段
        if (param.hasColumns()) {
            this.columnHandle(builder, param);
        }
        // 主键
        this.primaryKeyHandle(builder, param);
        // 索引
        if (param.hasIndex()) {
            this.indexHandle(builder, param);
        }
        // 外键
        if (param.hasForeignKey()) {
            this.foreignKeyHandle(builder, param);
        }
        // 检查
        if (param.hasCheck()) {
            this.checkHandle(builder, param);
        }
        builder.append(" )");
        builder.append(";");
        // 表注释（达梦独立语句）
        if (table.hasComment()) {
            builder.append("\nCOMMENT ON TABLE ")
                    .append(DBUtil.wrap(dbName, tableName))
                    .append(" IS ").append(DBUtil.wrapData(table.getComment()))
                    .append(";");
        }
        // 表触发器
        if (param.hasTrigger()) {
            this.triggerHandle(builder, param);
        }
        String sql = builder.toString();
        sql = sql.replaceAll(",\\)", ")");
        sql = sql.replaceAll(", \\)", ")");
        sql = sql.replaceAll(",;", ";");
        return sql;
    }

    protected void triggerHandle(StringBuilder builder, DamengCreateTableParam param) {
        for (DamengTrigger trigger : param.getTriggers()) {
            builder.append("CREATE OR REPLACE TRIGGER ")
                    .append(DBUtil.wrap(trigger.getName()))
                    .append(" ")
                    .append(trigger.getPolicy())
                    .append(" ON ")
                    .append(DBUtil.wrap(param.tableName()))
                    .append(" FOR EACH ROW\nBEGIN\n")
                    .append(trigger.getDefinition().endsWith(";") ? trigger.getDefinition() : trigger.getDefinition() + ";")
                    .append("\nEND;");
        }
    }

    protected void columnHandle(StringBuilder builder, DamengCreateTableParam param) {
        for (DamengColumn column : param.getColumns()) {
            builder.append(DBUtil.wrap(column.getName()));
            // 字段类型（达梦不支持MySQL式显示宽度如INT(10)）
            builder.append(" ").append(normalizeType(column.getType()));
            // 字段长度
            if (column.supportSize() && column.getSize() != null) {
                builder.append("(").append(column.getSize());
                // 小数位
                if (column.supportDigits() && column.getDigits() != null) {
                    builder.append(",").append(column.getDigits());
                }
                builder.append(")");
            } else if (column.supportValue() && column.getValue() != null) {
                builder.append("(").append(column.getValue()).append(")");
            }

            // 达梦自增列使用 IDENTITY
            boolean isIdentity = column.supportAutoIncrement() && column.isAutoIncrement();

            // 默认值（达梦自增列不能有DEFAULT约束）
            if (!isIdentity && column.supportDefaultValue() && column.getDefaultValue() != null) {
                builder.append(" DEFAULT ").append(DBUtil.wrapData(column.getDefaultValueString()));
            }
            // 可为null
            if (column.isNullable()) {
                builder.append(" NULL");
            } else {
                builder.append(" NOT NULL");
            }
            if (isIdentity) {
                builder.append(" IDENTITY(1,1)");
            }
            // 达梦列注释使用独立COMMENT ON COLUMN语句，ALTER TABLE中跳过
            if (column.hasComment()) {
                String commentSql = """
                            COMMENT ON COLUMN "$1"."$2"."$3" IS '$4';
                            """;
                commentSql = commentSql.replace("$1", column.getDbName());
                commentSql = commentSql.replace("$2", column.getTableName());
                commentSql = commentSql.replace("$3", column.getName());
                commentSql = commentSql.replace("$4", column.getComment());
                this.sqlList.add(commentSql);
            }
            builder.append(",");
        }
    }

    protected void primaryKeyHandle(StringBuilder builder, DamengCreateTableParam param) {
        List<DamengColumn> keyList = param.primaryKeys();
        if (!keyList.isEmpty()) {
            builder.append(" PRIMARY KEY (");
            for (DamengColumn column : keyList) {
                builder.append(DBUtil.wrap(column.getName()));
                if (column.supportKeySize()) {
                    if (column.getPrimaryKeySize() != null) {
                        builder.append("(").append(column.getPrimaryKeySize()).append(")");
                    } else if (column.getSize() != null) {
                        builder.append("(").append(Math.min(column.getSize(), 100)).append(")");
                    } else {
                        builder.append("(100)");
                    }
                }
                builder.append(",");
            }
            StringUtil.deleteLast(builder, ",");
            builder.append("),");
        }
    }

    protected void indexHandle(StringBuilder builder, DamengCreateTableParam param) {
        DamengIndexes indexes = param.getIndexes();
        for (DamengIndex index : indexes) {
            if (index.isUnique()) {
                builder.append(" UNIQUE");
            }
            builder.append(" INDEX ")
                    .append(DBUtil.wrap(index.getName()))
                    .append(" (");
            for (DamengIndex.IndexColumn column : index.getColumns()) {
                builder.append(DBUtil.wrap(column.getColumnName()));
                builder.append(",");
            }
            StringUtil.deleteLast(builder, ",");
            builder.append(")");
            if (index.methodName() != null) {
                builder.append(" USING ").append(index.methodName());
            }
            builder.append(",");
        }
    }

    protected void foreignKeyHandle(StringBuilder builder, DamengCreateTableParam param) {
        DamengForeignKeys foreignKeys = param.getForeignKeys();
        for (DamengForeignKey foreignKey : foreignKeys) {
            builder.append(" CONSTRAINT ")
                    .append(DBUtil.wrap(foreignKey.getName()))
                    .append(" FOREIGN KEY (");
            for (String column : foreignKey.getColumns()) {
                builder.append(DBUtil.wrap(column)).append(",");
            }
            StringUtil.deleteLast(builder, ",");
            builder.append(")");
            builder.append(" REFERENCES ")
                    .append(DBUtil.wrap(foreignKey.getPrimaryKeyDatabase(), foreignKey.getPrimaryKeyTable()))
                    .append(" (");
            for (String column : foreignKey.getPrimaryKeyColumns()) {
                builder.append(DBUtil.wrap(column)).append(",");
            }
            StringUtil.deleteLast(builder, ",");
            builder.append(")")
                    .append(" ON DELETE ")
                    .append(foreignKey.getDeletePolicy());
            builder.append(",");
        }
    }

    protected void checkHandle(StringBuilder builder, DamengCreateTableParam table) {
        DamengChecks checks = table.getChecks();
        for (DamengCheck check : checks) {
            builder.append(" CONSTRAINT ")
                    .append(DBUtil.wrap(check.getName()))
                    .append(" CHECK (")
                    .append(check.getClause())
                    .append(")");
            builder.append(",");
        }
    }

    /**
     * 规范化字段类型，移除MySQL式显示宽度（如INT(10)→INT）
     */
    private static String normalizeType(String type) {
        if (type == null) return "";
        String upper = type.toUpperCase();
        if (upper.matches("(TINYINT|SMALLINT|INT|INTEGER|BIGINT|MEDIUMINT)\\s*\\(\\s*\\d+\\s*\\)")) {
            return upper.replaceAll("\\(.*\\)", "").trim();
        }
        return type;
    }

    public static String generateSql(DamengCreateTableParam param) {
        return new DamengTableCreateSqlGenerator().generate(param);
    }
}
