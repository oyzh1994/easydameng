package cn.oyzh.easydameng.store;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengSSHConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;

import java.util.List;

/**
 * 达梦数据库连接存储。
 *
 * @author oyzh
 * @since 2024/09/26
 */
public class DamengConnectStore extends JdbcStandardStore<DamengConnect> {

    /**
     * 当前实例
     */
    public static final DamengConnectStore INSTANCE = new DamengConnectStore();

    /**
     * ssh配置存储
     */
    private final DamengSSHConfigStore sshConfigStore = DamengSSHConfigStore.INSTANCE;

    /**
     * 加载连接列表
     *
     * @return 连接列表
     */
    public List<DamengConnect> load() {
        return super.selectList();
    }

    /**
     * 保存连接，存在则更新，否则新增，并同步维护关联的 SSH 配置
     *
     * @param model 连接对象
     * @return 结果
     */
    public boolean replace(DamengConnect model) {
        boolean result = false;
        if (model != null) {
            if (super.exist(model.getId())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }

            // ssh处理
            DamengSSHConfig sshConfig = model.getSshConfig();
            if (sshConfig != null) {
                sshConfig.setIid(model.getId());
                this.sshConfigStore.replace(sshConfig);
            } else {
                this.sshConfigStore.deleteByIid(model.getId());
            }
        }
        return result;
    }

    @Override
    public boolean delete(DamengConnect model) {
        boolean result = super.delete(model);
        // 删除关联配置
        if (result) {
            this.sshConfigStore.deleteByIid(model.getId());
        }
        return result;
    }

    @Override
    protected Class<DamengConnect> modelClass() {
        return DamengConnect.class;
    }
}
