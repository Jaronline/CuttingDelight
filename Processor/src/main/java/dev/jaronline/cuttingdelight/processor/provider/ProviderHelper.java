package dev.jaronline.cuttingdelight.processor.provider;

import dev.jaronline.cuttingdelight.common.provider.ProviderManager;
import dev.jaronline.cuttingdelight.core.provider.Provider;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ProviderHelper {
	private static final Logger LOGGER = Logger.getLogger(ProviderHelper.class.getName());
	private final Types types;
	private final Elements elements;

	public ProviderHelper(Types types, Elements elements) {
		this.types = types;
		this.elements = elements;
	}

	public List<TypeElement> getProviderInterfaces(TypeElement typeElement) {
		return typeElement.getInterfaces().stream()
				.filter(this::validProviderInterface)
				.map(typeMirror -> (TypeElement) types.asElement(typeMirror))
				.toList();
	}

	private boolean validProviderInterface(TypeMirror typeMirror) {
		TypeMirror providerInterface = elements.getTypeElement(Provider.class.getName()).asType();
		return types.isAssignable(types.erasure(typeMirror), types.erasure(providerInterface));
	}

	public Method findRegistrationMethod(TypeElement element) {
		return findRegistrationMethod(element, 1);
	}

	public Method findRegistrationMethod(TypeElement element, int maxDepth) {
		return findRegistrationMethod(element, 0, maxDepth);
	}

	private Method findRegistrationMethod(TypeElement element, int depth, int maxDepth) {
		Method method = getRegistrationMethod(element);
		if (method != null) {
			return method;
		}
		if (depth >= maxDepth) {
			return null;
		}
		List<TypeElement> providerInterfaces = getProviderInterfaces(element);
		if (!providerInterfaces.isEmpty()) {
			for (TypeElement providerInterface : providerInterfaces) {
				method = findRegistrationMethod(providerInterface, depth + 1, maxDepth);
				if (method != null) {
					return method;
				}
			}
		}
		return null;
	}

	public Method getRegistrationMethod(TypeElement element) {
		try {
			Class<?> providerInterface = Class.forName(element.getQualifiedName().toString());
			String className = providerInterface.getSimpleName();

			if (hasRegistrationMethod("add" + className, providerInterface)) {
				return ProviderManager.class.getMethod("add" + className, providerInterface);
			} else if (hasRegistrationMethod("set" + className, providerInterface)) {
				return ProviderManager.class.getMethod("set" + className, providerInterface);
			}
		} catch (ClassNotFoundException e) {
			LOGGER.warning("Could not load class for " + element.getQualifiedName());
		} catch (NoSuchMethodException e) {
			LOGGER.severe("No registration method found for " + element.getQualifiedName() + "." +
					" This should not be possible. Please report this issue to the maintainers.");
		}
		return null;
	}

	private boolean hasRegistrationMethod(String methodName, Class<?> providerInterface) {
		return Arrays.stream(ProviderManager.class.getMethods())
				.anyMatch(method -> method.getName().equals(methodName) && method.getParameterCount() == 1
					&& method.getParameterTypes()[0].equals(providerInterface));
	}
}
