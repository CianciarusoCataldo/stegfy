package com.stegfy.utils.compress;

public class Node {

    private String binary;
    private char sympol;
    private int nodeNum;
    private int counter;

    private Node leftNode;
    private Node rightNode;
    private Node parent;

    Node() {

        binary = "";
        nodeNum = 0;
        counter = 0;
        sympol = '\u0000';

        leftNode = null;
        rightNode = null;
        parent = null;

    }

    void addLeft(int num, String b) {

        Node node = new Node();

        node.nodeNum = num;
        node.binary = b;
        node.counter = 0;

        this.leftNode = node;
        node.parent = this;
    }

    void addRight(char s, int num, String b) {

        Node node = new Node();

        node.sympol = s;
        node.nodeNum = num;
        node.binary = b;
        node.counter = 1;

        node.parent = this;
        this.rightNode = node;

    }

    public Node get(char sympol, Node root) {
        Node node = new Node();
        node.setSympol(sympol);
        return search(node, root);
    }

    private Node search(Node searchNode, Node node) {
        if (node != null) {
            if (searchNode.getSympol() == node.getSympol() && node.getLeftNode() == null
                    && node.getRightNode() == null) {
                return node;
            } else {
                Node foundNode = search(searchNode, node.getLeftNode());
                if (foundNode == null)
                    foundNode = search(searchNode, node.getRightNode());
                return foundNode;
            }
        } else
            return null;
    }

    Node find(String code, Node root) {
        Node node = new Node();
        node.setBinary(code);
        return searchBinary(node, root);
    }

    private Node searchBinary(Node searchNode, Node node) {
        if (node != null) {
            if (searchNode.getBinary().equals(node.getBinary())
                    && node.getLeftNode() == null
                    && node.getRightNode() == null) {
                return node;
            } else {
                Node foundNode = searchBinary(searchNode, node.getLeftNode());
                if (foundNode == null)
                    foundNode = searchBinary(searchNode, node.getRightNode());
                return foundNode;
            }
        } else
            return null;
    }

    void traverse(Node root) {

        if (root.getLeftNode() != null) {
            traverse(root.getLeftNode());
        }
        System.out.println(root.getSympol() + " num: " + root.getNodeNum() + " counter: " + root.getCounter() + " binary: " + root.getBinary());
        if (root.getRightNode() != null) {
            traverse(root.getRightNode());
        }
    }

    boolean checkForSwap(Node node, Node root) {
        Node swapNode = node;
        swapNode = findSwapNode(node, root, swapNode);
        if (swapNode == node)
            return false;
        else {
            String constBinary = swapNode.getBinary();
            int constNodeNum = swapNode.getNodeNum();
            boolean swapLeft = false;
            if (swapNode.getParent().getLeftNode() == swapNode)
                swapLeft = true;
            Node swapParent = swapNode.getParent();
            if (node.getParent().getLeftNode() == node) {
                node.getParent().setLeftNode(swapNode);
                swapNode.setParent(node.getParent());
            } else {
                node.getParent().setRightNode(swapNode);
                swapNode.setParent(node.getParent());
            }
            if (swapLeft) {
                swapParent.setLeftNode(node);
                node.setParent(swapParent);
            } else {
                swapParent.setRightNode(node);
                node.setParent(swapParent);
            }
            swapNode.setNodeNum(node.getNodeNum());// a right then left
            swapNode.setBinary(node.getBinary());
            node.setNodeNum(constNodeNum);
            node.setBinary(constBinary);
            changeBinaryCode(root);
            return true;
        }
    }

    private void changeBinaryCode(Node root) {
        if (root.getLeftNode() != null) {
            root.getLeftNode().setBinary(root.getBinary() + "0");
            changeBinaryCode(root.getLeftNode());
        }
        if (root.getRightNode() != null) {
            root.getRightNode().setBinary(root.getBinary() + "1");
            changeBinaryCode(root.getRightNode());
        }
    }

    private Node findSwapNode(Node node, Node searchNode, Node swapNode) {

        if (searchNode.getCounter() <= swapNode.getCounter() &&
                searchNode.getNodeNum() > swapNode.getNodeNum() &&
                searchNode != node.getParent()) {
            swapNode = searchNode;
        }
        if (searchNode.getLeftNode() != null) {
            swapNode = findSwapNode(node, searchNode.getLeftNode(), swapNode);
        }
        if (searchNode.getRightNode() != null) {
            swapNode = findSwapNode(node, searchNode.getRightNode(), swapNode);
        }
        return swapNode;
    }

    String getBinary() {
        return binary;
    }

    private void setBinary(String binary) {
        this.binary = binary;
    }

    char getSympol() {
        return sympol;
    }

    private void setSympol(char sympol) {
        this.sympol = sympol;
    }

    private int getNodeNum() {
        return nodeNum;
    }

    private void setNodeNum(int nodeNum) {
        this.nodeNum = nodeNum;
    }

    int getCounter() {
        return counter;
    }

    void setCounter(int counter) {
        this.counter = counter;
    }

    Node getLeftNode() {
        return leftNode;
    }

    private void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    private Node getRightNode() {
        return rightNode;
    }

    private void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    Node getParent() {
        return parent;
    }

    private void setParent(Node parent) {
        this.parent = parent;
    }


}
