package cn.oyzh.easydameng.store;

import cn.oyzh.easydameng.domain.DamengSSHConfig;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class DamengSSHConfigStore extends JdbcStandardStore<DamengSSHConfig> {

    /**
     * 当前实例
     */
    public static final DamengSSHConfigStore INSTANCE = new DamengSSHConfigStore();

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

    public void deleteByIid(String iid) {
        DeleteParam param = new DeleteParam();
        param.addQueryParam(QueryParam.of("iid", iid));
        super.delete(param);
    }

    public DamengSSHConfig getByIid(String iid) {
        return super.selectOne(QueryParam.of("iid", iid));
    }
}
