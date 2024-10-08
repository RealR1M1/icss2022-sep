package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<ASTNode>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        ast.root = (Stylesheet) currentContainer.pop();
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		if (ctx.LOWER_IDENT() != null){
			TagSelector tagSelector = new TagSelector(ctx.getText());
			currentContainer.push(tagSelector);
		} else if (ctx.CLASS_IDENT() != null){
			ClassSelector classSelector = new ClassSelector(ctx.getText());
			currentContainer.push(classSelector);
		} else if (ctx.ID_IDENT() != null) {
			IdSelector idSelector = new IdSelector(ctx.getText());
			currentContainer.push(idSelector);
		}
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		if (ctx.LOWER_IDENT() != null){
			TagSelector tagSelector = (TagSelector) currentContainer.pop();
			currentContainer.peek().addChild(tagSelector);
		} else if (ctx.CLASS_IDENT() != null){
			ClassSelector classSelector = (ClassSelector) currentContainer.pop();
			currentContainer.peek().addChild(classSelector);
		} else if (ctx.ID_IDENT() != null) {
			IdSelector idSelector = (IdSelector) currentContainer.pop();
			currentContainer.peek().addChild(idSelector);
		}
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterProperty(ICSSParser.PropertyContext ctx) {
		PropertyName property = new PropertyName(ctx.getText());
		currentContainer.push(property);
	}

	@Override
	public void exitProperty(ICSSParser.PropertyContext ctx) {
		PropertyName property = (PropertyName) currentContainer.pop();
		currentContainer.peek().addChild(property);
	}

	@Override
	public void enterExpression(ICSSParser.ExpressionContext ctx) {
		if (ctx.COLOR() != null) {
			Expression expression = new ColorLiteral(ctx.getText());
			currentContainer.push(expression);
		} else if (ctx.PIXELSIZE() != null) {
			Expression expression = new PixelLiteral(ctx.getText());
			currentContainer.push(expression);
		} else throw new IllegalStateException("Expression cannot be empty");
	}

	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		Expression expression = (Expression) currentContainer.pop();
		currentContainer.peek().addChild(expression);
	}
}