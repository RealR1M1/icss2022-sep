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
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Literal){
                map.put(node.name.name,(Literal) child);
            }
        }
        variableValues.addFirst(map);
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

    private void applyDeclaration(Declaration node) {
        System.out.println("Node: " + node);
        for (ASTNode child : node.getChildren()) {
            System.out.println(child);
            if (child instanceof VariableReference) {
                HashMap<String, Literal> map = variableValues.get(0);
                String varName = ((VariableReference) child).name;
                Literal literal = map.get(varName);

                if (literal instanceof PixelLiteral) {

                    //fetch pixel value
                } else if (literal instanceof ScalarLiteral) {
                    //fetch scalar value
                } else if (literal instanceof ColorLiteral) {
                    // fetch color value
                }
            }
            if (child instanceof Operation) {


            }
            if (child instanceof Expression) {
                evalExpression((Expression) child);
            }
        }
        //node.expression = evalExpression(node.expression);
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

    private Expression evalExpression(Expression expression) {
        //TODO: Overwrite previous value
        return expression;
    }


}
