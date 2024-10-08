package nl.han.ica.datastructures;

import java.util.EmptyStackException;

public class HANStack<ASTNode> implements IHANStack<ASTNode>{
    private HANLinkedList<ASTNode> list;

    public HANStack() {
        list = new HANLinkedList<>();
    }

    @Override
    public void push(ASTNode value) {
        list.addFirst(value);
    }

    @Override
    public ASTNode pop() {
        if (list.getSize() == 1){
            throw new EmptyStackException();
        }
        ASTNode top = list.getFirst();
        list.removeFirst();
        return top;
    }

    @Override
    public ASTNode peek() {
        if (list.getSize() == 1){
            throw new EmptyStackException();
        }
        return list.getFirst();
    }
}
