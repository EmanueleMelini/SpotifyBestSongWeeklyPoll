package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Contest;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface ContestRepository extends CrudRepository<Contest, Long> {

	Contest getContestByIdcontest(long idcontest);

	Contest getContestByGuildAndDeleted(Guild guild, boolean deleted);

	Contest getContestByGuildAndDateAndDeleted(Guild guild, LocalDateTime date, boolean deleted);

}
