package com.os.crossdomain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

class IOUtils {

    public static String getResourceAsString(String path) throws IOException {
        InputStream stream = IOUtils.class.getResourceAsStream(path);
        StringBuilder sb = new StringBuilder();
        String line;

        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        while ((line = br.readLine()) != null) {
            sb.append(line + "\r\n");
        }
        return sb.toString();
    }

    public static String replace(String content, Map<String, String> map) {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            content = content.replaceAll("\\$" + key, map.get(key) == null ? "" : map.get(key));
        }
        return content;
    }
}
