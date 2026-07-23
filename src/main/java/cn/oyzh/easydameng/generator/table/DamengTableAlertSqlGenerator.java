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
        if (param.hasForeignKey()) {
            this.foreignKeyHandle2(this.sqlBuilder, param);
        }
        this.sqlBuilder.append("ALTER TABLE ")
                .append(DBUtil.wrap(dbName, tableName, DBDialect.MYSQL))
                .append(" \n");
        // 字段
        if (param.columnChanged()) {
            this.columnHandle(this.sqlBuilder, param);
        }
        // 主键
        if (param.primaryKeyChanged()) {
            this.primaryKeyHandle(this.sqlBuilder, param);
        }
        // 索引
        if (param.hasIndex()) {
            this.indexHandle(this.sqlBuilder, param);
        }
        // 外键
        if (param.hasForeignKey()) {
            this.foreignKeyHandle1(this.sqlBuilder, param);
        }
        // 检查
        if (param.hasCheck()) {
            this.checkHandle(this.sqlBuilder, param);
        }
        // 表字符集
        if (table.hasCharset()) {
            this.sqlBuilder.append(" CHARACTER SET = ").append(table.getCharset()).append(",");
        }
        // 表排序
        if (table.hasCollation()) {
            this.sqlBuilder.append(" COLLATE = ").append(table.getCollation()).append(",");
        }
        // 表引擎
        if (table.hasEngine()) {
            this.sqlBuilder.append(" ENGINE = ").append(table.getEngine()).append(",");
        }
        // 表注释
        if (table.hasComment()) {
            this.sqlBuilder.append(" COMMENT = ").append(DBUtil.wrapData(table.getComment())).append(",");
        }
        // 行格式
        if (table.hasRowFormat()) {
            this.sqlBuilder.append(" NULL AS ROW_FORMAT = ").append(table.getRowFormat()).append(",");
        }
        // 表自动递增
        if (table.hasAutoIncrement()) {
            this.sqlBuilder.append(" NULL AS AUTO_INCREMENT = ").append(table.getAutoIncrement()).append(",");
        }
        this.sqlBuilder.append(";");
        // 表触发器
        if (param.hasTrigger()) {
            this.triggerHandle(param);
        }
        return this.buildSql();
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
                        .append(DBUtil.wrap(trigger.originalName(), DBDialect.MYSQL))
                        .append(";");
                this.sqlList.add(builder.toString());
            }
            if (DamengTriggers.isChanged(trigger) || DamengTriggers.isCreated(trigger)) {
                StringBuilder builder = new StringBuilder();
                builder.append("CREATE TRIGGER ")
                        .append(DBUtil.wrap(trigger.getName(), DBDialect.MYSQL))
                        .append(" ")
                        .append(trigger.getPolicy())
                        .append(" ON ")
                        .append(DBUtil.wrap(param.tableName(), DBDialect.MYSQL))
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
                            .append(DBUtil.wrap(column.getName(), DBDialect.MYSQL));
                } else if (column.isNameChanged()) {
                    builder.append(" CHANGE COLUMN ")
                            .append(DBUtil.wrap(column.originalName(), DBDialect.MYSQL))
                            .append(" ")
                            .append(DBUtil.wrap(column.getName(), DBDialect.MYSQL));
                } else {
                    builder.append(" MODIFY COLUMN ")
                            .append(DBUtil.wrap(column.getName(), DBDialect.MYSQL));
                }
                // 字段类型
                builder.append(" ").append(column.getType());

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

                // 无符号
                if (column.supportUnsigned() && column.isUnsigned()) {
                    builder.append(" UNSIGNED ");
                }

                // 填充零
                if (column.supportZeroFill() && column.isZeroFill()) {
                    builder.append(" ZEROFILL ");
                }

                // 字符集及排序
                if (column.supportCharset()) {
                    if (column.getCharset() != null) {
                        builder.append(" CHARACTER SET ").append(column.getCharset());
                    }
                    if (column.getCollation() != null) {
                        builder.append(" COLLATE ").append(column.getCollation());
                    }
                }

                // 默认值
                if (column.supportDefaultValue() && column.getDefaultValue() != null) {
                    builder.append(" DEFAULT ").append(DBUtil.wrapData(column.getDefaultValueString()));
                }

                // 可为null
                if (column.isNullable()) {
                    builder.append(" NULL");
                } else {
                    builder.append(" NOT NULL");
                }

                // 根据时间戳更新
                if (column.supportTimestamp() && column.isUpdateOnCurrentTimestamp()) {
                    builder.append(" ON UPDATE CURRENT_TIMESTAMP(0)");
                }

                // 自动递增
                if (column.supportAutoIncrement() && column.isAutoIncrement()) {
                    builder.append(" NULL AS AUTO_INCREMENT ");
                }

                // 注释
                if (column.hasComment()) {
                    builder.append(" COMMENT ").append(DBUtil.wrapData(column.getComment()));
                }
                builder.append(",");
            } else if (DamengColumns.isDeleted(column)) {// 删除字段
                builder.append(" DROP COLUMN ")
                        .append(DBUtil.wrap(column.getName(), DBDialect.MYSQL))
                        .append(",");
            }
        }
        // 删除最后一个字符
        StringUtil.deleteLast(builder, ",");
    }

    protected void primaryKeyHandle(StringBuilder builder, DamengAlertTableParam table) {
        if(!builder.toString().endsWith(",")){
            builder.append(",");
        }
        if (table.isExistPrimaryKey()) {
            builder.append(" DROP PRIMARY KEY,");
        }
        List<DamengColumn> keyList = table.primaryKeys();
        if (!keyList.isEmpty()) {
            builder.append(" ADD PRIMARY KEY (");
            for (DamengColumn column : keyList) {
                builder.append(DBUtil.wrap(column.getName(), DBDialect.MYSQL));
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
            builder.append(") USING BTREE,");
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
                        .append(DBUtil.wrap(index.originalName(), DBDialect.MYSQL))
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
                        .append(DBUtil.wrap(index.getName(), DBDialect.MYSQL))
                        .append(" (");
                for (DamengIndex.IndexColumn column : index.getColumns()) {
                    builder.append(DBUtil.wrap(column.getColumnName(), DBDialect.MYSQL));
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
                    .append(DBUtil.wrap(foreignKey.getName(), DBDialect.MYSQL))
                    .append(" FOREIGN KEY (");
            for (String column : foreignKey.getColumns()) {
                builder.append(DBUtil.wrap(column, DBDialect.MYSQL)).append(",");
            }
            StringUtil.deleteLast(builder, ",");
            builder.append(")")
                    .append(" REFERENCES ")
                    .append(DBUtil.wrap(foreignKey.getPrimaryKeyDatabase(), foreignKey.getPrimaryKeyTable(), DBDialect.MYSQL))
                    .append(" (");
            for (String column : foreignKey.getPrimaryKeyColumns()) {
                builder.append(DBUtil.wrap(column, DBDialect.MYSQL)).append(",");
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

    protected void foreignKeyHandle2(StringBuilder builder, DamengAlertTableParam param) {
        DamengForeignKeys foreignKeys = param.getForeignKeys();
        if (!foreignKeys.hasChanged() && !foreignKeys.hasDeleted()) {
            return;
        }
        // StringBuilder builder = new StringBuilder();
        builder.append("ALTER TABLE ")
                .append(DBUtil.wrap(param.dbName(), param.tableName(), DBDialect.MYSQL));
        for (DamengForeignKey foreignKey : foreignKeys.filterList(DBObjectList.TYPE_DELETED, DBObjectList.TYPE_CHANGED)) {
            String fkName = foreignKey.originalName();
            // 名称为null是临时数据
            if (StringUtil.isNotBlank(fkName)) {
                this.sqlBuilder.append(" DROP FOREIGN KEY ")
                        .append(DBUtil.wrap(foreignKey.originalName(), DBDialect.MYSQL))
                        .append(",");
            }
        }
        StringUtil.deleteLast(builder, ",");
        builder.append(";");
    }

    protected void checkHandle(StringBuilder builder, DamengAlertTableParam param) {
        if (!builder.toString().endsWith(",")) {
            builder.append(",");
        }
        DamengChecks checks = param.getChecks();
        for (DamengCheck check : checks) {
            // 检查删除、变更
            if (DamengChecks.isDeleted(check) || DamengChecks.isChanged(check)) {
                builder.append("DROP CONSTRAINT ")
                        .append(DBUtil.wrap(check.originalName(), DBDialect.MYSQL))
                        .append(",");
            }
            // 检查新增、变更
            if (DamengChecks.isCreated(check) || DamengChecks.isChanged(check)) {
                builder.append(" ADD CONSTRAINT ")
                        .append(DBUtil.wrap(check.getName(), DBDialect.MYSQL))
                        .append(" CHECK (")
                        .append(check.getClause())
                        .append(")");
                // 拼接,
                builder.append(",");
            }
            StringUtil.deleteLast(builder, ",");
        }
    }

    public static String generateSql(DamengAlertTableParam param) {
        return new DamengTableAlertSqlGenerator().generate(param);
    }
}
