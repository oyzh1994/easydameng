package cn.oyzh.easydameng.dameng.view;

/**
 * 达梦修改视图参数
 *
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengAlertViewParam {

    /**
     * 库名称或者模式名称
     */
    private String schema;

    /**
     * 视图
     */
    private DamengView view;

    /** 获取库名称或者模式名称 */
    public String getSchema() {
        return schema;
    }

    /** 设置库名称或者模式名称 */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /** 获取视图 */
    public DamengView getView() {
        return view;
    }

    /** 设置视图 */
    public void setView(DamengView view) {
        this.view = view;
    }

    /**
     * 获取视图名称
     *
     * @return 视图名称
     */
    public String getViewName() {
        return this.view.getName();
    }

    /**
     * 设置视图名称
     *
     * @param viewName 视图名称
     */
    public void setViewName(String viewName) {
        this.view.setName(viewName);
    }

}
