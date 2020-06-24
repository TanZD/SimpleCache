package com.zd;

import static org.junit.Assert.assertTrue;

import com.zd.cache.LRUCacheUtil;
import com.zd.cache.TTLCache;
import com.zd.cache.TTLCacheUtil;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    /**
     * 测试数据的添加和查找删除
     */
    @Test
    public void TestSetAndGet() {
//        TTLCacheUtil<String, String> table = new TTLCacheUtil<>();
        TTLCacheUtil<String, HashMap> table = new TTLCacheUtil<>(3);
        table.setCategory("FIFO");
        try {
            HashMap<String, String> data = new HashMap<>();
            HashMap<String, String> data2 = new HashMap<>();
            HashMap<String, String> data3 = new HashMap<>();
            HashMap<String, String> data4 = new HashMap<>();
            //添加数据1
            data.put("COROLLA", "11W");
            data.put("LEVIN", "9W");
            data.put("AVALON", "19W");
            data.put("CAMRY", "17W");
            table.set("Toyota", data, -1L);
            //添加数据2
            data2.put("325i", "32W");
            data2.put("320i", "29W");
            data2.put("525i", "42W");
            data2.put("730Li", "82W");
            table.set("BMW", data2);
            //添加数据3
            data3.put("A4L", "30W");
            data3.put("A3", "19W");
            data3.put("Q7", "68W");
            data3.put("Q5", "38W");
            table.set("Audi", data3);

            System.out.println(table.toString());

            //获取所有数据
            HashMap<String, String> result;
            Collection totalData = table.values();
            System.out.println(totalData.toString());

            //查找数据
            result = table.get("Toyota");
            System.out.println("获取Toyota->" + result);
            result = table.get("NISSAN");
            System.out.println("查找NISSAN->" + result);
            result = table.get("BMW");
            System.out.println("查找BMW->" + result);

            System.out.println(table.toString());

            //添加数据4
            data4.put("civic", "11W");
            data4.put("CR-V", "16W");
            data4.put("ACCORD", "16W");
            table.set("HONDA", data4);
            System.out.println(table.toString());

            //移除数据
            table.remove("HONDA");
            table.remove("NISSAN");
            System.out.println(table.toString());

            //移除所有数据
            table.removeAll();
            System.out.println(table.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestTimeToLiveSeconds() {
        TTLCacheUtil<String, String> table = new TTLCacheUtil<>(10, 5000L);
        try {
            table.set("Rx-78-2", "GUNDAM", 2000L);
            table.set("Rx-93", "vGUNDAM");
            table.set("MSZ-010", "ZZ");
            table.set("MSZ-006", "Z", 3000L);
            table.set("MSN-04", "SAZABI", 1000L);
            System.out.println(table.toString());
            System.out.println(table.toString());
            System.out.println("------The World!!!------");
            Thread.sleep(9000L);
            System.out.println(table.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestLRUStrategy() {
        try {
            TTLCacheUtil<String, Integer> table = new TTLCacheUtil<>(3);
            table.setCategory("LRU");
            table.set("ARE", 0);
            table.set("YOU", 1);
            table.set("OK", 2);
            System.out.println("------The World!!!------");
            Thread.sleep(1000L);
            System.out.println("ARE->" + table.get("ARE").toString());
            System.out.println("YOU->" + table.get("YOU").toString());
            System.out.println(table.toString());
            table.set("MI FANS?", 3);
            System.out.println(table.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestLFUStrategy() {
        try {
            TTLCacheUtil<String, Integer> table = new TTLCacheUtil<>(3);
            table.setCategory("LFU");
            table.set("ARE", 0);
            table.set("YOU", 1);
            table.set("OK", 2);
            System.out.println("------The World!!!------");
            Thread.sleep(1000L);
            System.out.println("ARE->" + table.get("ARE").toString());
            System.out.println("YOU->" + table.get("OK").toString());
            System.out.println(table.toString());
            table.set("MI FANS?", 3);
            System.out.println(table.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestFIFOStrategy() {
        try {
            TTLCacheUtil<String, Integer> table = new TTLCacheUtil<>(3);
            table.setCategory("FIFO");
            table.set("ARE", 0);
            table.set("YOU", 1);
            table.set("OK", 2);
            System.out.println("------The World!!!------");
            Thread.sleep(1000L);
            System.out.println("ARE->" + table.get("ARE").toString());
            System.out.println("YOU->" + table.get("YOU").toString());
            System.out.println(table.toString());
            table.set("MI FANS? ", 3);
            System.out.println(table.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestLRUCacheUtil() {
        LRUCacheUtil<String, String> table = new LRUCacheUtil<>(3);
        table.set("Kuuga", "2000");
        table.set("Agito", "2001");
        table.set("Ryuki", "2002");
        table.set("Faiz", "2013");
        table.remove("Ryuki");
        table.set("Blade", "2004");
        table.set("Faiz", "2003");
        System.out.println("Agito-> " + table.get("Agito"));
        System.out.println(table.toString());
    }


    @Test
    public void TestTotal() {
        TTLCacheUtil<String, String> table = new TTLCacheUtil<>(3);
        try {
            System.out.println(table.toString());
            //添加数据1
            table.set("张三", "1");
            Thread.sleep(500L);
            //添加数据2
            table.set("李四", "2");
            Thread.sleep(500L);
            //添加数据3 并设置过期时间为5s
            table.set("王五", "3");
            System.out.println(table.toString());
            //添加数据4
            table.set("毛六", "4", 8000L);
            System.out.println(table.toString());
            Thread.sleep(1000L);
            //修改数据1
            table.set("李四", "1111");
            System.out.println(table.toString());
            System.out.println("------The World!!!------");
            Thread.sleep(10000L);
            System.out.println(table.toString());
            table.remove("张三");
            System.out.println(table.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
