package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;
    private HashMap<String, Literal> map = new HashMap<>();

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                applyVariableAssignment((VariableAssignment) child);
            }
            if (child instanceof Stylerule) {
                ApplyStylerule((Stylerule) child);
            }
        }
    }

    private void applyVariableAssignment(VariableAssignment node) {
        Expression expression = node.expression;
        node.expression = applyExpression(expression);
        map.put(node.name.name,(Literal) node.expression);

        variableValues.addFirst(map);
    }

    private void applyDeclaration(Declaration declaration) {
        declaration.expression = applyExpression(declaration.expression);
    }

    private Expression applyExpression(Expression expression) {
        if (expression instanceof Operation) {
            return applyOperation((Operation) expression);
        }
        
        if (expression instanceof VariableReference) {
            HashMap<String, Literal> map = variableValues.get(0);
            String varName = ((VariableReference) expression).name;
            return map.get(varName);
        }
        return expression;
    }


    private Literal applyOperation(Operation operation) {
        Literal left;
        Literal right;

        int leftValue;
        int rightValue;

        if (operation.lhs instanceof Operation) {
            left = applyOperation((Operation) operation.lhs);
        } else if (operation.lhs instanceof VariableReference) {
            HashMap<String, Literal> map = variableValues.get(0);
            String varName = ((VariableReference) operation.lhs).name;
            left = map.get(varName);
        } else {
            left = (Literal) operation.lhs;
        }

        if (operation.rhs instanceof Operation) {
            right = applyOperation((Operation) operation.rhs);
        } else if (operation.rhs instanceof VariableReference) {
            HashMap<String, Literal> map = variableValues.get(0);
            String varName = ((VariableReference) operation.rhs).name;
            right = map.get(varName);
        } else {
            right = (Literal) operation.rhs;
        }

        leftValue = getLiteralValue(left);
        rightValue = getLiteralValue(right);

        if (operation instanceof AddOperation) {
            return newLiteral(left, leftValue + rightValue);
        } else if (operation instanceof SubtractOperation) {
            return newLiteral(left, leftValue - rightValue);
        } else if (operation instanceof MultiplyOperation) {
            if (right instanceof ScalarLiteral) {
                return newLiteral(left, leftValue * rightValue);
            } else {
                return newLiteral(right, leftValue * rightValue);
            }
        }
        return newLiteral(left, leftValue);
    }

    private Literal newLiteral(Literal literal, int value) {
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        } else if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        } else if (literal instanceof PercentageLiteral) {
            return new PercentageLiteral(value);
        }
        return literal;
    }

    private int getLiteralValue(Literal literal) {
        if (literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value;
        } else if (literal instanceof ScalarLiteral) {
            return ((ScalarLiteral) literal).value;
        } else if (literal instanceof PercentageLiteral) {
            return ((PercentageLiteral) literal).value;
        }
        return 0;
    }

    private void ApplyStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                evalIfStatement((IfClause) child);
            }
        }
    }

    private void evalIfStatement(IfClause node){
        boolean isIfTrue = false;
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableReference) {
                HashMap<String, Literal> map = variableValues.get(0);
                String varName = ((VariableReference) child).name;
                Literal literal = map.get(varName);

                if (literal instanceof BoolLiteral) {
                    isIfTrue = ((BoolLiteral) literal).value;
                }
            }
            if (isIfTrue) {
                if (child instanceof IfClause) { //kinda recursive? no clue if this is correct
                    evalIfStatement((IfClause) child);
                } else if (child instanceof ElseClause) {
                    evalElseStatement((ElseClause) child);
                } else if (child instanceof Declaration) {
                    applyDeclaration((Declaration) child);
                }
            }
        }
    }

    private void evalElseStatement(ElseClause node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            }
        }
    }
}
