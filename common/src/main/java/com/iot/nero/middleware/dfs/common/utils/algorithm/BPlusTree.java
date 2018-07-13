package com.iot.nero.middleware.dfs.common.utils.algorithm;


/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/19
 * Time   9:25 AM
 */


/**
 * thanks to https://blog.csdn.net/lijiecao0226/article/details/24191543
 * @param <K>
 * @param <V>
 */
public class BPlusTree<K extends Comparable<K>, V> {

    /** 根节点 */
    protected BPlusNode<K, V> root;

    /** 阶数，M值 */
    protected int order;

    /** 叶子节点的链表头 */
    protected BPlusNode<K, V> head;

    /** 树高*/
    protected int height = 0;


    public BPlusNode<K, V> getRoot() {
        return root;
    }

    public void setRoot(BPlusNode<K, V> root) {
        this.root = root;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public BPlusNode<K, V> getHead() {
        return head;
    }

    public void setHead(BPlusNode<K, V> head) {
        this.head = head;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    public BPlusTree(int order) {
        if (order < 3) {
            System.out.print("order must be greater than 2");
            System.exit(0);
        }
        this.order = order;
        root = new BPlusNode<K, V>(true, true);
        head = root;
    }

    public void insertOrUpdate(K k,V v){
        root.insertOrUpdate(k,v);
    }


    public V get(K hash) {
        return this.getRoot().get(hash);
    }

    @Override
    public String toString() {
        return "BPlusTree{" +
                "root=" + root +
                ", order=" + order +
                ", head=" + head +
                ", height=" + height +
                '}';
    }


}
