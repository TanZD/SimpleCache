package com.zd.cache;

public class TTLCache<K, V> implements Comparable<TTLCache> {
    //Key值
    private K key;

    //Value值
    private V value;

    //最后有一次的访问时间
    private Long LastAccessTime;

    //创建时间
    private Long CreateTime;

    //存活时间
    private Long AliveTime;

    //使用(获取)次数
    private Integer count = 0;

    public K getKey() {
        return key;
    }


    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }


    public Long getLastAccessTime() {
        return LastAccessTime;
    }

    public void setLastAccessTime(Long lastAccessTime) {
        LastAccessTime = lastAccessTime;
    }

    public Long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Long createTime) {
        CreateTime = createTime;
    }

    public Long getAliveTime() {
        return AliveTime;
    }

    public void setAliveTime(Long aliveTime) {
        AliveTime = aliveTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public int compareTo(TTLCache cache) {
        return count.compareTo(cache.count);
    }

    public int compareToCount(TTLCache cache){
        return count.compareTo(cache.count);
    }

    public int compareToCreateTime(TTLCache cache){
        return CreateTime.compareTo(cache.getCreateTime());
    }

    public int compareToLastAccessTime(TTLCache cache){
        return LastAccessTime.compareTo(cache.getLastAccessTime());
    }

    public TTLCache(K key, V value, Long lastAccessTime, Long createTime, Long aliveTime, Integer count) {
        this.key = key;
        this.value = value;
        LastAccessTime = lastAccessTime;
        CreateTime = createTime;
        AliveTime = aliveTime;
        this.count = count;
    }

    @Override
    public String toString() {

        return "TTLCache{" +
                "key=" + key +
                ", value=" + value +
                ", LastAccessTime=" + LastAccessTime +
                ", CreateTime=" + CreateTime +
                ", AliveTime=" + AliveTime +
                ", count=" + count +
                '}';
//        return "TTLCache{" +
//                "key=" + key +
//                ", value=" + value +
//                ", count=" + count +
//                ", AliveTime=" + AliveTime +
//                '}';
    }
}
