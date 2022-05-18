package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    public static String sendGet(String url) throws IOException {
        return HttpUtil.sendGet(url, new HashMap<>());
    }

    /**
     *
     * @param url 请求地址
     * @param headers 请求头
     * @return
     * @throws IOException
     */
    public static String sendGet(String url, Map<String,String> headers) throws IOException {
        String msg = null;
        HttpURLConnection httpURLConnection = null;
        URL requestUrl = new URL(url);
        try {
            httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == 200) { // 正常响应
                // 从流中读取响应信息
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) { // 循环从流中读取
                    msg += line + "\n";
                }
                reader.close(); // 关闭流
            }

        } catch (Exception e) {

        } finally {
            // 断开连接，释放资源
            if (null != httpURLConnection){
                try {
                    httpURLConnection.disconnect();
                } catch (Exception e){

                }
            }
        }
        return msg;
    }
}
