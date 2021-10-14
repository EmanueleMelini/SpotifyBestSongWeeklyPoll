package it.emanuelemelini.spotifybestsongweeklypoll.model;

import java.util.List;

public class PlaylistSpec {

	private External_urls external_urls;
	private List<Images> images;

	public External_urls getExternal_urls() {
		return external_urls;
	}

	public void setExternal_urls(External_urls external_urls) {
		this.external_urls = external_urls;
	}

	public List<Images> getImages() {
		return images;
	}

	public void setImages(List<Images> images) {
		this.images = images;
	}

}
