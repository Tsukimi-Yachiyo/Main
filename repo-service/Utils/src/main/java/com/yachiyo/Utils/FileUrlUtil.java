package com.yachiyo.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cn.hutool.crypto.digest.DigestUtil.md5Hex;

@Component
public class FileUrlUtil {

    private static final String KEY = "yachiyo_file_url"+System.currentTimeMillis();

    @Autowired
    private IOFileUtils ioFileUtils;

    /**
     * 生成带签名的文件URL
     * @param fileName 文件名
     * @return 带签名的文件URL
     */
    public String generateFileUrl(String fileName, long expireSeconds) {
        long expire = System.currentTimeMillis() / 1000 + expireSeconds;
        String sign = md5Hex(fileName + expire + KEY);
        if (!ioFileUtils.checkDirExist(fileName)) {
            return "/file/" + fileName + "?expire=" + expire + "&sign=" + sign;
        }else  {
            return null;
        }
    }

    /**
     * 校验签名是否合法
     */
    public boolean verify(String filename, long expire, String sign) {
        if (System.currentTimeMillis() / 1000 > expire) return false;
        String realSign = md5Hex(filename + expire + KEY);
        return realSign.equalsIgnoreCase(sign);
    }
}
