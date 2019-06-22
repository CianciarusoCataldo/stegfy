package com.stegfy.utils.compress;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.stegfy.utils.compress.Node;
import com.stegfy.utils.compress.FileOperations;

public class AdabtiveHuffman {

    private Node compRoot = new Node();
    private Node decompRoot = new Node();
    private Node compNode, deCompNode;
    private String compText = "";

    private Map<Character, String> compShortCode = new HashMap<Character, String>();
    private Map<String, Character> decompShortCode = new HashMap<String, Character>();

    private AtomicInteger compNodeNum = new AtomicInteger(99);
    private AtomicInteger decompNodeNum = new AtomicInteger(99);

    private FileOperations fileOps = new FileOperations();

    public AdabtiveHuffman() {

        for (int i = 32; i < 128; i++) {
            compShortCode.put((char) i, Integer.toBinaryString(i)); // ASCII codes
        }
        compShortCode.put((char) 10, Integer.toBinaryString(10)); // new line character

        for (int i = 32; i < 128; i++) {
            decompShortCode.put(Integer.toBinaryString(i), (char) i); // ASCII codes
        }
        decompShortCode.put(Integer.toBinaryString(10), (char) 10); // new line character
    }

    public void compress(String fileName) throws IOException {
        String text = fileOps.readFile(fileName);
        compNode = compRoot;
        for (int i = 0; i < text.length(); i++) {
            char symbol = text.charAt(i);
            compNode = updateCompressTree(symbol, compNode, compRoot, compNodeNum);
        }
        fileOps.writeDataToFile("E:\\FCI\\Third year\\First term\\Multimedia\\Testing Files\\compressedAdvHuff.txt", compText);
        compRoot.traverse(compRoot);
    }

    public void decompress() throws IOException {
        compText = fileOps.readFile("E:\\FCI\\Third year\\First term\\Multimedia\\Testing Files\\compressedAdvHuff.txt");
        char symbol;
        String decompText = "";
        int codeSize;
        deCompNode = decompRoot;
        String code = compText.substring(0, 7);
        symbol = decompShortCode.get(code);
        decompText += symbol;
        deCompNode.addRight(symbol, decompNodeNum.getAndDecrement(), deCompNode.getBinary() + "1");
        deCompNode.addLeft(decompNodeNum.getAndDecrement(), deCompNode.getBinary() + "0");
        deCompNode.setCounter(1);
        deCompNode = deCompNode.getLeftNode();
        for (int i = 7; i < compText.length(); ) {
            codeSize = 1;
            while (true) {
                Node goToParentNode = null;
                code = compText.substring(i, i + codeSize);
                Node searchNode = deCompNode.find(code, decompRoot);
                if (searchNode == null) {
                    codeSize++;
                } else {
                    if (searchNode.getSympol() == 0) { // NYT node
                        code = compText.substring(i + codeSize, i + codeSize + 7);
                        symbol = decompShortCode.get(code);
                        decompText += symbol;
                        i += codeSize + 7;
                        deCompNode.addRight(symbol, decompNodeNum.getAndDecrement(), deCompNode.getBinary() + "1");
                        deCompNode.addLeft(decompNodeNum.getAndDecrement(), deCompNode.getBinary() + "0");
                        deCompNode.setCounter(1);
                        goToParentNode = deCompNode;
                        deCompNode = deCompNode.getLeftNode();
                    } else {
                        goToParentNode = searchNode;
                        deCompNode.checkForSwap(searchNode, decompRoot);
                        searchNode.setCounter(searchNode.getCounter() + 1);
                        decompText += searchNode.getSympol();
                        i += codeSize;
                    }
                    while (true) {
                        if (goToParentNode.getParent() == null) { // if reached root
                            break;
                        } else {
                            goToParentNode = goToParentNode.getParent();
                            if (goToParentNode.getParent() != null)
                                deCompNode.checkForSwap(goToParentNode, decompRoot);
                            goToParentNode.setCounter(goToParentNode.getCounter() + 1);
                        }
                    }
                    break;
                }
            }
        }
        decompRoot.traverse(decompRoot);
        fileOps.writeDataToFile("E:\\FCI\\Third year\\First term\\Multimedia\\Testing Files\\decompressedAdvHuff.txt", decompText);
        System.out.println(decompText);
    }

    private Node updateCompressTree(char symbol, Node node, Node root, AtomicInteger nodeNum) {

        Node goToParentNode;
        Node searchNode = node.get(symbol, root);
        if (searchNode != null) {
            goToParentNode = searchNode;
            send(searchNode.getBinary());
            node.checkForSwap(searchNode, root);
            searchNode.setCounter(searchNode.getCounter() + 1);
        } else {
            node.addRight(symbol, nodeNum.getAndDecrement(), node.getBinary() + "1");
            node.addLeft(nodeNum.getAndDecrement(), node.getBinary() + "0");
            node.setCounter(1);
            send(node.getBinary());
            send(compShortCode.get(symbol));
            goToParentNode = node;
            node = node.getLeftNode();
        }
        while (true) {
            if (goToParentNode.getParent() == null) { // if reached root
                break;
            } else {
                goToParentNode = goToParentNode.getParent();
                if (goToParentNode.getParent() != null)
                    node.checkForSwap(goToParentNode, root);
                goToParentNode.setCounter(goToParentNode.getCounter() + 1);
            }
        }
        return node;
    }

    public void send(String code) {
        //System.out.println(code);
        compText += code;
    }

}
