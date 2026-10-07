package cn.oyzh.easydameng.dameng.query;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.fx.db.query.DBQueryResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

/**
 * 达梦查询结果抽象基类
 *
 * @author oyzh
 * @since 2024/08/19
 */
public abstract class DamengQueryResult extends DBQueryResult {

    /**
     * 字段列表
     */
    protected DamengColumns columns;

    /**
     * 行列表
     */
    protected List<DamengRecord> records;

    @Override
    public int getCount() {
        return this.records == null ? 0 : this.records.size();
    }

    /**
     * 获取数据库名
     *
     * @return 数据库名
     */
    public String schema() {
        if (this.columns != null) {
            for (DamengColumn column : this.columns) {
                return column.getSchema();
            }
        }
        return null;
    }

    /**
     * 获取表名
     *
     * @return 表名
     */
    public String tableName() {
        if (this.columns != null) {
            for (DamengColumn column : this.columns) {
                return column.getTableName();
            }
        }
        return null;
    }

    /**
     * 获取主键字段
     *
     * @return 主键字段
     */
    public DamengColumn getPrimaryKey() {
        if (this.columns != null) {
            for (DamengColumn column : this.columns) {
                if (column.isAutoIncrement()) {
                    return column;
                }
            }
        }
        return null;
    }

    /**
     * 是否可更新
     *
     * @return 是否可更新
     */
    public boolean isUpdatable() {
        if (this.columns != null) {
            for (DamengColumn column : this.columns) {
                if (column.isAutoIncrement()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取字段列表
     *
     * @return 字段列表
     */
    public List<DamengColumn> columnList() {
        if (this.columns == null) {
            return Collections.emptyList();
        }
        return this.columns;
    }

    /** 获取字段列表 */
    public DamengColumns getColumns() {
        return columns;
    }

    /** 设置字段列表 */
    public void setColumns(DamengColumns columns) {
        this.columns = columns;
    }

    /** 获取记录列表 */
    public List<DamengRecord> getRecords() {
        return records;
    }

    /** 设置记录列表 */
    public void setRecords(List<DamengRecord> records) {
        this.records = records;
    }
}
