package dev.jaronline.cuttingdelight.core.provider;

public interface SingletonProvider extends SimpleProvider {
	@Override
	default Class<?> getType() {
		return Object.class;
	}
}
