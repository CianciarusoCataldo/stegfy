package com.stegfy.utils.compress;

import java.util.*;
import java.util.Map.Entry;

public class StdHuffman implements Coder{

    private List<Probability> probsList = new ArrayList<>();

    private Map<String, String> compressionCodes = new HashMap<>();

    private Map<String, String> decompressionCodes = new HashMap<>();


    public String compress(String text) {

        probsList.clear();
        calcProbabilty(text);
        constructTree();
        StringBuilder build=new StringBuilder();
        char letter;
        String binary;
        for (int i = 0; i < text.length(); i++) {
            letter = text.charAt(i);
            binary = compressionCodes.get(Character.toString(letter));
            build.append(binary).append(" ");
        }

        build.setLength(build.length()-1);
        build.append("#");

        for(Probability pb: probsList) {
            build.append((int) pb.getLetter()).append("&");
            build.append(pb.getProb()).append("@");
        }
        build.setLength(build.length()-1);
        return build.toString().replaceAll("\\s","");
    }


    public String decompress(String dec) {

        probsList.clear();
        compressionCodes.clear();
        String[] tmp=dec.split("#");
        String[] tmp2=tmp[1].split("@");
        for(String tmp3: tmp2) {
            String[] pb=tmp3.split("&");
            probsList.add(new Probability((char)Integer.parseInt(pb[0]), Double.parseDouble(pb[1])));
        }

        String compressedText = tmp[0];

        String text = "";
        constructTree();
        for (Entry<String, String> entry : compressionCodes.entrySet()) {
            decompressionCodes.put(entry.getValue(), entry.getKey());
        }
        for (int i = 0; i < compressedText.length(); ) {
            int subStrSz = 1;
            String subStr;
            while (true) {
                if (i + subStrSz > compressedText.length())
                    break;
                else
                    subStr = compressedText.substring(i, i + subStrSz);
                if (decompressionCodes.containsKey(subStr)) {
                    text += decompressionCodes.get(subStr);
                    i += subStrSz;
                    break;
                } else {
                    subStrSz++;
                }
            }
        }
        return text;
    }


    private void constructTree() {
        BinaryTree node = new BinaryTree();
        int sz = probsList.size();
        int probSz = probsList.size() - 1;

        while (true) {

            node.addRightNode();
            node.addLeftNode();

            for (int i = sz - probSz; i < sz; i++) {
                node.getRightNode().setProb(node.getRightNode().getProb() + probsList.get(i).getProb());
                node.getRightNode().setLetters(node.getRightNode().getLetters() + probsList.get(i).getLetter());
            }

            node.getRightNode().setBinary(node.getBinary() + "1");
            node.getLeftNode().setBinary(node.getBinary() + "0");
            node.getLeftNode().setLetters(Character.toString(probsList.get(sz - probSz - 1).getLetter()));
            node.getLeftNode().setProb(probsList.get(sz - probSz - 1).getProb());
            compressionCodes.put(node.getLeftNode().getLetters(), node.getLeftNode().getBinary());

            probSz--;

            if (node.getRightNode().getLetters().length() > 1)
                node = node.getRightNode();
            else if (node.getLeftNode().getLetters().length() > 1)
                node = node.getLeftNode();
            else {
                compressionCodes.put(node.getRightNode().getLetters(), node.getRightNode().getBinary());
                break;
            }
        }
    }


    private void calcProbabilty(String text) {

        Map<Character, Integer> freqOfLetter = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            Integer freq = freqOfLetter.get(letter);
            if (freq != null)
                freqOfLetter.put(letter, freq + 1);
            else
                freqOfLetter.put(letter, 1);
        }
        for (Entry<Character, Integer> entry : freqOfLetter.entrySet()) {
            Probability prob = new Probability(entry.getKey(), Double.valueOf(entry.getValue()) / text.length());
            probsList.add(prob);
            Collections.sort(probsList);
        }
    }
}
