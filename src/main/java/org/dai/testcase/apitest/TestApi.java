package org.dai.testcase.apitest;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class TestApi {

    @Test
    public void test0(){
        String env = System.getProperty("environment");
        System.out.println("env= " + env);
    }

    @Test
    public void test1(){
        Assert.assertEquals(1,2);
    }
    @Test
    public void test2(){
        Assert.assertEquals(1,1);
    }
    @Test
    public void test3(){
        Assert.assertEquals("aaa","aaa");
    }
    @Test
    public void logDemo(){
        Reporter.log("这是自己写的日志");
        throw new RuntimeException("这是自己的运行时异常");
    }
}
