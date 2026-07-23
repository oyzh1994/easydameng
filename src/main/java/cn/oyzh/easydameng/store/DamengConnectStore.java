package cn.oyzh.easydameng.store;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengSSHConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;

import java.util.List;

/**
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
     * 加载列表
     *
     * @return redis连接列表
     */
    public List<DamengConnect> load() {
        return super.selectList();
    }

    /**
     * 替换
     *
     * @param model 模型
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
