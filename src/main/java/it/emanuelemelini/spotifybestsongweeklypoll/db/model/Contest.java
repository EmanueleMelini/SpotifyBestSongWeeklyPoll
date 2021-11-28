package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contests")
public class Contest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_contest")
	private long idcontest;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	private Guild guild;

	@ManyToOne
	@JoinColumn(name = "song_id")
	private Song song;

	@Column(name = "emote")
	private String emote;

	@Column(name = "count")
	private int count;

	@Column(name = "mess_id")
	private long mess;

	@Column(name = "image")
	private String image;

	@Column(name = "url")
	private String url;

	@Column(name = "contest_date")
	private LocalDateTime date;

	@Column(name = "deleted")
	private boolean deleted;

	protected Contest() {}

	public Contest(Guild guild, Song song, String emote, int count, LocalDateTime date, String image, String url, boolean deleted) {
		this.guild = guild;
		this.song = song;
		this.emote = emote;
		this.count = count;
		this.date = date;
		this.image = image;
		this.url = url;
		this.deleted = deleted;
	}

	public long getIdcontest() {
		return idcontest;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	public String getEmote() {
		return emote;
	}

	public void setEmote(String emote) {
		this.emote = emote;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
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

	public long getMess() {
		return mess;
	}

	public void setMess(long mess) {
		this.mess = mess;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
