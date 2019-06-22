package com.stegfy.utils.compress;

import java.util.Arrays;

public class BitSignal {

    private int size;
    private int[] bits;

    public BitSignal(int size) {
        super();
        this.size = size;
        this.bits = new int[size];
    }

    public void insertBits(String bits) {
        validateSignal(bits);
        for (int i = 0; i < bits.length(); i++) {
            this.bits[i] = Integer.parseInt(String.valueOf(bits.charAt(i)));
        }
    }

    public int[] getBits() {
        return this.bits;
    }

    @Override
    public String toString() {
        return Arrays.toString(bits);
    }

    private void validateSignal(String signal) {
        if (this.size != signal.length() || !signal.matches("[0-1]+")) {
            throw new IllegalArgumentException("Entrada Inválida");
        }
    }
}
