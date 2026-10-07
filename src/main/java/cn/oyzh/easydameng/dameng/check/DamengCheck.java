package cn.oyzh.easydameng.dameng.check;

import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.db.DBCheck;
import cn.oyzh.fx.db.DBObject;

/**
 * 达梦检查约束对象
 *
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengCheck extends DBObject implements DBCheck, ObjectCopier<DamengCheck> {

    /**
     * 库名称
     */
    private String schema;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 名称
     */
    private String name;

    /**
     * 子语句
     */
    private String clause;

    /**
     * 构造检查约束对象
     */
    public DamengCheck() {

    }

    /**
     * 构造检查约束对象
     *
     * @param name 检查约束名称
     */
    public DamengCheck(String name) {
        this.name = name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }

    /**
     * 检查约束名称是否已变更
     *
     * @return 结果
     */
    public boolean isNameChanged() {
        return super.checkOriginalData("name", this.name);
    }

    /**
     * 获取原始名称
     *
     * @return 原始名称
     */
    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    /**
     * 设置子语句
     *
     * @param clause 子语句
     */
    public void setClause(String clause) {
        this.clause = clause;
        super.putOriginalData("clause", clause);
    }

    /**
     * 子语句是否已变更
     *
     * @return 结果
     */
    public boolean isClauseChanged() {
        return super.checkOriginalData("clause", this.clause);
    }

    @Override
    public void copy(DamengCheck check) {
        if (check != null) {
            this.name = check.name;
            this.schema = check.schema;
            this.clause = check.clause;
            this.tableName = check.tableName;
        }
    }

    @Override
    public boolean isInvalid() {
        return DBCheck.super.isInvalid() || StringUtil.isBlank(this.clause);
    }

    /** 获取库名称 */
    public String getSchema() {
        return schema;
    }

    /** 设置库名称 */
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

    @Override
    public String getName() {
        return name;
    }

    /**
     * 获取子语句
     *
     * @return 子语句
     */
    public String getClause() {
        return clause;
    }
}
