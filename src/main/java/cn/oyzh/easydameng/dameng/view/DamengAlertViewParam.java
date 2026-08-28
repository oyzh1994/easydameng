package cn.oyzh.easydameng.dameng.view;

/**
 * @author oyzh
 * @since 2024-09-14
 */
public class DamengAlertViewParam {

    private String dbName;

    private DamengView view;

    public String getDbName() {
        return this.dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public DamengView getView() {
        return view;
    }

    public void setView(DamengView view) {
        this.view = view;
    }

    public String getViewName() {
        return this.view.getName();
    }

    public void setViewName(String viewName) {
        this.view.setName(viewName);
    }

}
