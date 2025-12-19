package dev.jaronline.cuttingdelight.processor.config;

import dev.jaronline.cuttingdelight.core.config.Config;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("dev.jaronline.cuttingdelight.core.config.Config")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ConfigAP extends AbstractProcessor {
	private ConfigChecker checker;
	private ConfigGenerator generator;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.checker = new ConfigChecker(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
		this.generator = new ConfigGenerator(processingEnv.getFiler());
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(Config.class);

		if (!checker.check(annotated)) {
			return false;
		}

		TypeElement type = (TypeElement) annotated.iterator().next();
		generator.generate(type);

		return true;
	}
}
