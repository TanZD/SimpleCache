package com.zd.cache;

import java.util.concurrent.ConcurrentHashMap;

public class LRUCacheUtil<K, V> {

    class Node {
        Node prev;
        Node next;
        K key;
        V value;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.prev = null;
            this.next = null;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    private ConcurrentHashMap<Object, Node> concurrentHashMap;

    private Node head = new Node(null, null);
    private Node tail = new Node(null, null);

    //Map的大小 默认大小为10
    private Integer size = 10;

    public LRUCacheUtil(Integer size) {
        this.size = size;
        this.concurrentHashMap = new ConcurrentHashMap<>(size);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * 获取数据
     * 先查看缓存中是否存在Key的值 如果没有则返回null
     * 如果存在将该数据移动到链表最后一位表示最近使用过
     * 返回数据内容
     *
     * @param key
     * @return
     */
    public synchronized V get(K key) {
        if (concurrentHashMap.isEmpty()) {
            return null;
        }
        if (!concurrentHashMap.containsKey(key)) {
            return null;
        }
        Node data = concurrentHashMap.get(key);
        //将这个数据移到链表最后面表示最近使用过
        data.prev.next = data.next;
        data.next.prev = data.prev;
        moveToTail(data);
        return data.value;
    }

    public void set(K key, V value) {
        if (key == null || value == null || key == "") {
            throw new NullPointerException();
        }
        //先查找是否存在
        if (concurrentHashMap.containsKey(key)) {
            //如果存在就更新
            Node data = concurrentHashMap.get(key);
            System.out.println("Key=" + key + "值已存在，将对其数据进行更新-->" + data.value);
            data.value = value;
            //将该数据移到链表最后一位
            data.prev.next = data.next;
            data.next.prev = data.prev;
            moveToTail(data);
            return;
        }
        //如果已经满了
        if (size == concurrentHashMap.size()) {
            //将链表的第一个数据清除
            Node data = head.next;
            System.out.println("已经到达最大缓存");
            System.out.println("现将最近最少使用的数据清除->" + data.key + ":" + data.value);
            concurrentHashMap.remove(head.next.key);
            head.next.next.prev = head;
            head.next = head.next.next;
//            head.next.prev=head;
        }
        //添加数据
        Node node = new Node(key, value);
        concurrentHashMap.put(key, node);
        System.out.println("成功添加数据->" + node.key + ":" + node.value);
        moveToTail(node);
        return;
    }


    public void remove(K key) {
        if (concurrentHashMap.isEmpty()) {
            return;
        }
        if (!concurrentHashMap.containsKey(key)) {
            return;
        }
        Node data = concurrentHashMap.get(key);
        System.out.println("清除数据->" + data.key + ":" + data.value);
        data.prev.next = data.next;
        data.next.prev = data.prev;
        concurrentHashMap.remove(key);
        return;
    }


    public void moveToTail(Node node) {
        tail.prev.next = node;
        node.prev = tail.prev;
        tail.prev = node;
        node.next = tail;
    }

    @Override
    public String toString() {
        return "LRUCacheUtil{" +
                "concurrentHashMap=" + concurrentHashMap +
                ", head=" + head +
                ", tail=" + tail +
                ", size=" + size +
                '}';
    }
}
