package dev.jaronline.cuttingdelight.processor.provider;

import dev.jaronline.cuttingdelight.core.provider.AutoProvider;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("dev.jaronline.cuttingdelight.core.provider.AutoProvider")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ProviderAP extends AbstractProcessor {
	private ProviderChecker checker;
	private ProviderGenerator generator;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		ProviderHelper helper = new ProviderHelper(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
		this.checker = new ProviderChecker(helper);
		this.generator = new ProviderGenerator(helper, processingEnv.getFiler());
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(AutoProvider.class);

		if (!checker.check(annotated)) {
			return false;
		}

		generator.generate(annotated);

		return true;
	}
}
