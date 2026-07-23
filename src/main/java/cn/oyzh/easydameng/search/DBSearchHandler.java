//package cn.oyzh.easydameng.search;
//
//import cn.oyzh.easydameng.trees.function.DamengFunctionTreeItem;
//import cn.oyzh.easydameng.trees.procedure.DamengProcedureTreeItem;
//import cn.oyzh.easydameng.trees.query.DamengQueryTreeItem;
//import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
//import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
//import cn.oyzh.easydameng.trees.DBTreeView;
//import cn.oyzh.fx.common.util.TextUtil;
//import cn.oyzh.fx.plus.controls.search.SearchHandler;
//import cn.oyzh.fx.plus.controls.search.SearchParam;
//import cn.oyzh.fx.plus.controls.search.SearchValue;
//import cn.oyzh.fx.plus.trees.RichTreeItemValue;
//import cn.oyzh.fx.plus.util.ControlUtil;
//import javafx.scene.control.TreeItem;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Text;
//import lombok.NonNull;
//import lombok.experimental.Accessors;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * db主页搜索处理器
// *
// * @author oyzh
// * @since 2024/07/23
// */
//@Lazy
//@Component
//@Accessors(chain = true, fluent = true)
//public class DBSearchHandler extends SearchHandler {
//
//    /**
//     * 路径索引
//     */
//    private Integer pathIndex;
//
//    /**
//     * 树组件
//     */
//    private DBTreeView treeNode;
//
//    /**
//     * 搜索开始
//     */
//    public void init( DBTreeView treeNode) {
//        this.index = 0;
//        this.treeNode = treeNode;
//    }
//
//    @Override
//    public DBSearchParam searchParam() {
//        return (DBSearchParam) super.searchParam();
//    }
//
//    @Override
//    protected void resetSearch() {
//        super.resetSearch();
//        this.pathIndex = null;
//    }
//
//    @Override
//    public void preSearch(SearchParam param) {
//        this.treeNode.disable();
//        super.preSearch(param);
//        this.treeNode.enable();
//    }
//
//    @Override
//    protected void doSearch(SearchParam param, String action) {
//        this.treeNode.disable();
//        super.doSearch(param, action);
//        this.treeNode.enable();
//    }
//
//    @Override
//    protected void applyValue(SearchValue value, int index) {
//        super.applyValue(value, index);
//        // 选中并滚动到此节点
//        this.treeNode.selectAndScroll(value.getItem());
//    }
//
//    @Override
//    protected void updateCurrentItem(TreeItem<?> item) {
//        // 取消文本组件的选中
//        this.pathIndex = 0;
//        super.updateCurrentItem(item);
//    }
//
//    @Override
//    protected List<SearchValue> getMatchValues() {
//        return super.getMatchValues(this.treeNode.root());
//    }
//
//    @Override
//    public void doAnalyse() {
//        try {
//            // 判断节点是否存在
//            if (this.currentItem == null) {
//                return;
//            }
//            // 执行路径分析
//            if (this.pathIndex != -100 && this.nameAnalyse()) {
//                return;
//            }
//            this.pathIndex = 0;
//            RichTreeItemValue value = (RichTreeItemValue) this.currentItem.getValue();
//            ControlUtil.deselect(value.text());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    /**
//     * 名称节点分析
//     *
//     * @return 结果
//     */
//    private boolean nameAnalyse() {
//        try {
//            String kw = this.searchParam.getKw();
//            RichTreeItemValue value = (RichTreeItemValue) this.currentItem.getValue();
//            Text text = value.text();
//            String path = value.name();
//            // 搜索索引
//            int index = TextUtil.findIndex(path, kw, this.pathIndex, this.searchParam.isCompareCase(), this.searchParam.isFullMatch());
//            if (index != -1) {
//                int end = index + kw.length();
//                text.setSelectionStart(index);
//                text.setSelectionEnd(end);
//                text.setSelectionFill(Color.ORANGERED);
//                this.pathIndex = end;
//                return true;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        this.pathIndex = -100;
//        return false;
//    }
//
//    @Override
//    public String getMatchType(TreeItem<?> item) {
//        if (item == null || this.searchParam == null) {
//            return null;
//        }
//        boolean m1 = false;
//        // 表
//        if (item instanceof DamengTableTreeItem treeItem) {
//            String value = treeItem.getValue().name();
//            m1 = this.searchParam.isMatch(value);
//        } else if (item instanceof DamengViewTreeItem treeItem) {// 视图
//            String value = treeItem.getValue().name();
//            m1 = this.searchParam.isMatch(value);
//        } else if (item instanceof DamengFunctionTreeItem treeItem) {// 函数
//            String value = treeItem.getValue().name();
//            m1 = this.searchParam.isMatch(value);
//        } else if (item instanceof DamengProcedureTreeItem treeItem) {// 过程
//            String value = treeItem.getValue().name();
//            m1 = this.searchParam.isMatch(value);
//        } else if (item instanceof DamengQueryTreeItem treeItem) {// 查询
//            String value = treeItem.getValue().name();
//            m1 = this.searchParam.isMatch(value);
//        }
//        return m1 ? "name" : null;
//    }
//}
