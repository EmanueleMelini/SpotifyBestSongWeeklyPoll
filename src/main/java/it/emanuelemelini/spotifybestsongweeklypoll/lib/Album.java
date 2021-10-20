package it.emanuelemelini.spotifybestsongweeklypoll.lib;

import java.util.List;

public class Album {

	private List<Artists> artists;

	public List<Artists> getArtists() {
		return artists;
	}

	public void setArtists(List<Artists> artists) {
		this.artists = artists;
	}

	public String getAllArtists(){
		StringBuilder s = new StringBuilder();
		s.append(this.artists.get(0).getName());

		for(int i = 0; i < this.artists.size() && this.artists.size() > 1; i++) {
			String artist = this.artists.get(i).getName();
			if(!s.toString().contains(artist))
				s.append(", ")
					.append(artist);
		}

		return s.toString();
	}

}
