package cn.oyzh.easydameng.procedure;

/**
 * 达梦存储过程查询参数。
 *
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengSelectProcedureParam {

    /**
     * 是否查询全部
     */
    private boolean full;

    /**
     * 模式名
     */
    private String schema;

    /**
     * 存储过程名
     */
    private String procedureName;

    /**
     * 获取是否查询全部
     *
     * @return 是否查询全部
     */
    public boolean isFull() {
        return full;
    }

    /**
     * 设置是否查询全部
     *
     * @param full 是否查询全部
     */
    public void setFull(boolean full) {
        this.full = full;
    }

    /**
     * 获取模式名
     *
     * @return 模式名
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置模式名
     *
     * @param schema 模式名
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取存储过程名
     *
     * @return 存储过程名
     */
    public String getProcedureName() {
        return procedureName;
    }

    /**
     * 设置存储过程名
     *
     * @param procedureName 存储过程名
     */
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }
}
