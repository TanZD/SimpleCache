package com.zd.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 有一说一这是我参考了网上一些资料写的
 * @param <K>
 * @param <V>
 */
public class TTLCacheUtil<K, V> {

    private ConcurrentHashMap<Object, TTLCache> concurrentHashMap;

    //Map的大小 默认大小为10
    private Integer size = 10;

    //默认的缓存过期时间
    private Long AliveTime = -1L;

    //淘汰策略
    private String Category = "LFU";

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Long getAliveTime() {
        return AliveTime;
    }

    public void setAliveTime(Long aliveTime) {
        AliveTime = aliveTime;
    }

    /**
     * 设置缓存的大小 以及过期时间
     *
     * @param size
     * @param aliveTime
     */
    public TTLCacheUtil(Integer size, Long aliveTime) {
        this.size = size;
        this.AliveTime = aliveTime;
        concurrentHashMap = new ConcurrentHashMap<>(size);
        //开启线程检测数据中的存活时间
        new Thread(new TimeSchedule()).start();
    }

    /**
     * 设置缓存的大小
     *
     * @param size
     */
    public TTLCacheUtil(Integer size) {
        this.size = size;
        concurrentHashMap = new ConcurrentHashMap<>(size);
        //开启线程检测数据中的存活时间
        new Thread(new TimeSchedule()).start();
    }

    /**
     * 默认
     */
    public TTLCacheUtil() {
        concurrentHashMap = new ConcurrentHashMap<>(size);
        //开启线程检测数据中的存活时间
        new Thread(new TimeSchedule()).start();
    }

    /**
     * 根据Key值获取数据
     * 如果不存在返回null
     * 存在就顺便更新访问次数和最后访问时间
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
        TTLCache cache = concurrentHashMap.get(key);
        //更新访问次数
        cache.setCount(cache.getCount() + 1);
        //更新访问时间
        cache.setLastAccessTime(new Date().getTime());
        return (V) cache.getValue();
    }

    /**
     * 添加数据
     * 先查找是否存在相同的Key值 如果存在则更新Value值、访问次数、最后访问时间
     * 如果不存在 先查看缓存满了没有 如果满了会将使用次数最少的那个数据给去除掉
     * 让后添加数据
     *
     * @param key
     * @param value
     * @param Time  数据缓存时间
     */
    public synchronized void set(K key, V value, Long Time) {
        if (key == null) {
            return;
        }
        //先查找是否已经存在Key
        if (concurrentHashMap.containsKey(key)) {
            //如果存在则更新内容
            TTLCache cache = concurrentHashMap.get(key);
            cache.setValue(value);
            cache.setLastAccessTime(new Date().getTime());
            cache.setCreateTime(new Date().getTime());
            cache.setAliveTime(Time);
            cache.setCount(cache.getCount() + 1);
            System.out.println("Key=" + key + "值已存在，将对其数据进行更新");
            System.out.println(cache.toString());
            return;
        }
        //如果已经满了
        if (size == concurrentHashMap.size()) {
            switch (Category) {
                case "LFU": {
                    //将使用次数最少的缓存清除掉
                    TTLCache cache = Collections.min(concurrentHashMap.values());
                    System.out.println("Map中已经到达最大缓存");
                    System.out.println("现将使用次数最少的数据清除->" + cache.toString());
                    concurrentHashMap.remove(cache.getKey());
                    break;
                }
                case "LRU": {
                    //将最近访问时间就久的数据清除掉
                    Iterator<V> iterator= (Iterator<V>) concurrentHashMap.keySet().iterator();
                    TTLCache cache= concurrentHashMap.get(iterator.next());
                    while(iterator.hasNext()){
                        TTLCache x= concurrentHashMap.get(iterator.next());
                        if(x.compareToLastAccessTime(cache)<0){
                            cache=x;
                        }
                    }
                    System.out.println("Map中已经到达最大缓存");
                    System.out.println("现将最近最少使用的数据清除->" + cache.toString());
                    concurrentHashMap.remove(cache.getKey());
                    break;
                }
                case "FIFO":{
                    //将最近访问时间就久的数据清除掉
                    Iterator<V> iterator= (Iterator<V>) concurrentHashMap.keySet().iterator();
                    TTLCache cache= concurrentHashMap.get(iterator.next());
                    while(iterator.hasNext()){
                        TTLCache x= concurrentHashMap.get(iterator.next());
                        if(x.compareToCreateTime(cache)<0){
                            cache=x;
                        }
                    }
                    System.out.println("Map中已经到达最大缓存");
                    System.out.println("现将最早生成的数据清除->" + cache.toString());
                    concurrentHashMap.remove(cache.getKey());
                    break;
                }
                default:
                    //将使用次数最少的缓存清除掉
                    TTLCache cache = Collections.min(concurrentHashMap.values());
                    System.out.println("Map中已经到达最大缓存");
                    System.out.println("现将使用次数最少的数据清除->" + cache.toString());
                    concurrentHashMap.remove(cache.getKey());
                    break;
            }
//                //将使用次数最少的缓存清除掉
//                TTLCache cache = Collections.min(concurrentHashMap.values());
//                System.out.println("Map中已经到达最大缓存");
//                System.out.println("现将使用次数最少的数据清除->" + cache.toString());
//                concurrentHashMap.remove(cache.getKey());
        }
        //添加
        TTLCache cache = new TTLCache(key, value, new Date().getTime(), new Date().getTime(), Time, 1);
        concurrentHashMap.put(key, cache);
        System.out.println("成功添加数据->" + cache.toString());
//        concurrentHashMap.remove("a");
        return;
    }


    /**
     * 添加数据 过期时间使用AliveTime(默认不过期)
     *
     * @param key
     * @param value
     */
    public synchronized void set(K key, V value) {
        this.set(key, value, this.AliveTime);
    }

    /**
     * 删除数据
     * 同样先查看是否存在该Key值 否则返回null
     * 存在就删除好了
     *
     * @param key
     */
    public synchronized void remove(K key) {
        if (key == null) {
            return;
        }
        //先查找是否存在
        if (concurrentHashMap.containsKey(key)) {
            TTLCache cache = concurrentHashMap.get(key);
            System.out.println("清除数据->" + cache.toString());
            concurrentHashMap.remove(key);
        }
        return;
    }

    /**
     * 清空所有数据
     */
    public synchronized void removeAll() {
        if (concurrentHashMap.isEmpty()) {
            return;
        }
        concurrentHashMap.clear();
        System.out.println("清除所有数据------->");
    }

    /**
     * 获取所有数据
     *
     * @return
     */
    public synchronized Collection<TTLCache> values() {
        if (concurrentHashMap.isEmpty()) {
            return null;
        }
        System.out.println("获取所有数据------->");
        return concurrentHashMap.values();
    }

    @Override
    public String toString() {
        return "{" +
                "Map=" + concurrentHashMap.toString() +
                ", size=" + size +
                ", AliveTime=" + AliveTime +
                '}';
    }

    /**
     * 开启线程
     * 用于检测数据是否到达过期时间
     * 如果到达了过期时间就将数据移出
     */
    class TimeSchedule implements Runnable {
        @Override
        public void run() {
            System.out.println("开启线程，检测数据是否到过期时间");
            while (true) {
                try {
                    Thread.sleep(1000);
                    concurrentHashMap.forEach((k, v) -> {
//                        System.out.println(k + "->Alive Time->" + v.getAliveTime() + "->still time->" + stillTime);
                        if (v.getAliveTime() == -1L) {
                            return;
                        }
                        Long nowTime = new Date().getTime();
                        Long stillTime = nowTime - v.getCreateTime();
                        if (stillTime > v.getAliveTime()) {
                            //超出过期时间
                            System.out.println("--------到达设置过期时间-清除数据->>" + k + ":" + v.getValue());
                            concurrentHashMap.remove(k);
                            return;
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
