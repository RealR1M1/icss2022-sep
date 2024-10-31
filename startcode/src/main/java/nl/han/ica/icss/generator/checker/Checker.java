package nl.han.ica.icss.generator.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet node) {
        // Add new scope
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child, variableTypes.getFirst());
            }
            if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
        variableTypes.removeFirst();
    }

    private void checkVariableAssignment(VariableAssignment node, HashMap<String, ExpressionType> scope) {
        if (node.expression == null) {
            node.setError("Variable assignment must have an expression");
        } else {
            scope.put(node.name.name, checkExpressionType(node.expression));
        }
    }

    private ExpressionType checkVariableReference(VariableReference node) {
        if (variableTypes.getSize() > 0 && variableTypes.getFirst().containsKey(node.name)) {
            return variableTypes.getFirst().get(node.name);
        } else if (variableTypes.getSize() >= 1 || variableTypes.get(variableTypes.getSize() - 1).containsKey(node.name)){
            return variableTypes.get(variableTypes.getSize() - 1).get(node.name);
        } else {
            return ExpressionType.UNDEFINED;
        }
    }

    private void checkStylerule(Stylerule node) {
        // Add new scope
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child, variableTypes.getFirst());
            }
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
            if (child instanceof IfClause) {
                checkIfStatement((IfClause) child);
            }
        }
        // Delete scope
        variableTypes.removeFirst();
    }

    private void checkDeclaration(Declaration node) {
        switch (node.property.name) {
            case "width":
                if (checkExpression(node.expression) != ExpressionType.PIXEL){
                    node.setError("width must be defined in pixels");
                }
                break;
            case "height":
                if (checkExpression(node.expression) != ExpressionType.PIXEL){
                    node.setError("height must be defined in pixels");
                }
                break;
            case "color":
                if (checkExpression(node.expression) != ExpressionType.COLOR){
                    node.setError("color must be defined in hexcode");
                }
                break;
            case "background-color":
                if (checkExpression(node.expression) != ExpressionType.COLOR){
                    node.setError("background color must be defined in hexcode");
                }
                break;
            default:
                node.setError("unsupported property name");
        }
    }

    private ExpressionType checkExpression(Expression node) {
        if (node instanceof Operation) {
            return checkOperation((Operation) node);
        } else {
            return checkExpressionType(node);
        }
    }

    private ExpressionType checkOperation(Operation node) {

        ExpressionType left;
        ExpressionType right;

        //check if children are also operations
        if (node.lhs instanceof Operation) {
            left = checkOperation((Operation) node.lhs);
        } else {
            left = checkExpressionType(node.lhs);
        }

        if (node.rhs instanceof Operation) {
            right = checkOperation((Operation) node.rhs);
        } else {
            right = checkExpressionType(node.rhs);
        }

        if (node.lhs instanceof ColorLiteral || node.lhs instanceof BoolLiteral || node.rhs instanceof ColorLiteral || node.rhs instanceof BoolLiteral) {
            node.setError("Colors and Booleans are not allowed in operations");
        }

        if (node instanceof MultiplyOperation) { //check if one of the children is a scalar value
            if (node.lhs instanceof ScalarLiteral && node.rhs instanceof ScalarLiteral) {
                node.setError("Expression must have at least one scalar literal");
            } else if (!(node.lhs instanceof ScalarLiteral) && !(node.rhs instanceof ScalarLiteral)) {
                node.setError("Expression must have at least one scalar literal");
            }
            if (right != ExpressionType.SCALAR) {
                return right;
            } else {
                return left;
            }

        } else {// check if both sides are the same literal
            if (!left.equals(right)) {
                node.setError("Expressions must have the same type");
            }
        }
        return left;
    }

    private void checkIfStatement(IfClause node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableReference) {
                ExpressionType type = checkVariableReference((VariableReference) child);
                if (!type.equals(ExpressionType.BOOL)) {
                    node.setError("Statement must have boolean value");
                }
            }
        }
    }

    public ExpressionType checkExpressionType(Expression expression) {
        if (expression instanceof VariableReference) {
            return checkVariableReference((VariableReference) expression);
        } else {
            if (expression instanceof PercentageLiteral) {
                return ExpressionType.PERCENTAGE;
            } else if (expression instanceof PixelLiteral) {
                return ExpressionType.PIXEL;
            } else if (expression instanceof ColorLiteral) {
                return ExpressionType.COLOR;
            } else if (expression instanceof ScalarLiteral) {
                return ExpressionType.SCALAR;
            } else if (expression instanceof BoolLiteral) {
                return ExpressionType.BOOL;
            }
        }
        return ExpressionType.UNDEFINED;
    }
}
