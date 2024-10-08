package nl.han.ica.datastructures;

import java.util.Iterator;

public class HANLinkedListIterator<ASTNode> implements Iterator<ASTNode> {
    private HANListNode<ASTNode> current;

    public HANLinkedListIterator(HANListNode<ASTNode> head) {
        this.current = head;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public ASTNode next() {
        ASTNode data = current.getData();

        current = current.getNext();
        return data;
    }
}
