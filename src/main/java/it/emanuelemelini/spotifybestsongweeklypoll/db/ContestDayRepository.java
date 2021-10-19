package it.emanuelemelini.spotifybestsongweeklypoll.db;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.ContestDay;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestDayRepository extends CrudRepository<ContestDay, Long> {

	ContestDay getContestDayByGuildAndDeleted(Guild guild, boolean deleted);

	List<ContestDay> getContestDaysByDayAndDeleted(ContestDay.Day day, boolean deleted);

	ContestDay getContestDayByIdcontestday(long IDcontestday);
}
