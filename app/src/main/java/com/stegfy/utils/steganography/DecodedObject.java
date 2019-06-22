package com.stegfy.utils.steganography;

import com.stegfy.PasswordMissingException;
import com.stegfy.utils.encrypt.Encryptor;

import java.io.File;
import java.io.IOException;

import static com.stegfy.Constants.end;
import static com.stegfy.Constants.midWithOutPwd;
import static com.stegfy.Constants.midWithPwd;

public class DecodedObject {
    private final byte[] bytes;
    private String key = "";

    DecodedObject(byte[] bytes, String key) {
        // The base for this decoded class is a byte array of the decoded data
        this.bytes = bytes;
        this.key = key;
    }

    public byte[] intoByteArray() {
        return bytes;
    }

    public String intoString() throws Exception{
        String data = new String(bytes);
        return getSecretMessage(data);
    }

    private String getSecretMessage(String data) throws Exception{
        String mid = data.indexOf(midWithPwd) != -1 ? midWithPwd : midWithOutPwd;
        if(mid == midWithPwd && key.isEmpty()){
            throw new PasswordMissingException();
        }
        String [] seperatedData = data.split(end, 2);
        String [] splitedAdditionalData = seperatedData[0].split(mid, 2);
        String encryptionAlgo = splitedAdditionalData[0];
        String hashingAlgo = splitedAdditionalData[1];
        key = (mid == midWithOutPwd) ? seperatedData[0] + end : key;
        return Encryptor.decrypt(seperatedData[1], key, encryptionAlgo, hashingAlgo);
    }

    public File intoFile(String path) throws IOException {
        return intoFile(new File(path));
    }

    public File intoFile(File file){
        // FIXME:
        if (1 == 1) {
            throw new RuntimeException("Not implemented yet");
        }

        return file;
    }
}
