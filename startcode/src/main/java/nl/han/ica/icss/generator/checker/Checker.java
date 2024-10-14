package nl.han.ica.icss.generator.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.HANListNode;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private HashMap<String, ExpressionType> map = new HashMap<>();;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();

        checkStylesheet(ast.root);

    }

    private void checkStylesheet(Stylesheet node){
        for (ASTNode child : node.getChildren()){

            if (child instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) child, map);
            }

            if (child instanceof Stylerule){
                checkStylerule((Stylerule) child);
            }
        }
    }

    private void checkVariableAssignment(VariableAssignment node, HashMap<String, ExpressionType> map){
        if (node.expression == null){
            node.setError("Variable assignment must have an expression");
        } else {
            map.put(node.name.name, getExpressionType(node.expression));
            variableTypes.addFirst(map);
        }
    }

    private ExpressionType getExpressionType(Expression expression) {
        ExpressionType type = null;

        if (expression instanceof ColorLiteral){
            type = ExpressionType.COLOR;

        } else if (expression instanceof PixelLiteral){
            type = ExpressionType.PIXEL;
        }

        return type;
    }

    private void checkStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkDeclaration(Declaration node) {
        if (node.property.name.equals("width")) {
            if (node.expression instanceof VariableReference) {
                HashMap <String, ExpressionType> map = variableTypes.get(0);
                String varName = ((VariableReference) node.expression).name;
                ExpressionType type = map.get(varName);

                if (!type.equals(ExpressionType.PIXEL)) {
                    node.setError("Property 'width' has wrong type");
                }
            } else if (!(node.expression instanceof PixelLiteral)) {
                node.setError("Property 'width' has wrong type");
            }

        } else if (node.property.name.equals("color")) {
            if (node.expression instanceof VariableReference) {
                HashMap <String, ExpressionType> map = variableTypes.get(0);
                String varName = ((VariableReference) node.expression).name;
                ExpressionType type = map.get(varName);

                if (!type.equals(ExpressionType.COLOR)) {
                    node.setError("Property 'color' has wrong type");
                }
            } else if (!(node.expression instanceof ColorLiteral)) {
                node.setError("Property 'color' has wrong type");
            }
        }
    }

}
