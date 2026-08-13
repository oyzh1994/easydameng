package cn.oyzh.easydameng.generator.routine;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.dameng.routine.DamengRoutineParam;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.util.DBUtil;

import java.util.List;

/**
 * 函数sql生成器
 *
 * @author oyzh
 * @since 2024/08/09
 */
public class DamengFunctionSqlGenerator {

    public static final DamengFunctionSqlGenerator INSTANCE = new DamengFunctionSqlGenerator();

    public String generate(DamengFunction function) {
        String sql = "CREATE OR REPLACE FUNCTION " + DBUtil.wrap(function.getName(), DBDialect.DAMENG);
        // 参数
        sql += " (";
        List<DamengRoutineParam> params = function.getParams();
        if (CollectionUtil.isNotEmpty(params)) {
            for (DamengRoutineParam param : params) {
                sql = sql + "\n" + param.getDefinition() + ",";
            }
        }
        sql = StringUtil.replaceLast(sql, ",", "");
        sql += ") ";
        // 返回值
        DamengRoutineParam returnParam = function.getReturnParam();
        if (returnParam != null) {
            sql += " \nRETURN " + returnParam.getDefinition();
        }
        // 函数体
        sql += " \nAS\n" + function.getDefinition();
        return sql;
    }
}
