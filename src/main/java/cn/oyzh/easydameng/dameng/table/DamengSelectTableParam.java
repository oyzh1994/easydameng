package cn.oyzh.easydameng.dameng.table;

/**
 * 达梦查询表参数
 *
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengSelectTableParam {

    /**
     * 是否查询完整定义
     */
    private boolean full;

    /**
     * 库名称或者模式名称
     */
    private String schema;

    /**
     * 表名称
     */
    private String tableName;

    /** 是否查询完整定义 */
    public boolean isFull() {
        return full;
    }

    /** 设置是否查询完整定义 */
    public void setFull(boolean full) {
        this.full = full;
    }

    /** 获取库名称或者模式名称 */
    public String getSchema() {
        return schema;
    }

    /** 设置库名称或者模式名称 */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /** 获取表名称 */
    public String getTableName() {
        return tableName;
    }

    /** 设置表名称 */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
