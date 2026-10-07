package cn.oyzh.easydameng.dameng.record;


import cn.oyzh.fx.db.DBRecordData;

/**
 * 达梦删除记录参数
 *
 * @author oyzh
 * @since 2024-09-13
 */
public class DamengDeleteRecordParam {

    /**
     * 数据库模式名
     */
    private String schema;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 记录数据
     */
    private DBRecordData record;

    /**
     * 主键信息
     */
    private DamengRecordPrimaryKey primaryKey;

    /**
     * 获取数据库模式名
     *
     * @return 数据库模式名
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置数据库模式名
     *
     * @param schema 数据库模式名
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取表名
     *
     * @return 表名
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名
     *
     * @param tableName 表名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 获取记录数据
     *
     * @return 记录数据
     */
    public DBRecordData getRecord() {
        return record;
    }

    /**
     * 设置记录数据
     *
     * @param record 记录数据
     */
    public void setRecord(DBRecordData record) {
        this.record = record;
    }

    /**
     * 获取主键信息
     *
     * @return 主键信息
     */
    public DamengRecordPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * 设置主键信息
     *
     * @param primaryKey 主键信息
     */
    public void setPrimaryKey(DamengRecordPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }
}
