package com.alibaba;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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
        expressMap.put("JD987654321_19805433168", "5678");
        expressMap.put("YT456789123_13905479698", "9012");
        expressMap.put("ZT789123456_18505433664", "3456");
    }

    static class QueryRequest {
        private String trackingNumber;
        private String phone;

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    static class QueryResponse {
        private String pick_code;
        private String msg;

        public QueryResponse(String pick_code, String msg) {
            this.pick_code = pick_code;
            this.msg = msg;
        }

        public String getPick_code() {
            return pick_code;
        }

        public void setPick_code(String pick_code) {
            this.pick_code = pick_code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
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
                QueryRequest request = parseRequest(body);
                String trackingNumber = request.getTrackingNumber();
                String phone = request.getPhone();

                // 校验单号和手机号不能为空，不合法就抛异常走400分支
                if (trackingNumber == null || trackingNumber.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
                    throw new IllegalArgumentException("trackingNumber and phone are required");
                }

                String key = trackingNumber.trim() + "_" + phone.trim();
                String pickCode = expressMap.get(key);
                QueryResponse response;
                if (pickCode != null) {
                    response = new QueryResponse(pickCode, "查询成功");
                } else {
                    response = new QueryResponse(null, "未查询到该快递");
                }

                String json = toJson(response);
                byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } catch (Exception e) {
                // 处理异常，返回400状态码(Bad Request)
                String errorJson = "{\"pick_code\":null,\"msg\":\"请求格式错误\"}";
                byte[] errorBytes = errorJson.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(400, errorBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorBytes);
                }
            }
        }
    }

    private static QueryRequest parseRequest(String body) {
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Request body is empty");
        }

        String trimmed = body.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Request body is not JSON object");
        }

        Matcher trackingMatcher = Pattern.compile("\\\"trackingNumber\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(trimmed);
        Matcher phoneMatcher = Pattern.compile("\\\"phone\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(trimmed);

        if (!trackingMatcher.find() || !phoneMatcher.find()) {
            throw new IllegalArgumentException("Missing trackingNumber or phone");
        }

        QueryRequest request = new QueryRequest();
        request.setTrackingNumber(trackingMatcher.group(1));
        request.setPhone(phoneMatcher.group(1));
        return request;
    }

    private static String toJson(QueryResponse response) {
        String pickCode = response.getPick_code() == null ? "null" : "\"" + escapeJson(response.getPick_code()) + "\"";
        String msg = response.getMsg() == null ? "null" : "\"" + escapeJson(response.getMsg()) + "\"";
        return "{\"pick_code\":" + pickCode + ",\"msg\":" + msg + "}";
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}