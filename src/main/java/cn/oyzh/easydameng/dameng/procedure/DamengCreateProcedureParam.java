package cn.oyzh.easydameng.dameng.procedure;

/**
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengCreateProcedureParam {

    private String dbName;

    private DamengProcedure procedure;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public DamengProcedure getProcedure() {
        return procedure;
    }

    public void setProcedure(DamengProcedure procedure) {
        this.procedure = procedure;
    }

    public String getProcedureName() {
        return this.procedure.getName();
    }

    public void setProcedureName(String functionName) {
        this.procedure.setName(functionName);
    }
}
