package com.youthful.game.rogueliketest.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.youthful.game.rogueliketest.RoguelikeTest;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {
		//Temporary
		if (StartOnFirstThreadHelper.startNewJvmIfRequired())
			return null;
		
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		
		config.setWindowIcon("icon.png");
		
		config.setAutoIconify(true);
		
		//config.setWindowedMode(640, 360);

		config.useOpenGL3(true, 3, 2);
		
		config.useVsync(true);
		
		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		
		return new Lwjgl3Application(new RoguelikeTest(), config);
	}
}