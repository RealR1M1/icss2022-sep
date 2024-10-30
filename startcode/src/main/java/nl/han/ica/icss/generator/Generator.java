package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {

	public String generate(AST ast) {
		return generateStylesheet(ast.root);

	}

	private String generateStylesheet(Stylesheet node) {
		StringBuilder result = new StringBuilder();

		for (ASTNode child : node.getChildren()) {
			if (child instanceof Stylerule) {
				result.append(generateStylerule((Stylerule) child)).append(System.lineSeparator()).append(System.lineSeparator());
			}
		}
        return result.toString();
    }

	private String generateStylerule(Stylerule stylerule) {
		System.out.println(stylerule);
		for (ASTNode child : stylerule.getChildren()) {

			Selector selector = null;
			if (child instanceof Selector) {
				selector = (Selector) child;
			}

            assert selector != null;
            StringBuilder result = new StringBuilder(selector + " {\n");

			for (int i = 0; i < stylerule.body.size(); i++) {

				//Only append declarations into stringbuilder
				if (stylerule.body.get(i) instanceof Declaration) {
					result.append("  ").append(generateDeclaration((Declaration) stylerule.body.get(i)));
				}
			}
			result.append("}");

			return result.toString();
		}
		return "";
	}

	private String generateDeclaration(Declaration declaration) {
		String result = declaration.property.name;
		result += ": " + generateExpression(declaration.expression);
		result += ";\n";

		return result;
	}

	private String generateExpression(Expression expression) {
		if (expression instanceof PixelLiteral) {
			return ((PixelLiteral) expression).value + "px";
		} else if (expression instanceof PercentageLiteral) {
			return ((PercentageLiteral) expression).value + "%";
		} else if (expression instanceof ColorLiteral) {
			return ((ColorLiteral) expression).value;
		}
		return "";
	}
}
