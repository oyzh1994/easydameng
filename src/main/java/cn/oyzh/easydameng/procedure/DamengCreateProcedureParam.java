package cn.oyzh.easydameng.procedure;

/**
 * 达梦存储过程创建参数。
 *
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengCreateProcedureParam {

    /**
     * 模式名
     */
    private String schema;

    /**
     * 存储过程对象
     */
    private DamengProcedure procedure;

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
     * 获取存储过程对象
     *
     * @return 存储过程对象
     */
    public DamengProcedure getProcedure() {
        return procedure;
    }

    /**
     * 设置存储过程对象
     *
     * @param procedure 存储过程对象
     */
    public void setProcedure(DamengProcedure procedure) {
        this.procedure = procedure;
    }

    /**
     * 获取存储过程名
     *
     * @return 存储过程名
     */
    public String getProcedureName() {
        return this.procedure.getName();
    }

    /**
     * 设置存储过程名
     *
     * @param functionName 存储过程名
     */
    public void setProcedureName(String functionName) {
        this.procedure.setName(functionName);
    }
}
