package it.emanuelemelini.spotifybestsongweeklypoll.db;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuildRepository extends CrudRepository<Guild, Long> {

	Guild getGuildByGuildidAndDeleted(long guildid, boolean deleted);

	List<Guild> getGuildsByDeleted(boolean deleted);

	Guild getGuildByIdguild(long idguild);

}
