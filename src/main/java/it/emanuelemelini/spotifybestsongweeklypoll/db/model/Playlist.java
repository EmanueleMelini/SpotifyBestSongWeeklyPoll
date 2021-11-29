package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "playlists")
public class Playlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_playlist")
	private long id;

	@ManyToOne
	@JoinColumn(name = "id_guild")
	private Guild guild;

	@Column(name = "spotify_id")
	private String spotifyId;

	@Column(name = "image")
	private String image;

	@Column(name = "url")
	private String url;

	@Column(name = "deleted")
	private boolean deleted;

	@OneToMany(mappedBy = "playlist")
	private Set<Contest> contests;

	protected Playlist() {

	}

	public Playlist(Guild guild, String spotifyId, String image, String url, boolean deleted) {
		this.guild = guild;
		this.spotifyId = spotifyId;
		this.image = image;
		this.url = url;
		this.deleted = deleted;
	}

	public long getId() {
		return id;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Set<Contest> getContests() {
		return contests;
	}

}
