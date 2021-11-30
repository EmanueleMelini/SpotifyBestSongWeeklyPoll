package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Contest;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.ContestTrack;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContestTrackRepository extends CrudRepository<ContestTrack, Long> {

	List<ContestTrack> getContestTracksByContestAndGuildAndDeleted(Contest contest, Guild guild, boolean deleted);

	List<ContestTrack> getContestTracksByGuildAndDeleted(Guild guild, boolean deleted);

	List<ContestTrack> getContestTrackByContestAndGuildAndEmoteAndDeleted(Contest contest, Guild guild, String emote, boolean deleted);

	ContestTrack getContestTrackByIdAndDeleted(long id, boolean deleted);

}
