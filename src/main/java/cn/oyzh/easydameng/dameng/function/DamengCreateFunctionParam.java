package cn.oyzh.easydameng.dameng.function;

/**
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengCreateFunctionParam {

    private String dbName;

    private DamengFunction function;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public DamengFunction getFunction() {
        return function;
    }

    public void setFunction(DamengFunction function) {
        this.function = function;
    }

    public String getFunctionName() {
        return this.function.getName();
    }

    public void setFunctionName(String functionName) {
        this.function.setName(functionName);
    }
}
