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

	@Column(name = "song_id")
	private String songid;

	@Column(name = "count")
	private int count;

	@Column(name = "date")
	private LocalDateTime date;

	@Column(name = "deleted")
	private boolean deleted;

	protected Contest() {}

	public Contest(Guild guild, String songid, int count, LocalDateTime date, boolean deleted) {
		this.guild = guild;
		this.songid = songid;
		this.count = count;
		this.date = date;
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

	public String getSongid() {
		return songid;
	}

	public void setSongid(String songid) {
		this.songid = songid;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
