package com.electronwill.nightconfig.core.utils;

/**
 * @author TheElectronWill
 */
public abstract class AbstractObserved {
	protected final Runnable callback;

	protected AbstractObserved(Runnable callback) {
		this.callback = callback;
	}
}