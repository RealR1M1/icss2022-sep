package nl.han.ica.datastructures;

public class HANLinkedList<ASTNode> implements IHANLinkedList<ASTNode> {
    HANListNode<ASTNode> header;
    private int size;

    public HANLinkedList() {
        header = new HANListNode<>(null, null);
        size = 1;
    }

    @Override
    public void addFirst(ASTNode value) {
        HANListNode<ASTNode> newNode = new HANListNode<>(value, null);
        newNode.setNext(header.getNext());
        header.setNext(newNode);
        size++;
    }

    @Override
    public void clear() {
        header.next = null;
    }

    @Override
    public void insert(int index, ASTNode value) {
        HANListNode<ASTNode> newNode = new HANListNode<>(value, null);
        HANListNode<ASTNode> currentNode = header;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.getNext();
        }
        currentNode.setNext(newNode);
        size++;
    }

    @Override
    public void delete(int pos) {
        if (size == 1) {
            throw new IllegalArgumentException("List is empty");
        }
        HANListNode<ASTNode> currentNode = header;
        while (currentNode.getNext().getNext() != null) {
            currentNode = currentNode.getNext();
        }
        currentNode.setNext(null);
        size--;
    }

    @Override
    public ASTNode get(int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }
        HANListNode<ASTNode> currentNode = header.getNext();
        for (int i = 0; i < pos; i++) {
            currentNode = currentNode.getNext();
        }
        return currentNode.getData();
    }

    @Override
    public void removeFirst() {
        if (size == 1) {
            throw new IllegalStateException("List is empty");
        }
        header.setNext(header.getNext().getNext());
        size--;
    }

    @Override
    public ASTNode getFirst() {
        return header.getNext().getData();
    }

    @Override
    public int getSize() {
        return size;
    }
}