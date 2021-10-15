package it.emanuelemelini.spotifybestsongweeklypoll.lib;

public class Artists {

	private String name;
	private External_urls external_urls;
	private String href;
	private String id;
	private String type;
	private String uri;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public External_urls getExternal_urls() {
		return external_urls;
	}

	public void setExternal_urls(External_urls external_urls) {
		this.external_urls = external_urls;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
