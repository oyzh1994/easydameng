package cn.oyzh.easydameng.dameng.view;

/**
 * 达梦查询视图参数
 *
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengSelectViewParam {

    /**
     * 是否查询完整定义
     */
    private boolean full;

    /**
     * 库名称或者模式名称
     */
    private String schema;

    /**
     * 视图名称
     */
    private String viewName;

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

    /** 获取视图名称 */
    public String getViewName() {
        return viewName;
    }

    /** 设置视图名称 */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
