package nl.han.ica.datastructures;

public class HANListNode<ASTNode> {
    ASTNode data;
    HANListNode<ASTNode> next;

    public HANListNode(ASTNode data, HANListNode<ASTNode> next) {
        this.data = data;
        this.next = next;
    }

    public ASTNode getData(){
        return data;
    }

    public void setData(ASTNode data){
        this.data = data;
    }

    public HANListNode<ASTNode> getNext(){
        return next;
    }

    public void setNext(HANListNode<ASTNode> next){
        this.next = next;
    }
}
