package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;

public class Generator {

	public String generate(AST ast) {

		return generateStylesheet((Stylesheet) ast.root);



	}

	private String generateStylesheet(Stylesheet node) {
		for (ASTNode child : node.getChildren()) {
			if (child instanceof Stylerule) {
				return generateStylerule((Stylerule) child);
			}
		}
        return "";
    }

	private String generateStylerule(Stylerule stylerule) {
		String result = stylerule.selectors.get(0).toString() + " {\n";
		result += "\t" + generateDeclaration(stylerule.body.get(0)); //TODO: fix
		result += "\n}";

		return result;
	}

	private String generateDeclaration(ASTNode astNode) {
		return "Declaration"; //TODO: implement
	}


}
