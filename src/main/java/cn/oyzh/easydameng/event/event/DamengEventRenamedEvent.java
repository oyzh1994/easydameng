//package cn.oyzh.easydameng.event.event;
//
//import cn.oyzh.easydameng.trees.database.DamengSchemaTreeItem;
//import cn.oyzh.easydameng.trees.event.DamengEventTreeItem;
//import cn.oyzh.event.Event;
//
///**
// * @author oyzh
// * @since 2024/01/23
// */
//public class DamengEventRenamedEvent extends Event<DamengEventTreeItem> {
//
//    private DamengSchemaTreeItem dbItem;
//
//    public String eventName() {
//        return this.data().eventName();
//    }
//
//    public String dbName() {
//        return this.dbItem.dbName();
//    }
//
//    public DamengSchemaTreeItem getDbItem() {
//        return dbItem;
//    }
//
//    public void setDbItem(DamengSchemaTreeItem dbItem) {
//        this.dbItem = dbItem;
//    }
//}
