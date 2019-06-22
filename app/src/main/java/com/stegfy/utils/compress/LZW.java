package com.stegfy.utils.compress;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LZW implements Coder {
    private Vector<Integer> tags;
    private Map<String, Integer> compressDict = new HashMap<>();
    private Map<Integer, String> decompressDict;


    LZW() {
        decompressDict = new HashMap<>();
        for (int i = 32; i < 128; i++) {
            compressDict.put(Character.toString((char) i), i);
            decompressDict.put(i, Character.toString((char) i));
        }
        tags = new Vector<>();
    }

    public String compress(String text) {

        int subStrSz, idx = 65, compressionKey = 128;
        String subStr;
        StringBuilder res=new StringBuilder();
        boolean compressionFinished = false;

        for (int i = 0; i < text.length() && !compressionFinished; ) {
            subStrSz = i + 1;
            while (true) {
                if (subStrSz > text.length()) {
                    compressionFinished = true;
                    tags.add(idx);
                    res.append(idx).append("#");
                    break;
                }
                subStr = text.substring(i, subStrSz);
                if (compressDict.containsKey(subStr)) {
                    idx = compressDict.get(subStr);
                    subStrSz++;
                } else if (!compressDict.containsKey(subStr)) {
                    compressDict.put(subStr, compressionKey);
                    tags.add(idx);
                    res.append(idx).append("#");
                    i = subStrSz - 1;
                    compressionKey++;
                    break;
                }
            }
        }
        return res.toString();
    }

    public String decompress(String s) {

        String[] comp= s.split("#");
        Integer[] icomp=new Integer[comp.length];
        for (int i = 0; i < comp.length; i++) {
            icomp[i]=Integer.parseInt(comp[i]);
        }

        int decompressionKey = 128;
        String text = "";
        String lastDecompressed = "", decompressed;
        for (Integer tag : icomp) {

            assert lastDecompressed != null;
            if (lastDecompressed.isEmpty()) {
                lastDecompressed = decompressDict.get(tag);
                text = lastDecompressed;
            } else {
                if (decompressDict.get(tag) == null) {
                    decompressDict.put(tag, lastDecompressed + lastDecompressed.charAt(0));
                }
                decompressed = decompressDict.get(tag);

                assert decompressed != null;
                decompressDict.put(decompressionKey, lastDecompressed + decompressed.charAt(0));
                lastDecompressed = decompressed;
                text += decompressed;
                decompressionKey++;
            }
        }
        return text;
    }
}
