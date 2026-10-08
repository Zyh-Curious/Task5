package com.alibaba;

import com.alibaba.fastjson2.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 8000;
    // 模拟快递柜数据：key = 快递单号_手机号，value = 取件码
    private static final Map<String, String> expressMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // 初始化一些测试数据
        initializeExpressData();
        // 创建HTTP服务器，监听指定端口
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        // 设置路由和处理程序
        server.createContext("/query", new QueryHandler());
        // 启动服务器
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    private static void initializeExpressData() {
        // 添加一些测试数据
        // 键的构成是  快递单号_手机号
        expressMap.put("SF123456789_13005433678", "1234");
        // TODO 1: 参照测试数据表，把其余3条数据也加进去
    }

    static class QueryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 非POST请求返回405 Method Not Allowed
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            try {
                // 读取请求体（请求体是字节流，按UTF-8转成字符串）
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                // 解析JSON请求，取出单号和手机号
                QueryRequest request = JSON.parseObject(body, QueryRequest.class);
                String trackingNumber = request.getTrackingNumber();
                String phone = request.getPhone();

                // TODO 2: 校验单号和手机号不能为空，不合法就抛异常走400分支
                // TODO 3: 用"单号_手机号"拼成key，在expressMap中查询取件码
                // TODO 4: 根据查询结果构造QueryResponse
                // TODO 5: 把响应对象序列化成JSON并发送响应（状态码200）
            } catch (Exception e) {
                // 处理异常，返回400状态码(Bad Request)
                // TODO 6: 返回状态码400和错误JSON {"pick_code":null,"msg":"请求格式错误"}
            }
        }
    }
}

客户端
客户端用 HttpURLConnection 手动发 HTTP 请求。同样，带 TODO 标记的地方需要你补全.


