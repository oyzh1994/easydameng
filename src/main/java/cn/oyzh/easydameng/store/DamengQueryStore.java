package cn.oyzh.easydameng.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.QueryParam;
import cn.oyzh.store.jdbc.param.SelectParam;

import java.util.List;

/**
 * 达梦数据库查询存储。
 *
 * @author oyzh
 * @since 2025/01/20
 */
public class DamengQueryStore extends JdbcStandardStore<DamengQuery> {

    /**
     * 当前实例
     */
    public static final DamengQueryStore INSTANCE = new DamengQueryStore();

    /**
     * 根据连接id和数据库名称加载查询列表
     *
     * @param iid    连接id
     * @param dbName 数据库名称
     * @return 查询列表
     */
    public List<DamengQuery> list(String iid, String dbName) {
        QueryParam param1 = QueryParam.of("iid", iid);
        QueryParam param2 = QueryParam.of("dbName", dbName);
        SelectParam selectParam = new SelectParam();
        selectParam.addQueryParam(param1);
        selectParam.addQueryParam(param2);
        return super.selectList(selectParam);
    }

    /**
     * 保存查询，不存在则新增，存在则更新
     *
     * @param model 查询对象
     * @return 结果
     */
    public boolean replace(DamengQuery model) {
        if (model != null) {
            if (!this.exist(model.getUid())) {
                return this.insert(model);
            }
            return this.update(model);
        }
        return false;
    }

    /**
     * 根据连接id删除查询
     *
     * @param iid 连接id
     * @return 结果
     * @see cn.oyzh.easydameng.domain.DamengConnect
     */
    public boolean deleteByIid(String iid) {
        if (StringUtil.isEmpty(iid)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(QueryParam.of("iid", iid));
            return this.delete(param);
        }
        return false;
    }

    @Override
    protected Class<DamengQuery> modelClass() {
        return DamengQuery.class;
    }
}
