package cn.oyzh.easydameng.dameng.generator.view;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.view.DamengAlertViewParam;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.db.DBSqlGenerator;
import cn.oyzh.easydameng.util.DBUtil;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengViewAlertSqlGenerator extends DBSqlGenerator {

    private void _generate(DamengAlertViewParam param) {
        DamengView view = param.getView();
        String dbName = param.getDbName();
        String viewName = param.getViewName();
        String viewFullName = DBUtil.wrap(dbName, viewName, DBDialect.DAMENG);

        this.sqlBuilder.append("CREATE OR REPLACE ");
        this.sqlBuilder.append(" VIEW ")
                .append(viewFullName)
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
        if (StringUtil.isNotBlank(view.getComment())) {
            StringBuilder builder = new StringBuilder();
            builder.append("COMMENT ON VIEW ")
                    .append(viewFullName)
                    .append(" IS ")
                    .append(DBUtil.wrapData(view.getComment(), DBDialect.DAMENG)).append(";");
            this.sqlList.add(builder.toString());
        }
    }

    public List<String> generate(DamengAlertViewParam param) {
        this._generate(param);
        return this.buildSql();
    }

    public String generateSingle(DamengAlertViewParam param) {
        this._generate(param);
        return this.buildSqlSingle();
    }

    public static List<String> generateSql(DamengAlertViewParam param) {
        return new DamengViewAlertSqlGenerator().generate(param);
    }

    public static String generateSqlSingle(DamengAlertViewParam param) {
        return new DamengViewAlertSqlGenerator().generateSingle(param);
    }
}
