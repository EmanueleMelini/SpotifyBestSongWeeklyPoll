package it.emanuelemelini.spotifybestsongweeklypoll.db;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.ContestDay;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContestDayRepository extends CrudRepository<ContestDay, Long> {

	List<ContestDay> getAllContestDay();

	List<ContestDay> getAllContestDayByGuildid(String guildid);

	List<ContestDay> getAllContestDayByDay(ContestDay.Day day);

	ContestDay getContestDayByIdcontestday(long IDcontestday);
}
