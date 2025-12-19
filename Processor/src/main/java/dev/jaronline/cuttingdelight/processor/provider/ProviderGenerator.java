package dev.jaronline.cuttingdelight.processor.provider;

import dev.jaronline.cuttingdelight.common.provider.ProviderManager;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class ProviderGenerator {
	private static final String PACKAGE = "dev.jaronline.cuttingdelight.generated";
	private static final String CLASS = "GeneratedProviders";
	private final ProviderHelper helper;
	private final Filer filer;

	public ProviderGenerator(ProviderHelper helper, Filer filer) {
		this.helper = helper;
		this.filer = filer;
	}

	public void generate(Set<? extends Element> elements) throws ProviderGeneratorException {
		try (Writer out = filer.createSourceFile(PACKAGE + "." + CLASS).openWriter()) {
			out.write("package " + PACKAGE + ";\n\n");
			out.write("import " + ProviderManager.class.getName() + ";\n\n");
			out.write("public final class " + CLASS + " {\n");
			out.write("\tpublic static void loadProviders() {\n");

			for (Element element : elements) {
				generateProviderRegistration(out, element);
			}

			out.write("\t}\n");
			out.write("}\n");
		} catch (IOException e) {
			throw new ProviderGeneratorException(e);
		}
	}

	private void generateProviderRegistration(Writer out, Element element) throws IOException {
		TypeElement typeElement = (TypeElement) element;
		List<TypeElement> interfaces = helper.getProviderInterfaces(typeElement);

		for (TypeElement interfaceElement : interfaces) {
			generateProviderRegistration(out, typeElement, interfaceElement);
		}
	}

	private void generateProviderRegistration(Writer out, TypeElement provider, TypeElement providerInterface) throws IOException {
		Method registerMethod = helper.findRegistrationMethod(providerInterface);
		if (registerMethod != null) {
			out.write("\t\t" + ProviderManager.class.getSimpleName() + "." + registerMethod.getName() + "(new " + provider.getQualifiedName() + "());\n");
		}
	}
}
