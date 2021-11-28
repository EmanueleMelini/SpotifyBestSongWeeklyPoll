package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Contest;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ContestRepository extends CrudRepository<Contest, Long> {

	Contest getContestByIdcontest(long idcontest);

	List<Contest> getContestsByGuildAndDeletedOrderByIdcontest(Guild guild, boolean deleted);

	List<Contest> getContestsByGuildAndDateAndDeleted(Guild guild, LocalDateTime date, boolean deleted);

	List<Contest> getContestsByGuildAndDateAndEmoteAndDeleted(Guild guild, LocalDateTime date, String emote, boolean deleted);

	List<Contest> getContestsByGuildAndDateAndMessAndDeleted(Guild guild, LocalDateTime date, long mess, boolean deleted);

}
