package cn.oyzh.easydameng.generator.view;

import cn.oyzh.easydameng.dameng.view.DamengCreateViewParam;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.db.DBSqlGenerator;
import cn.oyzh.easydameng.util.DBUtil;

/**
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengViewCreateSqlGenerator extends DBSqlGenerator {

    private void _generate(DamengCreateViewParam param) {
        DamengView view = param.getView();
        String dbName = param.getDbName();
        this.sqlBuilder.append("CREATE VIEW ");
        this.sqlBuilder.append(DBUtil.wrap(dbName, view.getName(), DBDialect.DAMENG))
                .append(" AS \n")
                .append(view.getDefinition());
    }

    public String generateSingle(DamengCreateViewParam param) {
        this._generate(param);
        return this.buildSqlSingle();
    }

    public static String generateSqlSingle(DamengCreateViewParam param) {
        return new DamengViewCreateSqlGenerator().generateSingle(param);
    }
}
