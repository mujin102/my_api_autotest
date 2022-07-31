package org.dai;

public class MyContext {
    private static String env = System.getProperty("enviroment");

    public static void main(String[] args){
        System.out.println("env=" + env);
    }

}
