package dev.jaronline.cuttingdelight.processor.provider;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ProviderChecker {
	private static final Logger LOGGER = Logger.getLogger(ProviderChecker.class.getName());
	private final ProviderHelper helper;

	public ProviderChecker(ProviderHelper providerHelper) {
		this.helper = providerHelper;
	}

	public boolean check(Set<? extends Element> annotatedProviders) throws ProviderCheckException {
		if (annotatedProviders.isEmpty()) {
			return false;
		}
		for (Element element : annotatedProviders) {
			if (!check(element)) {
				return false;
			}
		}
		return true;
	}

	private boolean check(Element element) throws ProviderCheckException {
		if (!(element instanceof TypeElement typeElement && element.getKind().isClass())) {
			throw new ProviderCheckException("The @AutoProvider annotation can only be applied to classes!");
		}
		List<TypeElement> interfaces = helper.getProviderInterfaces(typeElement);
		if (interfaces.isEmpty()) {
			throw new ProviderCheckException("Class " + typeElement.getQualifiedName() + " is annotated with @AutoProvider but does not implement any Provider interfaces.");
		}
		for (TypeElement interfaceElem : interfaces) {
			if (!canRegisterProvider(interfaceElem)) {
				LOGGER.warning("Provider " + typeElement.getQualifiedName() + " cannot be registered for interface " + interfaceElem + " and will be skipped.");
			}
		}
		return true;
	}

	private boolean canRegisterProvider(TypeElement element) {
		Method registerMethod = helper.findRegistrationMethod(element);
		return registerMethod != null;
	}
}
