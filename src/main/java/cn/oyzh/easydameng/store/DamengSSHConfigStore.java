package cn.oyzh.easydameng.store;

import cn.oyzh.easydameng.domain.DamengSSHConfig;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.QueryParam;

/**
 * 达梦数据库 SSH 配置存储。
 *
 * @author oyzh
 * @since 2024/09/26
 */
public class DamengSSHConfigStore extends JdbcStandardStore<DamengSSHConfig> {

    /**
     * 当前实例
     */
    public static final DamengSSHConfigStore INSTANCE = new DamengSSHConfigStore();

    /**
     * 保存 SSH 配置，已存在则更新，否则新增
     *
     * @param model SSH 配置对象
     * @return 结果
     */
    public boolean replace(DamengSSHConfig model) {
        String iid = model.getIid();
        if (super.exist(iid)) {
            return super.update(model);
        }
        return this.insert(model);
    }

    @Override
    protected Class<DamengSSHConfig> modelClass() {
        return DamengSSHConfig.class;
    }

    /**
     * 根据连接id删除 SSH 配置
     *
     * @param iid 连接id
     */
    public void deleteByIid(String iid) {
        DeleteParam param = new DeleteParam();
        param.addQueryParam(QueryParam.of("iid", iid));
        super.delete(param);
    }

    /**
     * 根据连接id获取 SSH 配置
     *
     * @param iid 连接id
     * @return SSH 配置对象
     */
    public DamengSSHConfig getByIid(String iid) {
        return super.selectOne(QueryParam.of("iid", iid));
    }
}
