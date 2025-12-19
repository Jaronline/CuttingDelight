package dev.jaronline.cuttingdelight.processor.config;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

public class ConfigGenerator {
	private static final String PACKAGE = "dev.jaronline.cuttingdelight.generated";
	private static final String CLASS = "GeneratedConfigLoader";
	private final Filer filer;

	public ConfigGenerator(Filer filer) {
		this.filer = filer;
	}

	public void generate(TypeElement typeElement) throws ConfigGeneratorException {
		String className = typeElement.getQualifiedName().toString();

		try {
			writeGeneratedClass(className);
		} catch (IOException e) {
			throw new ConfigGeneratorException(e);
		}
	}

	private void writeGeneratedClass(String className) throws IOException {
		JavaFileObject file = filer.createSourceFile(PACKAGE + "." + CLASS);
		try (Writer out = file.openWriter()) {
			out.write("package " + PACKAGE + ";\n\n");
			out.write("import dev.jaronline.cuttingdelight.common.config.ConfigManager;\n\n");
			out.write("public final class " + CLASS + " {\n");
			out.write("\tpublic static void loadConfig() {\n");
			out.write("\t\tConfigManager.setConfig(new " + className + "());\n");
			out.write("\t}\n");
			out.write("}\n");
		}
	}
}
