package cn.oyzh.easydameng.data.db;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 连接管理
 *
 * @author oyzh
 * @since 2023/5/12
 */
public interface DBConnectManager {

    /**
     * 添加连接
     *
     * @param redisInfo 连接信息
     */
    void addConnect( DamengConnect redisInfo);

    /**
     * 添加多个连接
     *
     * @param redisInfos 连接列表
     */
    default void addConnects(List<DamengConnect> redisInfos) {
        if (CollectionUtil.isNotEmpty(redisInfos)) {
            for (DamengConnect redisInfo : redisInfos) {
                this.addConnect(redisInfo);
            }
        }
    }

    /**
     * 添加连接键
     *
     * @param item 连接键
     */
    void addConnectItem( DBConnectTreeItem item);

    /**
     * 添加多个连接键
     *
     * @param items 连接键列表
     */
    void addConnectItems( List<DBConnectTreeItem> items);

    /**
     * 删除连接键
     *
     * @param item 连接键
     * @return 结果
     */
    boolean delConnectItem( DBConnectTreeItem item);

    /**
     * 获取连接键
     *
     * @return 连接键
     */
    List<DBConnectTreeItem> getConnectItems();

    /**
     * 获取已连接的连接节点
     *
     * @return 已连接的连接节点
     */
    default List<DBConnectTreeItem> getConnectedItems() {
        return this.getConnectItems().parallelStream().filter(DBConnectTreeItem::isConnected).collect(Collectors.toList());
    }

}
