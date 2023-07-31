package com.electronwill.nightconfig.core.file;

import com.electronwill.nightconfig.core.utils.ConfigWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author TheElectronWill
 */
public final class AutoreloadFileConfig<C extends FileConfig> extends ConfigWrapper<C> implements FileConfig {
	private final FileWatcher watcher = FileWatcher.defaultInstance();

	public AutoreloadFileConfig(C config) {
		super(config);
		try {
			watcher.addWatch(config.getFile(), config::load);
		} catch (IOException e) {
			throw new RuntimeException("Unable to create the autoreloaded config", e);
		}
	}

	@Override
	public File getFile() {
		return config.getFile();
	}

	@Override
	public Path getNioPath() {
		return config.getNioPath();
	}

	@Override
	public void save() {
		config.save();
	}

	@Override
	public void load() {
		config.load();
	}

	@Override
	public void close() {
		watcher.removeWatch(config.getFile());
		config.close();
	}
}