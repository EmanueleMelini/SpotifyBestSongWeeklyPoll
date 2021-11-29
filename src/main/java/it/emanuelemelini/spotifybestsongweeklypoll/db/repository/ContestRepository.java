package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Contest;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContestRepository extends CrudRepository<Contest, Long> {

	Contest getContestById(long id);

	List<Contest> getContestsByGuildAndDeletedOrderById(Guild guild, boolean deleted);

	List<Contest> getContestsByGuildAndDateAndDeleted(Guild guild, LocalDateTime date, boolean deleted);

}
