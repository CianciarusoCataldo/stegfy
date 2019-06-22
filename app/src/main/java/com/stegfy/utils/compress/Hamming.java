package com.stegfy.utils.compress;

public class Hamming implements Coder {


    private final String NL = "|" + System.lineSeparator();

    private int parityCount;
    private int[] signal;
    private int[] generatedCode;

    public Hamming(BitSignal signal) {
        super();
        parityCount = 0;
        this.signal = signal.getBits();
        generateCode();
    }

    @Override
    public String compress(String c) {
        return null;
    }

    @Override
    public String decompress(String d) {
        return null;
    }

    private void generateCode() {

        int i = 0;

        while (i < signal.length) {

            int poweredPos = (int) Math.pow(2, parityCount);

            if (poweredPos == parityCount + i + 1)
                parityCount++;
            else
                i++;
        }

        this.generatedCode = new int[signal.length + parityCount];

        allocateBits(generatedCode);
        allocateParityBits(generatedCode);
    }

    private void allocateParityBits(int[] code) {
        for (int i = 0; i < parityCount; i++) {
            code[((int) Math.pow(2, i)) - 1] = getParity(code, i);
        }
    }

    private void allocateBits(int[] code) {

        int j = 0, k = 0;

        for (int i = 1; i <= code.length; i++) {
            if (Math.pow(2, j) == i) {
                code[i - 1] = 2;
                j++;
            } else {
                code[k + j] = signal[k++];
            }
        }
    }

    private int getParity(int[] bits, int power) {

        int parity = 0;

        for (int i = 0; i < bits.length; i++) {
            if (bits[i] != 2) {
                int k = i + 1;

                String s = Integer.toBinaryString(k);

                int x = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;

                if (x == 1) {
                    if (bits[i] == 1) {
                        parity = (parity + 1) % 2;
                    }
                }
            }
        }
        return parity;
    }

    public void receive() {

        int power;
        int parity[] = new int[parityCount];

        String errorLocation = new String();

        for (power = 0; power < parityCount; power++) {
            for (int i = 0; i < this.generatedCode.length; i++) {

                int k = i + 1;

                String s = Integer.toBinaryString(k);

                int bit = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;

                if (bit == 1) {
                    if (this.generatedCode[i] == 1) {
                        parity[power] = (parity[power] + 1) % 2;
                    }
                }
            }
            errorLocation = parity[power] + errorLocation;
        }

        int error_location = Integer.parseInt(errorLocation, 2);
        if (error_location != 0) {

            System.out.println(NL + "| >>> Erro detectado na posição " + error_location);
            generatedCode[error_location - 1] = (generatedCode[error_location - 1] + 1) % 2;
            System.out.println("| >>> O Código correto é: " + getGenerateCode());

        } else {
            System.out.println(NL + "| Não houveram erros com a detecção de dados");
        }

        System.out.print(NL + "| O dado original enviado é: ");
        power = parityCount - 1;
        for (int i = generatedCode.length; i > 0; i--) {
            if (Math.pow(2, power) != i) {
                System.out.print(generatedCode[i - 1]);
            } else {
                power--;
            }
        }
        System.out.println();
    }

    public void throwError(int error) {

        if (error != 0) {
            this.generatedCode[error - 1] = (this.generatedCode[error - 1] + 1) % 2;
        }

        System.out.println(NL + "| O Código Enviado é: " + getGenerateCode());

        receive();
    }

    public String getGenerateCode() {
        String code = "";
        for (int i = 0; i < generatedCode.length; i++) {
            code += Integer.toString(generatedCode[generatedCode.length - i - 1]);
        }
        return code;
    }
}
