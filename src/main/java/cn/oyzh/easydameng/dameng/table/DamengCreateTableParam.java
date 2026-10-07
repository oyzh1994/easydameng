package cn.oyzh.easydameng.dameng.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.check.DamengCheck;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKey;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.fx.db.DBObject;
import cn.oyzh.fx.db.DBObjects;

import java.util.List;

/**
 * 达梦创建表参数
 *
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengCreateTableParam {

    /**
     * 表
     */
    private DamengTable table;

    /**
     * 检查约束集合
     */
    private DBObjects<DamengCheck> checks;

    /**
     * 字段集合
     */
    private DamengColumns columns;

    /**
     * 索引集合
     */
    private DBObjects<DamengIndex> indexes;

    /**
     * 触发器集合
     */
    private DBObjects<DamengTrigger> triggers;

    /**
     * 外键集合
     */
    private DBObjects<DamengForeignKey> foreignKeys;

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String schema() {
        return this.table.getSchema();
    }

    /**
     * 是否包含字段
     *
     * @return 结果
     */
    public boolean hasColumns() {
        return CollectionUtil.isNotEmpty(this.columns);
    }

    /**
     * 获取主键字段列表
     *
     * @return 主键字段列表
     */
    public List<DamengColumn> primaryKeys() {
        return this.columns.primaryKeys();
    }

    /**
     * 是否包含索引
     *
     * @return 结果
     */
    public boolean hasIndex() {
        return CollectionUtil.isNotEmpty(this.indexes);
    }

    /**
     * 是否包含外键
     *
     * @return 结果
     */
    public boolean hasForeignKey() {
        return CollectionUtil.isNotEmpty(this.foreignKeys);
    }

    /**
     * 是否包含检查约束
     *
     * @return 结果
     */
    public boolean hasCheck() {
        return CollectionUtil.isNotEmpty(this.checks);
    }

    /**
     * 是否包含触发器
     *
     * @return 结果
     */
    public boolean hasTrigger() {
        return CollectionUtil.isNotEmpty(this.triggers);
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        return this.table.getName();
    }

    /**
     * 设置表名称
     *
     * @param tableName 表名称
     */
    public void setTableName(String tableName) {
        this.table.setName(tableName);
//        if (this.columns != null) {
//            for (DamengColumn column : columns) {
//                column.setTableName(this.tableName());
//            }
//        }
    }

    /** 获取表 */
    public DamengTable getTable() {
        return table;
    }

    /** 设置表 */
    public void setTable(DamengTable table) {
        this.table = table;
    }

    /** 获取检查约束集合 */
    public DBObjects<DamengCheck> getChecks() {
        return checks;
    }

    /** 设置检查约束集合 */
    public void setChecks(DBObjects<DamengCheck> checks) {
        this.checks = checks;
    }

    /** 获取字段集合 */
    public DamengColumns getColumns() {
        return columns;
    }

    /** 设置字段集合 */
    public void setColumns(DamengColumns columns) {
        this.columns = columns;
    }

    /** 获取索引集合 */
    public DBObjects<DamengIndex> getIndexes() {
        return indexes;
    }

    /** 设置索引集合 */
    public void setIndexes(DBObjects<DamengIndex> indexes) {
        this.indexes = indexes;
    }

    /** 获取触发器集合 */
    public DBObjects<DamengTrigger> getTriggers() {
        return triggers;
    }

    /** 设置触发器集合 */
    public void setTriggers(DBObjects<DamengTrigger> triggers) {
        this.triggers = triggers;
    }

    /** 获取外键集合 */
    public DBObjects<DamengForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    /** 设置外键集合 */
    public void setForeignKeys(DBObjects<DamengForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
}
