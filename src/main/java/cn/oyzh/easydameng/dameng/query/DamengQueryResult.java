package cn.oyzh.easydameng.dameng.query;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.record.DamengRecord;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/08/19
 */
public abstract class DamengQueryResult {

    /**
     * sql
     */
    protected String sql;

    /**
     * 耗时，微妙
     */
    protected long used;

    /**
     * 消息
     */
    protected String msg;

    /**
     * 变更总数
     */
    protected int updateCount;

    /**
     * 是否成功
     */
    protected boolean success;

    /**
     * 字段列表
     */
    protected DamengColumns columns;

    /**
     * 行列表
     */
    protected List<DamengRecord> records;

    public boolean hasResult() {
        if (CollectionUtil.isNotEmpty(this.records)) {
            return true;
        }
        return this.columns == null || this.columns.isEmpty();
    }

    public void parseResult(ResultSet resultSet, Connection connection) throws Exception {
        this.parseResult(resultSet, connection, true);
    }

    public abstract void parseResult(ResultSet resultSet, Connection connection, boolean readonly) throws Exception;

    public String dbName() {
        if (this.columns != null) {
            for (DamengColumn column : this.columns) {
                return column.getDbName();
            }
        }
        return null;
    }

    public String tableName() {
        if (this.columns != null) {
            for (DamengColumn column : this.columns) {
                return column.getTableName();
            }
        }
        return null;
    }

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

    public int getCount() {
        return this.records == null ? 0 : this.records.size();
    }

    public long getUsedMs() {
        return this.used / 1_000_000L;
    }

    public List<DamengColumn> columnList() {
        if (this.columns == null) {
            return Collections.emptyList();
        }
        return this.columns;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DamengColumns getColumns() {
        return columns;
    }

    public void setColumns(DamengColumns columns) {
        this.columns = columns;
    }

    public List<DamengRecord> getRecords() {
        return records;
    }

    public void setRecords(List<DamengRecord> records) {
        this.records = records;
    }
}
