package it.emanuelemelini.spotifybestsongweeklypoll;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTrack {

	private String spotifyUserID;

	private String spotifyUserName;

	private String spotifySongID;

	private String trackName;

	private String trackAuthors;

}
