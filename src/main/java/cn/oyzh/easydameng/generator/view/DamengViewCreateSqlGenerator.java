package cn.oyzh.easydameng.generator.view;

import cn.oyzh.easydameng.dameng.view.DamengAlertViewParam;
import cn.oyzh.easydameng.dameng.view.DamengCreateViewParam;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.db.DBSqlGenerator;
import cn.oyzh.easydameng.util.DBUtil;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengViewCreateSqlGenerator extends DBSqlGenerator {

    private void _generate(DamengCreateViewParam param) {
        DamengView view = param.getView();
        String dbName = param.getDbName();
        String viewName = param.getViewName();
        String viewFullName = DBUtil.wrap(dbName, viewName, DBDialect.DAMENG);

        this.sqlBuilder.append("CREATE VIEW ");
        this.sqlBuilder.append(viewFullName)
                .append(" AS \n")
                .append(view.getDefinition());
        if (view.getSecurityType() != null) {
            this.sqlBuilder.append("\n")
                    .append("BEQUEATH ")
                    .append(view.getSecurityType());
        }
        if (!view.isUpdatable()) {
            this.sqlBuilder.append("\n")
                    .append("WITH READ ONLY");
        }
        this.sqlBuilder.append(";");

        // 注释
        if(view.getComment()!=null){
            StringBuilder builder = new StringBuilder();
            builder.append("COMMENT ON TABLE ")
                    .append(viewFullName)
                    .append(" IS ")
                    .append(DBUtil.wrapData(view.getComment())).append(";");
            this.sqlList.add(builder.toString());
        }
    }

    public List<String> generate(DamengCreateViewParam param) {
        this._generate(param);
        return this.buildSql();
    }

    public String generateSingle(DamengCreateViewParam param) {
        this._generate(param);
        return this.buildSqlSingle();
    }

    public static List<String> generateSql(DamengCreateViewParam param) {
        return new DamengViewCreateSqlGenerator().generate(param);
    }

    public static String generateSqlSingle(DamengCreateViewParam param) {
        return new DamengViewCreateSqlGenerator().generateSingle(param);
    }
}
