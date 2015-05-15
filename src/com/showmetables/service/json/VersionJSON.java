package com.showmetables.service.json;

public class VersionJSON {

	private int currentVersion;
	
	public int getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	public VersionJSON(int currentVersion) {
		this.currentVersion = currentVersion;
	}
}
