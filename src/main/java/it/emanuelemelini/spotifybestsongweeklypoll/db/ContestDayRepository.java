package it.emanuelemelini.spotifybestsongweeklypoll.db;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.ContestDay;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestDayRepository extends CrudRepository<ContestDay, Long> {

	List<ContestDay> getAllContestDayByGuildid(String guildid);

	List<ContestDay> getAllContestDayByDay(ContestDay.Day day);

	ContestDay getContestDayByIdcontestday(long IDcontestday);
}
