//package cn.oyzh.easydameng.store;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.oyzh.common.json.JSONUtil;
//import cn.oyzh.common.log.JulLog;
//import cn.oyzh.easydameng.DamengConst;
//import cn.oyzh.easydameng.domain.DamengQuery;
//import cn.oyzh.store.json.ArrayFileStore;
//import lombok.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * db查询存储
// *
// * @author oyzh
// * @since 2024/02/19
// */
//public class DBQueryStore extends ArrayFileStore<DamengQuery> {
//
//    /**
//     * 当前实例
//     */
//    public static final DBQueryStore INSTANCE = new DBQueryStore();
//
//    /**
//     * 已加载的db节点
//     */
//    private final List<DamengQuery> queryList;
//
//    {
//        this.filePath(DamengConst.STORE_PATH + "db_query.json");
//        JulLog.info("DBQueryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
//        this.queryList = this.load();
//    }
//
//    @Override
//    public synchronized List<DamengQuery> load() {
//        if (this.queryList == null) {
//            // 读取存储文件中的文本
//            String text = FileUtil.readString(this.storeFile(), this.charset());
//            if (StrUtil.isBlank(text)) {
//                return new ArrayList<>();
//            }
//            List<DamengQuery> list = JSONUtil.toBeanList(text, DamengQuery.class);
//            if (CollUtil.isNotEmpty(list)) {
//                list = list.parallelStream().sorted().collect(Collectors.toList());
//            }
//            return list;
//        }
//        return this.queryList;
//    }
//
//    public synchronized List<DamengQuery> list(String iid, String dbName) {
//        return this.load().parallelStream()
//                .filter(p -> iid.equals(p.getIid()) && dbName.equalsIgnoreCase(p.getDbName()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public synchronized boolean add( DamengQuery query) {
//        try {
//            if (query.getId() == null) {
//                query.setId(System.currentTimeMillis() + "");
//            }
//            Optional<DamengQuery> optional = this.queryList.parallelStream().filter(q -> q.compare(query)).findAny();
//            if (optional.isEmpty()) {
//                // 添加到集合
//                this.queryList.add(query);
//                // 更新数据
//                return this.save(this.queryList);
//            }
//        } catch (Exception e) {
//            JulLog.warn("add error,err:{}", e.getMessage());
//        }
//        return false;
//    }
//
//    @Override
//    public synchronized boolean update( DamengQuery query) {
//        try {
//            Optional<DamengQuery> optional = this.queryList.parallelStream().filter(q -> q.compare(query)).findAny();
//            // 更新数据
//            if (optional.isPresent()) {
//                return this.save(this.queryList);
//            }
//        } catch (Exception e) {
//            JulLog.warn("update error,err:{}", e.getMessage());
//        }
//        return false;
//    }
//
//    @Override
//    public synchronized boolean delete( DamengQuery query) {
//        try {
//            // 删除数据
//            if (this.queryList.remove(query)) {
//                return this.save(this.queryList);
//            }
//        } catch (Exception e) {
//            JulLog.warn("delete error,err:{}", e.getMessage());
//            return false;
//        }
//        return true;
//    }
//}
