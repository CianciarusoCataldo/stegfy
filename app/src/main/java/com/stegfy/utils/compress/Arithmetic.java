package com.stegfy.utils.compress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Arithmetic implements Coder{

    private List<Probability> probsList=new ArrayList<>();
    private Map<Character, Range> basicRanges=new HashMap<>();
    private double lower;
    private double higher;

    public String compress(String text) {
        probsList.clear();
        basicRanges.clear();
        probsList=calcProbabilty(text);
        basicRanges = calcBasicRanges(probsList);
        lower = 0.0;
        higher = 1.0;
        double compressedValue=0.0;
        for (int i = 0; i < text.length(); i++) {
            Double range = higher - lower;
            Character letter = text.charAt(i);
            higher = lower + (range * Objects.requireNonNull(basicRanges.get(letter)).getHigher());
            lower = lower + (range * Objects.requireNonNull(basicRanges.get(letter)).getLower());
            System.out.println(lower + "  " + higher);
            compressedValue = (lower + higher) / 2;
        }

        StringBuilder b = new StringBuilder();
        for(Probability pb: probsList){
            b.append(pb.getLetter());
            b.append(" ").append(pb.getProb()).append(",");
        }
        return compressedValue +"-"+text.length()+"-"+ b.toString();

    }

    public String decompress(String decmp) {
        String decomprssedText="";
        probsList.clear();
        basicRanges.clear();
        String[] tmp=decmp.split("-");
        double code = Double.valueOf(tmp[0]);
        int length=Integer.valueOf(tmp[1]);
        String[] tmp2=tmp[2].split(",");
        for(String tmp3: tmp2){
            String[] prb=tmp3.split(" ");
            probsList.add(new Probability(prb[0].charAt(0),Double.valueOf(prb[1])));
        }
        basicRanges = calcBasicRanges(probsList);
        lower = 0.0;
        higher = 1.0;
        for (int i = 0; i < length; i++) {
            code = ((code - lower) / (higher - lower));
            System.out.println("code: " + code);
            for (Map.Entry<Character, Range> entry : basicRanges.entrySet()) {
                Double l = entry.getValue().getLower();
                Double h = entry.getValue().getHigher();
                if (code > l && code < h) {
                    lower = l;
                    higher = h;
                    decomprssedText += entry.getKey();
                    break;
                }
            }
        }

        return decomprssedText;
    }

    private Map<Character, Range> calcBasicRanges(List<Probability> probsList) {
        Map<Character, Range> basicRanges = new HashMap<>();
        double lr = 0.0;
        for (Probability p : probsList) {
            Range r = new Range();
            r.setLower(lr);
            r.setHigher(lr + p.getProb());
            basicRanges.put(p.getLetter(), r);
            lr += p.getProb();
        }
        return basicRanges;
    }


    private List<Probability> calcProbabilty(String text) {
        List<Probability> probsList=new ArrayList<>();
        Map<Character, Integer> freqOfLetter = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            Integer freq = freqOfLetter.get(letter);
            if (freq != null)
                freqOfLetter.put(letter, freq + 1);
            else
                freqOfLetter.put(letter, 1);
        }
        for (Map.Entry<Character, Integer> entry : freqOfLetter.entrySet()) {
            Probability prob = new Probability(entry.getKey(), Double.valueOf(entry.getValue()) / text.length());
            probsList.add(prob);
        }

        return probsList;

    }

}
