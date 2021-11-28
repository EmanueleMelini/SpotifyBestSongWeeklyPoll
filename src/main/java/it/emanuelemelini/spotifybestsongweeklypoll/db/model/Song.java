package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;

@Entity
@Table(name = "songs")
public class Song {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_song")
	private long idsong;

	@Column(name = "name_song")
	private String name;

	@Column(name = "spotify_id")
	private String spotifyID;

	@Column(name = "authors")
	private String authors;

	@Column(name = "deleted")
	private boolean deleted;

	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;

	@ManyToOne
	@JoinColumn(name = "id_guild")
	private Guild guild;

	public Song(String name, String spotifyID, String authors, User user, Guild guild, boolean deleted) {
		this.name = name;
		this.spotifyID = spotifyID;
		this.authors = authors;
		this.user = user;
		this.guild = guild;
		this.deleted = deleted;
	}

	protected Song() {}

	public long getIdsong() {
		return idsong;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpotifyID() {
		return spotifyID;
	}

	public void setSpotifyID(String spotifyID) {
		this.spotifyID = spotifyID;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
