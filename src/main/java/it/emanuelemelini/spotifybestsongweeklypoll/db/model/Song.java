package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;

@Entity
@Table(name = "songs")
public class Song {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_song")
	private long id;

	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;

	@ManyToOne
	@JoinColumn(name = "id_guild")
	private Guild guild;

	@Column(name = "name_song")
	private String name;

	@Column(name = "spotify_id")
	private String spotifyId;

	@Column(name = "authors")
	private String authors;

	@Column(name = "deleted")
	private boolean deleted;

	protected Song() {

	}

	public Song(Guild guild, String name, String spotifyId, String authors, User user, boolean deleted) {
		this.guild = guild;
		this.name = name;
		this.spotifyId = spotifyId;
		this.authors = authors;
		this.user = user;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
