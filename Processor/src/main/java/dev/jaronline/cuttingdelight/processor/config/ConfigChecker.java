package dev.jaronline.cuttingdelight.processor.config;

import dev.jaronline.cuttingdelight.common.config.IConfig;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

public class ConfigChecker {
	private final Types types;
	private final Elements elements;

	public ConfigChecker(Types types, Elements elements) {
		this.types = types;
		this.elements = elements;
	}

	public boolean check(Set<? extends Element> annotatedConfigs) throws ConfigCheckException {
		if (annotatedConfigs.isEmpty()) {
			return false;
		}
		if (annotatedConfigs.size() > 1) {
			throw new ConfigCheckException("Only one @Config annotation is allowed per loader module!");
		}
		for (Element element : annotatedConfigs) {
			if (!check(element)) {
				return false;
			}
		}
		return true;
	}

	private boolean check(Element element) throws ConfigCheckException {
		if (!(element instanceof TypeElement typeElement && element.getKind().isClass())) {
			throw new ConfigCheckException("The @Config annotation can only be applied to classes!");
		}
		if (typeElement.getInterfaces().stream().noneMatch(this::validConfigInterface)) {
			throw new ConfigCheckException("The class annotated with @Config must implement a valid config interface!");
		}
		return true;
	}

	private boolean validConfigInterface(TypeMirror typeMirror) {
		TypeMirror configInterface = elements.getTypeElement(IConfig.class.getName()).asType();
		return types.isSameType(types.erasure(typeMirror), types.erasure(configInterface));
	}
}
