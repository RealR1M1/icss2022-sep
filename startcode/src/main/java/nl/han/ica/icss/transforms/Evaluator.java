package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;

//TODO: SCOPE IMPLEMENTATION, IF-CLAUSE EVALUATION
public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet node) {
        variableValues.addFirst(new HashMap<>());
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                applyVariableAssignment((VariableAssignment) child, variableValues.getFirst());
            }
            if (child instanceof Stylerule) {
                ApplyStylerule((Stylerule) child);
            }
        }
        variableValues.removeFirst();
    }

    private void applyVariableAssignment(VariableAssignment node, HashMap<String, Literal> scope) {
        Expression expression = node.expression;
        node.expression = applyExpression(expression);
        scope.put(node.name.name,(Literal) node.expression);
    }

    private Literal applyVariableReference(VariableReference node) {
        if (variableValues.getSize() > 0 && variableValues.getFirst().containsKey(node.name)) {
            System.out.println("found in current scope");
            return variableValues.getFirst().get(node.name);
        } else if (variableValues.getSize() >= 1 || variableValues.get(variableValues.getSize() - 1).get(node.name) != null) {
            System.out.println("found in parent scope");
            return variableValues.get(variableValues.getSize() - 1).get(node.name);
        } else {
            return null;
        }
    }

    private void ApplyStylerule(Stylerule node) {
        // Add new scope
        variableValues.addFirst(new HashMap<>());
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                evalIfStatement((IfClause) child);
            } else if (child instanceof VariableAssignment) {
                applyVariableAssignment((VariableAssignment) child, variableValues.getFirst());
            }
        }
        variableValues.removeFirst();
    }

    private void evalIfStatement(IfClause node){
        boolean isIfTrue = false;
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableReference) {
                Literal literal = applyVariableReference((VariableReference) child);

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
                System.out.println(child);
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

    private void applyDeclaration(Declaration declaration) {
        declaration.expression = applyExpression(declaration.expression);
    }

    private Expression applyExpression(Expression expression) {
        if (expression instanceof Operation) {
            return applyOperation((Operation) expression);
        }
        
        if (expression instanceof VariableReference) {
            return applyVariableReference((VariableReference) expression);
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
            left = applyVariableReference((VariableReference) operation.lhs);
        } else {
            left = (Literal) operation.lhs;
        }

        if (operation.rhs instanceof Operation) {
            right = applyOperation((Operation) operation.rhs);
        } else if (operation.rhs instanceof VariableReference) {
            right = applyVariableReference((VariableReference) operation.rhs);
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
}
