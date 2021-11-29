package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;

@Entity
@Table(name= "contest_tracks")
public class ContestTrack {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_contest_track")
	private long id;

	@ManyToOne
	@JoinColumn(name = "id_contest")
	private Contest contest;

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

	@Column(name = "deleted")
	private boolean deleted;

	protected ContestTrack() {

	}

	public ContestTrack(Contest contest, Guild guild, Song song, String emote, int count, boolean deleted) {
		this.contest = contest;
		this.guild = guild;
		this.song = song;
		this.emote = emote;
		this.count = count;
		this.deleted = deleted;
	}

	public long getId() {
		return id;
	}

	public Contest getContest() {
		return contest;
	}

	public void setContest(Contest contest) {
		this.contest = contest;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
