package cn.oyzh.easydameng.dameng.query;


import cn.oyzh.fx.db.query.DBQueryToken;

/**
 * 达梦查询词法单元
 *
 * @author oyzh
 * @since 2024/8/15
 */
public class ShellDamengQueryToken extends DBQueryToken {

    /**
     * 是否可能为关键字
     *
     * @return 是否可能为关键字
     */
    public boolean isPossibilityKeyword() {
        return ' ' == this.getToken() || 'n' == this.getToken() || '\0' == this.getToken();
    }

    /**
     * 是否可能为表
     *
     * @return 是否可能为表
     */
    public boolean isPossibilityTable() {
        return ' ' == this.getToken() || '"' == this.getToken() || ',' == this.getToken() || '.' == this.getToken();
    }

    /**
     * 是否可能为视图
     *
     * @return 是否可能为视图
     */
    public boolean isPossibilityView() {
        return ' ' == this.getToken() || '"' == this.getToken() || ',' == this.getToken() || '.' == this.getToken();
    }

    /**
     * 是否可能为函数
     *
     * @return 是否可能为函数
     */
    public boolean isPossibilityFunction() {
        return ' ' == this.getToken() || '"' == this.getToken() || ',' == this.getToken() || '.' == this.getToken();
    }

    /**
     * 是否可能为存储过程
     *
     * @return 是否可能为存储过程
     */
    public boolean isPossibilityProcedure() {
        return ' ' == this.getToken() || '"' == this.getToken() || ',' == this.getToken() || '.' == this.getToken();
    }

    /**
     * 是否可能为字段
     *
     * @return 是否可能为字段
     */
    public boolean isPossibilityColumn() {
        return ' ' == this.getToken() || '"' == this.getToken() || ',' == this.getToken() || '.' == this.getToken();
    }

    /**
     * 是否可能为数据库
     *
     * @return 是否可能为数据库
     */
    public boolean isPossibilityDatabase() {
        return '"' == this.getToken() || ' ' == this.getToken();
    }
}
