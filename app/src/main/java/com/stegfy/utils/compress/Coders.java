package com.stegfy.utils.compress;

public class Coders {
    private static NoCompression none = new NoCompression();
    private static LZW lzw = new LZW();
    private static Arithmetic aritm = new Arithmetic();
    private static StdHuffman huff = new StdHuffman();

    public enum Algoritms {NONE, LZW, ARITM, HUFFMAN}


    public static Coder getCoder(Algoritms a) {
        switch (a) {
            case NONE:
                return none;

            case LZW:
                return lzw;


            case ARITM:
                return aritm;


            case HUFFMAN:
                return huff;

        }
        return none;
    }
}