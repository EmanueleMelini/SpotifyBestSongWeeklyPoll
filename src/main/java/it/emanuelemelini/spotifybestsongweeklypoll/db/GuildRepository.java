package it.emanuelemelini.spotifybestsongweeklypoll.db;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuildRepository extends CrudRepository<Guild, Long> {

	List<Guild> getGuildsByGuildid(long guildid);

	Guild getGuildByIdguild(long idguild);

}