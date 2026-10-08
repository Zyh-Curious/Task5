package project5;

import com.alibaba.fastjson2.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Client {
    private static final String SERVER_URL = "http://localhost:8000/query";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                // 读取用户输入的快递单号
                System.out.print("请输入快递单号（输入exit退出）: ");
                String trackingNumber = scanner.nextLine().trim();
                // TODO 1: 输入exit时退出循环（
                if (trackingNumber.equals("exit")) {
                    break;
                }

                // 读取用户输入的手机号
                System.out.print("请输入手机号: ");
                String phone = scanner.nextLine().trim();

                // TODO 2: 用fastjson构造json格式的请求体，样例为
                // {"trackingNumber":"SF123456789","phone":"13005433678"}
                String json = "{\"trackingNumber\":\"" + trackingNumber + "\",\"phone\":\"" + phone + "\"}";

                // TODO 3: 创建HttpURLConnection，把上面的json写入请求体并发送HTTP POST请求
                // （注意：连接对象要命名为connection，下面判断状态码要用）
                URL url = new URL(SERVER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);

                }

                // 读取响应，状态码是200才是响应成功
                if (connection.getResponseCode() == 200) {
                    // TODO 4: 读取响应体并解析json，拿到取件码就打印取件码，
                    // 拿不到就打印msg的内容。响应样例：
                    // {"pick_code":"1234","msg":"success"}
                    // {"pick_code":null,"msg":"手机号不正确"}
                    try (InputStream is = connection.getInputStream()) {
                        String responsesBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        QueryResponse response = JSON.parseObject(responsesBody, QueryResponse.class);
                        if (response != null) {
                            System.out.println(response.getMsg() + "Express:" + response.getExpressKey());
                        }else{
                            System.out.println("查询失败，服务端相应错误。Express:null" );
                        }

                    }

                } else {
                    System.out.println("查询失败，状态码: " + connection.getResponseCode());
                }
                connection.disconnect();
            } catch (IOException e) {
                // 请求发生错误（比如Server没启动），打印错误信息后继续循环
                System.out.println("请求发生错误: " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("客户端已退出");
    }

    /** 读取响应体：把InputStream按UTF-8读成一个字符串 */
    private static String readBody(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
