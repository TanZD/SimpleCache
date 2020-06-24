package com.zd.cache;

public class Test {
    public static void main(String[] args) {
        TTLCacheUtil<String, String> table = new TTLCacheUtil<>(3);
        TTLCacheUtil<String, String> table2 = new TTLCacheUtil<>(3);
        try {
            System.out.println(table.toString());
            table.set("张三", "1");
            table.set("李四", "2");
            System.out.println(table.toString());
            Thread.sleep(5000L);
            table.set("张三", "@");
            System.out.println(table.toString());
            Thread.sleep(10000L);
            System.out.println(table.toString());
            table.set("王五", "3");
            Thread.sleep(10000L);
            System.out.println(table.toString());
            System.out.println(table2.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
