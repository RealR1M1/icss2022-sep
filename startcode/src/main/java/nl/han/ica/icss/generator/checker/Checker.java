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
    private HashMap<String, ExpressionType> map = new HashMap<>();

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {

            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child, map);
            }

            if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
    }

    private void checkVariableAssignment(VariableAssignment node, HashMap<String, ExpressionType> map) {
        if (node.expression == null) {
            node.setError("Variable assignment must have an expression");
        } else {
            map.put(node.name.name, getExpressionType(node.expression));
            variableTypes.addFirst(map);
        }
    }

    private boolean checkVariableReference(VariableReference node, ExpressionType expressionType) {
        HashMap<String, ExpressionType> map = variableTypes.get(0);
        String varName = node.name;
        ExpressionType type = map.get(varName);

        return type.equals(expressionType);
    }

    private ExpressionType getExpressionType(Expression expression) {
        ExpressionType type = null;

        if (expression instanceof ColorLiteral) {
            type = ExpressionType.COLOR;
        } else if (expression instanceof PixelLiteral) {
            type = ExpressionType.PIXEL;
        } else if (expression instanceof ScalarLiteral) {
            type = ExpressionType.SCALAR;
        } else if (expression instanceof BoolLiteral) {
            type = ExpressionType.BOOL;
        }

        return type;
    }

    private void checkStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
            if (child instanceof IfClause) {
                checkIfStatement((IfClause) child);
            }
        }
    }

    private void checkDeclaration(Declaration node) {
        if (node.property.name.contains("width") || node.property.name.contains("height")) {
            for (ASTNode child : node.getChildren()) {
                if (child instanceof Expression) {
                    checkExpression((Expression) child, ExpressionType.PIXEL);
                }
            }
        } else if (node.property.name.contains("color")) {
            for (ASTNode child : node.getChildren()) {
                if (child instanceof Expression) {
                    checkExpression((Expression) child, ExpressionType.COLOR);
                }
            }
        }
    }

    private void checkExpression(Expression node, ExpressionType expressionType) {
        if (node instanceof Operation) {
            checkOperation((Operation) node, expressionType);
        } else {
            if (node instanceof VariableReference) {
                System.out.println(node);
                if (!(checkVariableReference((VariableReference) node, expressionType))) {
                    node.setError("Variable reference doesn't match required type");
                }
            } else if (expressionType == ExpressionType.COLOR) {
                if (!(node instanceof ColorLiteral)) {
                    node.setError("Expression must have a color literal");
                }
            } else if (expressionType == ExpressionType.PIXEL) {
                if (!(node instanceof PixelLiteral)) {
                    node.setError("Expression must have a pixel literal");
                }
            } else if (expressionType == ExpressionType.SCALAR) {
                if (!(node instanceof ScalarLiteral)) {
                    node.setError("Expression must have a scalar literal");
                }
            } else if (expressionType == ExpressionType.BOOL) {
                if (!(node instanceof BoolLiteral)) {
                    node.setError("Expression must have a bool literal");
                }
            } else if (expressionType == ExpressionType.PERCENTAGE) {
                if (!(node instanceof PercentageLiteral)) {
                    node.setError("Expression must have a percentage literal");
                }
            }
        }
    }

    private void checkOperation(Operation node, ExpressionType type) {
        //check if children are also operations
        if (node.lhs instanceof Operation) {
            checkOperation((Operation) node.lhs, type);
        } else if (node.rhs instanceof Operation) {
            checkOperation((Operation) node.rhs, type);
        }

        if (node instanceof MultiplyOperation) { //check if one of the children is a scalar value
            if (node.lhs instanceof ScalarLiteral && node.rhs instanceof ScalarLiteral) {
                node.setError("Expression must have at least one scalar literal");
            }

        } else {// check if both sides are the same literal
            if (node.lhs instanceof ColorLiteral || node.lhs instanceof BoolLiteral || node.rhs instanceof ColorLiteral || node.rhs instanceof BoolLiteral) {
                node.setError("Colors and Booleans are not allowed in operations");
            }

            // fix in case either is an operation
            ExpressionType left = checkExpressionType(node.lhs);
            ExpressionType right = checkExpressionType(node.rhs);

            if (!left.equals(right)) {
                node.setError("Expressions must have the same type");
            }

        }
    }

    private void checkIfStatement(IfClause node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableReference) {
                HashMap<String, ExpressionType> map = variableTypes.get(0);
                String varName = ((VariableReference) child).name;
                ExpressionType type = map.get(varName);
                if (!type.equals(ExpressionType.BOOL)) {
                    node.setError("Statement must have boolean value");
                }
            }
        }
    }

    public ExpressionType checkExpressionType(Expression expression) {

        if (expression instanceof VariableReference) {
            if (checkVariableReference((VariableReference) expression, ExpressionType.PIXEL)) {
                return ExpressionType.PIXEL;
            } else if (checkVariableReference((VariableReference) expression, ExpressionType.COLOR)) {
                return ExpressionType.COLOR;
            } else if (checkVariableReference((VariableReference) expression, ExpressionType.SCALAR)) {
                return ExpressionType.SCALAR;
            } else if (checkVariableReference((VariableReference) expression, ExpressionType.BOOL)) {
                return ExpressionType.BOOL;
            } else if (checkVariableReference((VariableReference) expression, ExpressionType.PERCENTAGE)) {
                return ExpressionType.PERCENTAGE;
            }
        }

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

        return ExpressionType.UNDEFINED;
    }
}
