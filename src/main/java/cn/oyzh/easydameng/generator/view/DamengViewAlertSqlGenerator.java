package cn.oyzh.easydameng.generator.view;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.view.DamengAlertViewParam;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.db.DBSqlGenerator;
import cn.oyzh.easydameng.util.DBUtil;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengViewAlertSqlGenerator extends DBSqlGenerator {

    private void _generate(DamengAlertViewParam param) {
        DamengView view = param.getView();
        String dbName = param.getDbName();
        this.sqlBuilder.append("CREATE OR REPLACE ");
        this.sqlBuilder.append(" VIEW ")
                .append(DBUtil.wrap(dbName, view.getName(), DBDialect.DAMENG))
                .append(" AS \n")
                .append(view.getDefinition());
    }

    public String generateSingle(DamengAlertViewParam param) {
        this._generate(param);
        return this.buildSqlSingle();
    }

    public static String generateSqlSingle(DamengAlertViewParam param) {
        return new DamengViewAlertSqlGenerator().generateSingle(param);
    }
}
