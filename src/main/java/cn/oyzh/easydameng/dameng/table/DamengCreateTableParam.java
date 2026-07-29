package cn.oyzh.easydameng.dameng.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.check.DamengChecks;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKeys;
import cn.oyzh.easydameng.dameng.index.DamengIndexes;
import cn.oyzh.easydameng.dameng.trigger.DamengTriggers;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengCreateTableParam {

    private DamengTable table;

    private DamengChecks checks;

    private DamengColumns columns;

    private DamengIndexes indexes;

    private DamengTriggers triggers;

    private DamengForeignKeys foreignKeys;

    public String dbName() {
        return this.table.getDbName();
    }

    public boolean hasColumns() {
        return CollectionUtil.isNotEmpty(this.columns);
    }

    public List<DamengColumn> primaryKeys() {
        return this.columns.primaryKeys();
    }

    public boolean hasIndex() {
        return CollectionUtil.isNotEmpty(this.indexes);
    }

    public boolean hasForeignKey() {
        return CollectionUtil.isNotEmpty(this.foreignKeys);
    }

    public boolean hasCheck() {
        return CollectionUtil.isNotEmpty(this.checks);
    }

    public boolean hasTrigger() {
        return CollectionUtil.isNotEmpty(this.triggers);
    }

    public String tableName() {
        return this.table.getName();
    }

    public void setTableName(String tableName) {
        this.table.setName(tableName);
//        if (this.columns != null) {
//            for (DamengColumn column : columns) {
//                column.setTableName(this.tableName());
//            }
//        }
    }

    public DamengTable getTable() {
        return table;
    }

    public void setTable(DamengTable table) {
        this.table = table;
    }

    public DamengChecks getChecks() {
        return checks;
    }

    public void setChecks(DamengChecks checks) {
        this.checks = checks;
    }

    public DamengColumns getColumns() {
        return columns;
    }

    public void setColumns(DamengColumns columns) {
        this.columns = columns;
    }

    public DamengIndexes getIndexes() {
        return indexes;
    }

    public void setIndexes(DamengIndexes indexes) {
        this.indexes = indexes;
    }

    public DamengTriggers getTriggers() {
        return triggers;
    }

    public void setTriggers(DamengTriggers triggers) {
        this.triggers = triggers;
    }

    public DamengForeignKeys getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(DamengForeignKeys foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
}
