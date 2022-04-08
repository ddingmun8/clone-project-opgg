package com.gnar.cloneprojectopgg;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * GRUtils
 */
public class GRUtils {
    //URL Encoding
    public String URLEncode(String url) {
        String enUrl = "";
        try {
            enUrl = URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enUrl;
    }
    

    //URL Ddcoding
    public String URLDecode(String url) {
        String deUrl = "";
        try {
            deUrl = URLDecoder.decode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deUrl;
    }
}