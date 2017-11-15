package cn.edu.nju.entity.item;

import cn.edu.nju.entity.sign.Sign;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 项集
 */
public class ItemSet {

    /**
     * 项的集合
     */
    private Set<Item> itemSet;

    /**
     * 通过sign能够到达的项集
     */
    private Map<Sign, ItemSet> gotoMap;

    public ItemSet(Set<Item> itemSet) {
        this.itemSet = itemSet;
        this.gotoMap = new HashMap<>();
    }

    public Set<Item> getItemSet() {
        return itemSet;
    }

    public void setItemSet(Set<Item> itemSet) {
        this.itemSet = itemSet;
    }

    /**
     * 新增一条到另一个项集的边
     *
     * @param sign        文法符号
     * @param destItemSet 下一个项集
     */
    public void addEdge(Sign sign, ItemSet destItemSet) {
        gotoMap.put(sign, destItemSet);
    }

    public Map<Sign, ItemSet> getGotoMap() {
        return gotoMap;
    }

    /**
     * 返回该项集中能够规约的项，即dot在产生式右部最后的项
     *
     * @return 所有能够规约的项的集合
     */
    public Set<Item> getItemCanReduce() {
        Set<Item> itemsCanReduce = new HashSet<Item>();
        for (Item item : itemSet) {
            if (item.canReduce()) itemsCanReduce.add(item);
        }
        return itemsCanReduce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemSet itemSet1 = (ItemSet) o;

        return itemSet.equals(itemSet1.itemSet);
    }

    @Override
    public int hashCode() {
        return itemSet.hashCode();
    }
}
