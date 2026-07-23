package cn.oyzh.easydameng.generator.table;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.db.DBObjectList;
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
import cn.oyzh.easydameng.util.DBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengTableAlertSqlGenerator {

    private List<String> sqlList;

    private StringBuilder sqlBuilder;

    public String generate(DamengAlertTableParam param) {
        this.sqlList = new ArrayList<>();
        this.sqlBuilder = new StringBuilder();
        String dbName = param.dbName();
        String tableName = param.tableName();
        DamengTable table = param.getTable();
        String tableFullName = DBUtil.wrap(dbName, tableName, DBDialect.DAMENG);

        if (param.hasForeignKey()) {
            this.foreignKeyHandle2(param, tableFullName);
        }
        // 修改列、主键、索引、外键、检查 — 每个操作生成独立 ALTER TABLE 语句
        if (param.columnChanged()) {
            int beforeAll = this.sqlBuilder.length();
            this.sqlBuilder.append("ALTER TABLE ").append(tableFullName).append(" ");
            this.columnHandle(this.sqlBuilder, param);
            if (this.sqlBuilder.length() == beforeAll + ("ALTER TABLE " + tableFullName + " ").length()) {
                this.sqlBuilder.setLength(beforeAll);
            } else {
                this.endStatement();
            }
        }
        if (param.primaryKeyChanged()) {
            int beforeAll = this.sqlBuilder.length();
            this.sqlBuilder.append("ALTER TABLE ").append(tableFullName).append(" ");
            this.primaryKeyHandle(this.sqlBuilder, param);
            if (this.sqlBuilder.length() == beforeAll + ("ALTER TABLE " + tableFullName + " ").length()) {
                this.sqlBuilder.setLength(beforeAll);
            } else {
                this.endStatement();
            }
        }
        if (param.hasIndex()) {
            int beforeAll = this.sqlBuilder.length();
            this.sqlBuilder.append("ALTER TABLE ").append(tableFullName).append(" ");
            this.indexHandle(this.sqlBuilder, param);
            if (this.sqlBuilder.length() == beforeAll + ("ALTER TABLE " + tableFullName + " ").length()) {
                this.sqlBuilder.setLength(beforeAll);
            } else {
                this.endStatement();
            }
        }
        if (param.hasForeignKey()) {
            int beforeAll = this.sqlBuilder.length();
            this.sqlBuilder.append("ALTER TABLE ").append(tableFullName).append(" ");
            this.foreignKeyHandle1(this.sqlBuilder, param);
            if (this.sqlBuilder.length() == beforeAll + ("ALTER TABLE " + tableFullName + " ").length()) {
                this.sqlBuilder.setLength(beforeAll);
            } else {
                this.endStatement();
            }
        }
        if (param.hasCheck()) {
            int beforeAll = this.sqlBuilder.length();
            this.sqlBuilder.append("ALTER TABLE ").append(tableFullName).append(" ");
            this.checkHandle(this.sqlBuilder, param);
            if (this.sqlBuilder.length() == beforeAll + ("ALTER TABLE " + tableFullName + " ").length()) {
                this.sqlBuilder.setLength(beforeAll);
            } else {
                this.endStatement();
            }
        }
        // 达梦表注释
        if (table.hasComment()) {
            this.sqlBuilder.append("COMMENT ON TABLE ").append(tableFullName)
                    .append(" IS ").append(DBUtil.wrapData(table.getComment())).append(";\n");
        }
        // 表触发器
        if (param.hasTrigger()) {
            this.triggerHandle(param);
        }
        return this.buildSqlNoComma();
    }

    private void endStatement() {
        String str = this.sqlBuilder.toString();
        if (str.endsWith(",")) {
            this.sqlBuilder.setLength(this.sqlBuilder.length() - 1);
        }
        this.sqlBuilder.append(";\n");
    }


    private String buildSqlNoComma() {
        String result = this.sqlBuilder.toString();
        result = result.replaceAll(",\n", "\n");
        result = result.replaceAll(",;", ";");
        return result.trim();
    }

    private String buildSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.sqlBuilder);
        for (String sql : sqlList) {
            builder.append("\n").append(sql);
        }
        return builder.toString().trim();
    }

    protected void triggerHandle(DamengAlertTableParam param) {
        DamengTriggers triggers = param.getTriggers();
        for (DamengTrigger trigger : triggers) {
            if (DamengTriggers.isDeleted(trigger) || DamengTriggers.isChanged(trigger)) {
                StringBuilder builder = new StringBuilder();
                builder.append("DROP TRIGGER ")
                        .append(DBUtil.wrap(trigger.originalName(), DBDialect.DAMENG))
                        .append(";");
                this.sqlList.add(builder.toString());
            }
            if (DamengTriggers.isChanged(trigger) || DamengTriggers.isCreated(trigger)) {
                StringBuilder builder = new StringBuilder();
                builder.append("CREATE TRIGGER ")
                        .append(DBUtil.wrap(trigger.getName(), DBDialect.DAMENG))
                        .append(" ")
                        .append(trigger.getPolicy())
                        .append(" ON ")
                        .append(DBUtil.wrap(param.tableName(), DBDialect.DAMENG))
                        .append(" FOR EACH ROW ")
                        .append(trigger.getDefinition())
                        .append(";");
                this.sqlList.add(builder.toString());
            }
        }
    }

    protected void columnHandle(StringBuilder builder, DamengAlertTableParam param) {
        for (DamengColumn column : param.getColumns()) {
            // 修改或者新增字段
            if (DamengColumns.isChanged(column) || DamengColumns.isCreated(column)) {
                if (column.isCreated()) {
                    builder.append(" ADD COLUMN ")
                            .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
                } else if (column.isNameChanged()) {
                    builder.append(" RENAME COLUMN ")
                            .append(DBUtil.wrap(column.originalName(), DBDialect.DAMENG))
                            .append(" TO ")
                            .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
                } else {
                    builder.append(" MODIFY ")
                            .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG));
                }
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
                } else if (column.supportValue() && column.getValue() != null) {// 值
                    builder.append("(").append(column.getValue()).append(")");
                }

                // 达梦不支持 UNSIGNED / ZEROFILL / CHARACTER SET / COLLATE，跳过

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

                // 达梦自增列使用 IDENTITY
                if (isIdentity) {
                    builder.append(" IDENTITY(1,1)");
                }

                // 达梦列注释使用独立COMMENT ON COLUMN语句，ALTER TABLE中跳过
                builder.append(",");
            } else if (DamengColumns.isDeleted(column)) {// 删除字段
                builder.append(" DROP COLUMN ")
                        .append(DBUtil.wrap(column.getName(), DBDialect.DAMENG))
                        .append(",");
            }
        }
        // 删除最后一个字符
        StringUtil.deleteLast(builder, ",");
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
        // if(!builder.toString().endsWith(",")){
        //     builder.append(",");
        // }
        DamengIndexes indexes = param.getIndexes();
        for (DamengIndex index : indexes) {
            // 索引删除、变更
            if (DamengIndexes.isDeleted(index) || DamengIndexes.isChanged(index)) {
                builder.append("DROP INDEX ")
                        .append(DBUtil.wrap(index.originalName(), DBDialect.DAMENG))
                        .append(",");
            }
            // 索引新增、变更
            if (DamengIndexes.isCreated(index) || DamengIndexes.isChanged(index)) {
                // 新增索引
                builder.append(" ADD");
                // 类型名称
                if (index.typeName() != null) {
                    builder.append(" ").append(index.typeName());
                }
                builder.append(" INDEX ")
                        .append(DBUtil.wrap(index.getName(), DBDialect.DAMENG))
                        .append(" (");
                for (DamengIndex.IndexColumn column : index.getColumns()) {
                    builder.append(DBUtil.wrap(column.getColumnName(), DBDialect.DAMENG));
                    if (column.getSubPart() != null && column.getSubPart() > 0) {
                        builder.append("(").append(column.getSubPart()).append(")");
                    }
                    builder.append(",");
                }
                // 删除最后一个字符
                StringUtil.deleteLast(builder);
                builder.append(") ");
                // 方法名称
                if (index.methodName() != null) {
                    builder.append(" USING ").append(index.methodName());
                }
                if (index.getComment() != null) {
                    builder.append(" COMMENT ").append(DBUtil.wrapData(index.getComment()));
                }
                // 拼接,
                builder.append(",");
            }
        }
        // // 删除最后一个字符
        // if (builder.toString().endsWith(",")) {
        //     builder.deleteCharAt(builder.length() - 1);
        // }
    }

    protected void foreignKeyHandle1(StringBuilder builder, DamengAlertTableParam table) {
        DamengForeignKeys foreignKeys = table.getForeignKeys();
        if (!foreignKeys.hasCreated() && !foreignKeys.hasChanged()) {
            return;
        }
        if(!builder.toString().endsWith(",")){
            builder.append(",");
        }
        for (DamengForeignKey foreignKey : foreignKeys.filterList(DBObjectList.TYPE_CHANGED, DBObjectList.TYPE_CREATED)) {
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
            builder.append(",");
        }
        StringUtil.deleteLast(builder, ",");
    }

    protected void foreignKeyHandle2(DamengAlertTableParam param, String tableFullName) {
        DamengForeignKeys foreignKeys = param.getForeignKeys();
        if (!foreignKeys.hasChanged() && !foreignKeys.hasDeleted()) {
            return;
        }
        this.sqlBuilder.append("ALTER TABLE ").append(tableFullName).append(" ");
        for (DamengForeignKey foreignKey : foreignKeys.filterList(DBObjectList.TYPE_DELETED, DBObjectList.TYPE_CHANGED)) {
            String fkName = foreignKey.originalName();
            if (StringUtil.isNotBlank(fkName)) {
                this.sqlBuilder.append("DROP CONSTRAINT ")
                        .append(DBUtil.wrap(foreignKey.originalName(), DBDialect.DAMENG))
                        .append(",");
            }
        }
        StringUtil.deleteLast(this.sqlBuilder, ",");
        this.sqlBuilder.append(";\n");
    }

    protected void checkHandle(StringBuilder builder, DamengAlertTableParam param) {
        DamengChecks checks = param.getChecks();
        for (DamengCheck check : checks) {
            // 检查删除、变更
            if (DamengChecks.isDeleted(check) || DamengChecks.isChanged(check)) {
                builder.append("DROP CONSTRAINT ")
                        .append(DBUtil.wrap(check.originalName(), DBDialect.DAMENG))
                        .append(",");
            }
            // 检查新增、变更
            if (DamengChecks.isCreated(check) || DamengChecks.isChanged(check)) {
                builder.append(" ADD CONSTRAINT ")
                        .append(DBUtil.wrap(check.getName(), DBDialect.DAMENG))
                        .append(" CHECK (")
                        .append(check.getClause())
                        .append(")");
                // 拼接,
                builder.append(",");
            }
            StringUtil.deleteLast(builder, ",");
        }
    }

    /**
     * 规范化字段类型，移除MySQL式显示宽度（如INT(10)→INT）
     */
    private static String normalizeType(String type) {
        if (type == null) return "";
        // 达梦不支持显示宽度的类型：INT(10), BIGINT(20), TINYINT(4), SMALLINT(6) 等
        String upper = type.toUpperCase();
        if (upper.matches("(TINYINT|SMALLINT|INT|INTEGER|BIGINT|MEDIUMINT)\\s*\\(\\s*\\d+\\s*\\)")) {
            return upper.replaceAll("\\(.*\\)", "").trim();
        }
        return type;
    }

    public static String generateSql(DamengAlertTableParam param) {
        return new DamengTableAlertSqlGenerator().generate(param);
    }
}
