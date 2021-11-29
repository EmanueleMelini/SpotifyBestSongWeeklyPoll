package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Playlist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends CrudRepository<Playlist, Long> {

	List<Playlist> getPlaylistsByGuildAndDeleted(Guild guild, boolean deleted);

	Playlist getPlaylistByIdAndDeleted(long id, boolean deleted);

}
