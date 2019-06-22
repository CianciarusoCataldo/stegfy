package com.stegfy.utils.compress;

public class NoCompression implements Coder {

    public String compress(String s){return s;}

    public String decompress(String s){return s;}
}
