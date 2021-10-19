package it.emanuelemelini.spotifybestsongweeklypoll;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTrack {

	private String spotifyid;

	private String spotifyname;

	private String trackname;

}
