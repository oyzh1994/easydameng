package cn.oyzh.easydameng.generator.routine;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.dameng.routine.DamengRoutineParam;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.util.DBUtil;

import java.util.List;

/**
 * 国策sql生成器
 *
 * @author oyzh
 * @since 2024/08/09
 */
public class DamengProcedureSqlGenerator {

    public static final DamengProcedureSqlGenerator INSTANCE = new DamengProcedureSqlGenerator();

    public String generate(DamengProcedure procedure) {
        String sql = "CREATE OR REPLACE PROCEDURE " + DBUtil.wrap(procedure.getName(), DBDialect.DAMENG);
        // 参数
        sql += " (";
        List<DamengRoutineParam> params = procedure.getParams();
        if (CollectionUtil.isNotEmpty(params)) {
            for (DamengRoutineParam param : params) {
                sql = sql + "\n" + param.getDefinition() + ",";
            }
        }
        sql = StringUtil.replaceLast(sql, ",", "");
        sql += "\n) ";
        String characteristic = procedure.getCharacteristic();
        if (StringUtil.isNotBlank(characteristic)) {
            if (characteristic.contains("PARALLEL_ENABLE")) {
                sql += " \nPARALLEL_ENABLE";
            }
        }
        if (StringUtil.isNotBlank(procedure.getSecurityType())) {
            sql += " \nAUTHID " + procedure.getSecurityType();
        }
        // 存储过程体
        sql += " \nAS\n" + procedure.getDefinition();
        return sql;
    }
}
