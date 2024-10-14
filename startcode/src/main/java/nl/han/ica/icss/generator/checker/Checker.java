package nl.han.ica.icss.generator.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        // variableTypes = new HANLinkedList<>();

        checkStylesheet(ast.root);

    }

    private void checkStylesheet(Stylesheet node){
        checkStylerule((Stylerule) node.getChildren().get(0));
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
            if (!(node.expression instanceof PixelLiteral)) {
                node.setError("Property 'width' has wrong type");
            }
        } else if (node.property.name.equals("color")) {
            if (!(node.expression instanceof ColorLiteral)) {
                node.setError("Property 'color' has wrong type");
            }
        }
    }

}
