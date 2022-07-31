package org.dai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dai.utils.HttpUtils;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class UnitTist {
    public ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void test1() throws Exception{

        String url="http://microloan-microcredit-test-o.jhjj.spider.test/api/v1/loan/internal/dolend";
        Map<String,String> map = new HashMap<>();
        map.put("applyId","312006292650");
        String res = HttpUtils.getInstance().get(url,map);

        String postUrl ="http://route-api.jhjj.paas.test/api/v1/fundOrg/queryPlan";
        JsonNode jsonNode = objectMapper.readTree("{\n" +
                "    \"channelCode\": \"119\",\n" +
                "    \"fundCode\": \"QL\",\n" +
                "    \"loanTerm\": \"12\",\n" +
                "    \"loanAmount\": \"150000\"\n" +
                "}\n");
        String params = objectMapper.writeValueAsString(jsonNode);
        res = HttpUtils.getInstance().postJson(postUrl,params);
        System.out.println("请求结果 res=" +res);

//        String s = "";
//        System.out.println("s.isEmpty() 判断结果为： " + s.isEmpty());
//        Map<String,String> params2 = new HashMap<>();
//        params2.put("1","第一个参数");
//        params2.put("2","第二个参数");
//        params2.put("3","第三个参数");
//        System.out.println("Map输出的结果为：" + params.toString());
    }
}
