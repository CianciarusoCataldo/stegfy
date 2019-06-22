package com.stegfy.utils.compress;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class FileOperations {


    void writeDataToFile(String fileName, String data) throws IOException {
        File file = new File(fileName);
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
        try {
            os.writeBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        os.close();
    }


    String readFile(String fileName) throws IOException {

        File file = new File(fileName);
        byte[] data;
        try (FileInputStream fis = new FileInputStream(file)) {
            data = new byte[(int) file.length()];
            fis.read(data);
        }

        return new String(data, StandardCharsets.UTF_8);
    }
}
