package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "contests")
public class Contest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_contest")
	private long id;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	private Guild guild;

	@ManyToOne
	@JoinColumn(name = "id_playlist")
	private Playlist playlist;

	@Column(name = "mess_id")
	private long mess;

	@Column(name = "contest_date")
	private LocalDateTime date;

	@Column(name = "deleted")
	private boolean deleted;

	@OneToMany(mappedBy = "contest")
	private Set<ContestTrack> contestTracks;

	protected Contest() {}

	public Contest(Guild guild, LocalDateTime date, boolean deleted) {
		this.guild = guild;
		this.date = date;
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

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	public long getMess() {
		return mess;
	}

	public void setMess(long mess) {
		this.mess = mess;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Set<ContestTrack> getContesttracks() {
		return contestTracks;
	}

}
