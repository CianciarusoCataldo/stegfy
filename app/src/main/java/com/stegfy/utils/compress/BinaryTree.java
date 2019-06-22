package com.stegfy.utils.compress;

public class BinaryTree {

    private String binary;
    private String letters;
    private double prob;

    private BinaryTree leftNode;
    private BinaryTree rightNode;

    BinaryTree() {
        // TODO Auto-generated constructor stub

        binary = "";
        letters = "";
        prob = 0;
    }


    void addRightNode() {
        this.rightNode = new BinaryTree();
    }

    void addLeftNode() {
        this.leftNode = new BinaryTree();
    }


    String getBinary() {
        return binary;
    }

    String getLetters() {
        return letters;
    }

    double getProb() {
        return prob;
    }


    void setBinary(String binary) {
        this.binary = binary;
    }


    void setLetters(String letters) {
        this.letters = letters;
    }


    void setProb(double prob) {
        this.prob = prob;
    }


    BinaryTree getLeftNode() {
        return leftNode;
    }


    public void setLeftNode(BinaryTree leftNode) {
        this.leftNode = leftNode;
    }


    BinaryTree getRightNode() {
        return rightNode;
    }


    public void setRightNode(BinaryTree rightNode) {
        this.rightNode = rightNode;
    }

}
