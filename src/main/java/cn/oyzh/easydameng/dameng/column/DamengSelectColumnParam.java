package cn.oyzh.easydameng.dameng.column;

/**
 * 查询字段参数
 *
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengSelectColumnParam {

    /** 库名称或者模式名称 */
    private String schema;

    /** 表名称 */
    private String tableName;

    /**
     * 构造查询字段参数
     */
    public DamengSelectColumnParam() {
    }

    /**
     * 构造查询字段参数
     *
     * @param schema    库名称或者模式名称
     * @param tableName 表名称
     */
    public DamengSelectColumnParam(String schema, String tableName) {
        this.schema = schema;
        this.tableName = tableName;
    }

    /**
     * 获取库名称或者模式名称
     *
     * @return 库名称或者模式名称
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置库名称或者模式名称
     *
     * @param schema 库名称或者模式名称
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名称
     *
     * @param tableName 表名称
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
