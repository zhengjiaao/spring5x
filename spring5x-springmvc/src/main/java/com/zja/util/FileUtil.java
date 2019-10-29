package com.zja.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author ZhengJa
 * @description 文件操作工具类
 * @data 2019/10/22
 */
public class FileUtil {
    public static String saveFile(MultipartFile file, String path) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        String fullPath = path + File.separator + file.getOriginalFilename();
        try {
            File saveDir = new File(path);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            outputStream = new FileOutputStream(new File(fullPath));
            inputStream = file.getInputStream();
            byte[] bytes = new byte[1024 * 1024];
            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fullPath;
    }

    /**
     *file 转 byte[]
     * @param file
     * @return byte[]
     */
    public static byte[] readFileToByteArray(File file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[5 * 1024 * 1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }
}
