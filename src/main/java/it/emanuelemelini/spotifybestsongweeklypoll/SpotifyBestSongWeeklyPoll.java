package it.emanuelemelini.spotifybestsongweeklypoll;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.Reaction;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.*;
import discord4j.rest.util.AllowedMentions;
import discord4j.rest.util.Color;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.*;
import it.emanuelemelini.spotifybestsongweeklypoll.db.repository.*;
import it.emanuelemelini.spotifybestsongweeklypoll.lib.Items;
import it.emanuelemelini.spotifybestsongweeklypoll.lib.Login;
import it.emanuelemelini.spotifybestsongweeklypoll.lib.Playlist;
import it.emanuelemelini.spotifybestsongweeklypoll.lib.PlaylistSpec;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.*;

/**
 * A Discord bot that create a contest for voting the best song on the given playlist ID
 */
@SpringBootApplication
public class SpotifyBestSongWeeklyPoll implements CommandLineRunner {

	/**
	 * The {@link Winner} Table Query Repository
	 */
	@Autowired
	private WinnerRepository winnerRepository;

	/**
	 * The {@link Guild} Table Query Repository
	 */
	@Autowired
	private GuildRepository guildRepository;

	/**
	 * The {@link ContestDay} Table Query Repository
	 */
	@Autowired
	private ContestDayRepository contestDayRepository;

	/**
	 * The {@link User} Table Query Repository
	 */
	@Autowired
	private UserRepository userRepository;

	/**
	 * The {@link Contest} Table Query Repository
	 */
	@Autowired
	private ContestRepository contestRepository;

	/**
	 * The {@link Song} Table Query Repository
	 */
	@Autowired
	private SongRepository songRepository;

	/**
	 * The {@link it.emanuelemelini.spotifybestsongweeklypoll.db.model.Playlist\} Table Query Repository
	 */
	@Autowired
	private PlaylistRepository playlistRepository;

	/**
	 * The {@link ContestTrack} Table Query Repository
	 */
	@Autowired
	private ContestTrackRepository contestTrackRepository;

	/**
	 * The SpotifyDeveloper Application clientID
	 */
	private final String clientID;

	/**
	 * THe SpotifyDeveloper Application secret clientID
	 */
	private final String cliendIDsecret;

	/**
	 * The DiscordDeveloper Application Bot token
	 */
	private final String token;

	/**
	 * A Map that links a Discord's reaction to a song
	 */
	private Map<String, UserTrack> songReaction = new HashMap<>();

	/**
	 * The Discord message contest Snowflake
	 */
	private Snowflake messID;

	/**
	 * The Discord message contest tie Snowflake
	 */
	private Snowflake tieMessID;

	/**
	 * A JSON Object that contains the Spotify Playlist specification
	 */
	private PlaylistSpec playlistSpecThis;

	private String playlistThis;

	/**
	 * An Array that contains regional indicator letters emoji's unicode in UTF-16
	 */
	public final String[] emojisss = {
			"\uD83C\uDDE6", //A
			"\uD83C\uDDE7", //B
			"\uD83C\uDDE8", //C
			"\uD83C\uDDE9", //D
			"\uD83C\uDDEA", //E
			"\uD83C\uDDEB", //F
			"\uD83C\uDDEC", //G
			"\uD83C\uDDED", //H
			"\uD83C\uDDEE", //I
			"\uD83C\uDDEF", //J
			"\uD83C\uDDF0", //K
			"\uD83C\uDDF1", //L
			"\uD83C\uDDF2", //M
			"\uD83C\uDDF3", //N
			"\uD83C\uDDF4", //O
			"\uD83C\uDDF5", //P
			"\uD83C\uDDF6", //Q
			"\uD83C\uDDF7", //R
			"\uD83C\uDDF8", //S
	};

	public SpotifyBestSongWeeklyPoll(@Value("${CLIENT_ID}") String clientID,
			@Value("${CLIENT_ID_SECRET}") String clientIDsecret, @Value("${DISCORD_TOKEN}") String token) {
		this.clientID = clientID;
		this.cliendIDsecret = clientIDsecret;
		this.token = token;
	}

	/**
	 * The secret clientID's getter
	 *
	 * @return The secret clientID
	 */
	public String getCliendIDsecret() {
		return cliendIDsecret;
	}

	/**
	 * The clientID's getter
	 *
	 * @return The clientID
	 */
	public String getClientID() {
		return clientID;
	}

	/**
	 * The main function of the Bot
	 * TODO: set project better
	 */
	public void mainSpotify() {

		Mono<Void> client = DiscordClient.create(token)
				.withGateway((GatewayDiscordClient gatewayDiscordClient) -> {

					Mono<Void> testBot = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(event.getMessage()
										.getContent()
										.equalsIgnoreCase("!pipo"))
									return event.getMessage()
											.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("palle!"));

								return Mono.empty();
							})
							.then();

					Mono<Void> print = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								String[] command = event.getMessage()
										.getContent()
										.split(" ");

								if(command[0].equalsIgnoreCase("!print")) {
									if(command.length < 2)
										return event.getMessage()
												.getChannel()
												.flatMap(channel -> channel.createMessage("Insert playlist"));

									printAllPlaylist(command[1]);
								}
								return Mono.empty();
							})
							.then();

					Mono<Void> disconnectLennosc = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(event.getMessage()
										.getContent()
										.contains("osudisc")) {
									return event.getMessage()
											.getGuild()
											.flatMap(guild -> guild.getMemberById(Snowflake.of(324603556277780490L))
													.flatMap(member -> member.edit(GuildMemberEditSpec.builder()
															.newVoiceChannelOrNull(null)
															.build())))
											.then(event.getMessage()
													.getChannel()
													.flatMap(messageChannel -> messageChannel.createMessage("Zitto OSU Lennosc!")));
								} else
									return Mono.empty();
							})
							.then();

					Mono<Void> kickLennosc = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(event.getMessage()
										.getContent()
										.contains("osukick")) {
									return event.getMessage()
											.getGuild()
											.flatMap(guild -> guild.kick(Snowflake.of(324603556277780490L), "OSU"))
											.then(event.getMessage()
													.getChannel()
													.flatMap(channel -> channel.createMessage("Addio OSU Lennosc!")));
								} else
									return Mono.empty();

							})
							.then();

					Mono<Void> playlist_id = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!playlist"))
									return Mono.empty();

								Message message = event.getMessage();
								String[] content = message.getContent()
										.split(" ");
								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								if(content.length < 2)
									return channel.createMessage("Insert playlist ID");

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(discord_guild.getId()
										.asLong(), false);

								if(guild == null) {
									guild = new Guild(discord_guild.getId()
											.asLong(), false);
									guildRepository.save(guild);
								}

								List<it.emanuelemelini.spotifybestsongweeklypoll.db.model.Playlist> playlists = playlistRepository.getPlaylistsByGuildAndDeleted(guild, false);

								if(playlists.size() > 1)
									return channel.createMessage("More than 1 active playlist found, delete the inactive ones!");

								playlists.get(0).setSpotifyId(content[1]);

								playlistRepository.save(playlists.get(0));

								return channel.createMessage("Playlist inserted correctly");

							})
							.then();

					Mono<Void> reactionAddEvent = gatewayDiscordClient.on(ReactionAddEvent.class, event -> {

								Message message = event.getMessage()
										.block();

								if(message == null)
									return Mono.empty();

								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								if(messID != null)
									return Mono.empty();

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(discord_guild.getId()
										.asLong(), false);

								if(guild == null) {
									guild = new Guild(discord_guild.getId()
											.asLong(), false);
									guildRepository.save(guild);
								}

								List<Contest> check = contestRepository.getContestsByGuildAndDateAndDeleted(guild,
										LocalDateTime.now()
												.withHour(1)
												.withMinute(0)
												.withSecond(0),
										//id_guild.asLong(),
										false);

								if(check.size() == 0)
									//return Mono.empty();
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("5"));

								long mess_ = check.get(0)
										.getMess();

								Message contest_mess = channel.getMessageById(Snowflake.of(mess_))
										.block();

								if(contest_mess == null)
									return channel.createMessage("Internal error!");

								if(event.getUser()
										.block()
										.isBot())
									return Mono.empty();

								discord4j.core.object.entity.User user = event.getUser()
										.block();

								if(user == null)
									return Mono.empty();

								String name_ = event.getMember()
										.get()
										.getDisplayName();

								Multimap<ReactionEmoji, Snowflake> map_ = LinkedHashMultimap.create();

								message.getReactions()
										.forEach(reaction_ -> {
											//System.out.println("e: " + reaction_.getEmoji());
											Flux<discord4j.core.object.entity.User> users_ = message.getReactors(reaction_.getEmoji());
											users_.toStream()
													.forEach(user1 -> {
														map_.put(reaction_.getEmoji(), user1.getId());
														//System.out.println("u: " + user1.toString());
													});
										});


								/*
								System.out.println("m: " + map_);
								message.getReactions()
										.stream()
										.filter(reaction -> {
											if(reaction.getEmoji() != event.getEmoji())
												return message.getReactors(reaction.getEmoji())
														.toStream()
														.filter(user1 -> user1.getId()
																	.asLong() == user.getId()
																	.asLong()).count() > 1;
											else
												return false;
										})
										.count() > 0

								System.out.println("c: " + count_);
								 */

								AtomicInteger count_ = new AtomicInteger(0);

								map_.forEach((reaction_, id_) -> {
									System.out.println("1: " + id_.asLong() + " 2: " + user.getId()
											.asLong());
									if(id_.asLong() == user.getId()
											.asLong()) {
										count_.getAndIncrement();
									}
								});

								if(count_.get() > 1) {
									return message.removeReaction(event.getEmoji(), event.getUserId())
											.then(message.getChannel()
													.flatMap(messageChannel -> messageChannel.createMessage(name_ +
															" - You already voted! Remove your vote before voting another song!")));
								} else {

									List<Contest> contests = contestRepository.getContestsByGuildAndDateAndDeleted(guild,
											LocalDateTime.now()
													.withHour(1)
													.withMinute(0)
													.withSecond(0),
											false);

									if(contests.isEmpty())
										return channel.createMessage("No contest started!");

									if(contests.size() > 1)
										return channel.createMessage("More than one contest active, internal error!");

									Contest contest_ = contests.get(0);

									List<ContestTrack> contestTracks = contestTrackRepository.getContestTracksByContestAndGuildAndDeleted(contest_, guild, false);

									contestTracks = contestTracks.stream()
											.filter(contestTrack -> contestTrack.getEmote()
													.equalsIgnoreCase(event.getEmoji()
															.asUnicodeEmoji()
															.get()
															.getRaw()))
											.collect(Collectors.toList());

									if(contestTracks.size() > 1)
										return channel.createMessage("Internal error, duplicated Reaction");

									ContestTrack contestTrack_ = contestTracks.get(0);

									contestTrack_
											.setCount(contestTrack_
													.getCount() + 1);

									contestTrackRepository.save(contestTrack_);

									contestTracks = contestTrackRepository.getContestTracksByContestAndGuildAndDeleted(contest_, guild, false);

									List<EmbedCreateFields.Field> fields = createUpdatedEmbed(contestTracks);

									boolean role_ = guild.getRoleId() != null && guild.getRoleId() != 0;

									Role role = role_ ? discord_guild.getRoleById(Snowflake.of(guild.getRoleId()))
											.block() : null;

									if(role == null && role_)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"Role not found with saved ID"));

									List<it.emanuelemelini.spotifybestsongweeklypoll.db.model.Playlist> playlists = playlistRepository.getPlaylistsByGuildAndDeleted(guild, false);

									if(playlists.isEmpty())
										return channel.createMessage("Insert playlist!");

									if(playlists.size() > 1)
										return channel.createMessage("More than one playlist active.");

									it.emanuelemelini.spotifybestsongweeklypoll.db.model.Playlist playlist_ = playlists.get(0);

									String thumbnail = playlist_
											.getImage();

									String href = playlist_
											.getUrl();

									EmbedCreateSpec embed = EmbedCreateSpec.builder()
											.title("Weekly poll")
											.author(EmbedCreateFields.Author.of("Emanuele Melini",
													"https://github.com/EmanueleMelini",
													"https://avatars.githubusercontent.com/u/73402425?v=4"))
											.color(Color.GREEN)
											.description((role_ ? role.getMention() + "\n" : "") +
													"Votazione settimanale della miglior canzone nella playlist di EXP!\nRegole:\n - Si vota ogni mercoledì la canzone preferita più \"originale\"\n - Chi vince una votazione ha la possibilità di inserire una canzone aggiuntiva al prossimo inserimento\n - Le canzoni inserite dal vincitore della settimana precedente non possono essere votate")
											.addAllFields(fields)
											.thumbnail(thumbnail)
											.url(href)
											.timestamp(Instant.now())
											.build();

									return Mono.from(contest_mess.edit(MessageEditSpec.builder()
													.addEmbed(embed)
													.build()))
											.then(channel.createMessage(name_ + " - Vote added!"));

								}

							})
							.then();

					Mono<Void> closedb = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								//TODO: End contest db

								return Mono.empty();
							})
							.then();

					Mono<Void> reactionRemoveEvent = gatewayDiscordClient.on(ReactionRemoveEvent.class, event -> {

								Message message = event.getMessage()
										.block();

								if(message == null)
									//return Mono.empty();
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("2"));

								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									//return Mono.empty();
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("4"));

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									//return Mono.empty();
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("5"));

								if(messID != null)									return Mono.empty();

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(discord_guild.getId()
										.asLong(), false);

								if(guild == null) {
									guild = new Guild(discord_guild.getId()
											.asLong(), false);
									guildRepository.save(guild);
								}

								System.out.println("1: " + event.getEmoji());
								System.out.println("2: " + event.getEmoji()
										.asUnicodeEmoji()
										.get()
										.getRaw());
								System.out.println("3: " + event.getEmoji()
										.asUnicodeEmoji()
										.get()
										.toString());

								List<Contest> contests = contestRepository.getContestsByGuildAndDateAndDeleted(guild,
										LocalDateTime.now()
												.withHour(1)
												.withHour(0)
												.withSecond(0),
										false);

								/*
								event.getEmoji()
									.asUnicodeEmoji()
									.get()
									.getRaw(),
								 */

								if(contests.isEmpty())
									return channel.createMessage("No contest started!");

								if(contests.size() > 1)
									return channel.createMessage("More than one contest started!");

								Contest contest_ = contests.get(0);

								List<ContestTrack> contestTracks = contestTrackRepository.getContestTrackByContestAndGuildAndEmoteAndDeleted(contest_, guild, event.getEmoji().asUnicodeEmoji().get().getRaw(),false);

								if(contestTracks.isEmpty())
									return channel.createMessage("No contest started!");

								if(contestTracks.size() > 1)
									return channel.createMessage("Same emote found, internal error.");

								ContestTrack contestTrack_ = contestTracks.get(0);

								contestTrack_
										.setCount(contestTrack_
												.getCount() - 1);

								contestTrackRepository.save(contestTrack_);

								Member member = event.getUser()
										.block()
										.asMember(discord_guild.getId())
										.block();

								return channel.createMessage(member.getDisplayName() + " - Vote removed!");
							})
							.then();

					Mono<Void> contestDB = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!contest"))
									return Mono.empty();

								Message message = event.getMessage();

								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(message.getGuildId()
										.get()
										.asLong(), false);

								if(guild == null) {
									guild = new Guild(message.getGuildId()
											.get()
											.asLong(), false);
									guildRepository.save(guild);
								}

								List<Contest> check = contestRepository.getContestsByGuildAndDateAndDeleted(guild,
										LocalDateTime.now()
												.withHour(1)
												.withMinute(0)
												.withSecond(0),
										false);

								if(check.size() > 1)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"There is already a contest!"));

								List<it.emanuelemelini.spotifybestsongweeklypoll.db.model.Playlist> playlists = playlistRepository.getPlaylistsByGuildAndDeleted(guild, false);

								if(playlists.isEmpty())
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("Insert playlist ID first!"));

								if(playlists.size() > 1)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("More than one playlist active!"));

								it.emanuelemelini.spotifybestsongweeklypoll.db.model.Playlist playlist_ = playlists.get(0);

								String playlist = playlist_.getSpotifyId();

								Playlist playlistMapped = getPlaylist(playlist);

								PlaylistSpec playlistSpecMapped = getPlaylistSpec(playlist);

								String thumbnail = playlistSpecMapped.getImages()
										.get(0)
										.getUrl();

								String href = playlistSpecMapped.getExternal_urls()
										.getSpotify();

								playlistSpecThis = playlistSpecMapped;
								playlistThis = playlist;

								List<EmbedCreateFields.Field> fields = new LinkedList<>();

								AtomicInteger atEmbed = new AtomicInteger(0);

								//TODO: check messaggi di errore nelle chiamate (es playlist ID non valido/trovato)

								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

								ContestDay contestDay = contestDayRepository.getContestDayByGuildAndDeleted(guild, false);

								if(contestDay == null)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("Insert Contest Day!"));

								Winner lastWinner = winnerRepository.findTopByGuildAndDeletedOrderByWinnerDateDesc(guild,
										false);
								String last_winner;

								if(lastWinner != null) {
									if(!lastWinner.getWinnerDate()
											.withHour(23)
											.withMinute(59)
											.withSecond(59)
											.isBefore(LocalDateTime.now()
													.withHour(0)
													.withMinute(0)
													.withSecond(1)
													.minusDays(7))) {
										last_winner = lastWinner.getUser()
												.getSpotifyId();
									} else
										last_winner = "";
								} else
									last_winner = "";

								System.out.println("Last winner: " + last_winner);

								final List<Items> itemsFiltered = playlistMapped.getItems()
										.stream()
										.filter(item -> checkDate(LocalDateTime.parse(item.getAdded_at()
														.replace("T", " ")
														.replace("Z", ""), formatter),
												contestDay.getDay()
														.getValue(),
												item.getTrack()
														.getName()))
										.filter(item -> !item.getAdded_by()
												.getId()
												.equalsIgnoreCase(last_winner))
										.collect(Collectors.toList());

								//System.out.println("Filtered: " + itemsFiltered);

								String accesstoken = loginSpotify().getAccess_token();

								List<ContestTrack> contestTracks = new LinkedList<>();

								Contest contest_ = new Contest(guild, LocalDateTime.now().withHour(1).withMinute(0).withSecond(0), false);

								contest_ = contestRepository.save(contest_);

								for(Items item : itemsFiltered) {

									User user = userRepository.getUserBySpotifyIdAndDeleted(item.getAdded_by()
											.getId(), false);
									if(user == null) {
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage("Link Spotify ID: " +
														item.getAdded_by()
																.getId() + " to a Discord ID first!"));
									}

									fields.add(EmbedCreateFields.Field.of(emojisss[atEmbed.get()] + " " + item.getTrack()
													.getName() + " - Votes: 0",
											item.getTrack()
													.getAlbum()
													.getAllArtists(),
											true));

									if(atEmbed.get() % 2 == 1)
										fields.add(EmbedCreateFields.Field.of("\u200b", "\u200b", true));

									UserTrack userTrack = new UserTrack(item.getAdded_by()
											.getId(),
											getUser(item.getAdded_by()
													.getId(), accesstoken),
											item.getTrack()
													.getId(),
											item.getTrack()
													.getName(),
											item.getTrack()
													.getAlbum()
													.getAllArtists());

									//System.out.println("UserTrack: " + userTrack);

									songReaction.put(emojisss[atEmbed.get()], userTrack);

									Song song = songRepository.getSongBySpotifyIdAndDeleted(item.getTrack()
											.getId(), false);

									if(song == null) {
										song = new Song(guild,
												item.getTrack()
												.getName(),
												item.getTrack()
														.getId(),
												item.getTrack()
														.getAlbum()
														.getAllArtists(),
												user,
												false);
										songRepository.save(song);
									}

									contestTracks.add(new ContestTrack(contest_,
											guild,
											song,
											emojisss[atEmbed.get()],
											0,
											false));

									System.out.println(emojisss[atEmbed.get()]);

									atEmbed.getAndIncrement();
								}

								contestTrackRepository.saveAll(contestTracks);

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								boolean role_ = guild.getRoleId() != null && guild.getRoleId() != 0;

								Role role = role_ ? discord_guild.getRoleById(Snowflake.of(guild.getRoleId()))
										.block() : null;

								if(role == null && role_)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"Role not found with saved ID"));

								//System.out.println(songReaction);

								EmbedCreateSpec embed = EmbedCreateSpec.builder()
										.title("Weekly poll")
										.author(EmbedCreateFields.Author.of("Emanuele Melini",
												"https://github.com/EmanueleMelini",
												"https://avatars.githubusercontent.com/u/73402425?v=4"))
										.color(Color.GREEN)
										.description((role_ ? role.getMention() + "\n" : "") +
												"Votazione settimanale della miglior canzone nella playlist di EXP!\nRegole:\n - Si vota ogni mercoledì la canzone preferita più \"originale\"\n - Chi vince una votazione ha la possibilità di inserire una canzone aggiuntiva al prossimo inserimento\n - Le canzoni inserite dal vincitore della settimana precedente non possono essere votate")
										.addAllFields(fields)
										.thumbnail(thumbnail)
										.url(href)
										.timestamp(Instant.now())
										.build();

								AtomicInteger atCount = new AtomicInteger(0);

								Contest finalContest = contestRepository.getContestById(contest_.getId());

								return Mono.from(channel.createMessage(MessageCreateSpec.create()
												.withEmbeds(embed)
												.withAllowedMentions(AllowedMentions.builder()
														.allowRole(role_ ? role.getId() : Snowflake.of(0))
														.build()))
										.flatMap(msg -> {
											finalContest.setMess(msg.getId().asLong());
											contestRepository.save(finalContest);
											return Mono.from(Flux.fromIterable(itemsFiltered)
													.flatMap(track -> msg.addReaction(ReactionEmoji.unicode(emojisss[atCount.getAndIncrement()]))));
										}));

							})
							.then();

					Mono<Void> contest = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!contestlegacy"))
									return Mono.empty();

								Message message = event.getMessage();

								if(messID != null)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"There is already a contest!"));

								String[] command = message.getContent()
										.split(" ");
								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								String playlist;

								if(command.length < 2)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("Insert playlist id!"));

								playlist = command[1];

								Playlist playlistMapped = getPlaylist(playlist);

								PlaylistSpec playlistSpecMapped = getPlaylistSpec(playlist);

								String thumbnail = playlistSpecMapped.getImages()
										.get(0)
										.getUrl();

								String href = playlistSpecMapped.getExternal_urls()
										.getSpotify();

								playlistSpecThis = playlistSpecMapped;
								playlistThis = playlist;

								List<EmbedCreateFields.Field> fields = new LinkedList<>();

								AtomicInteger atEmbed = new AtomicInteger(0);

								//TODO: check messaggi di errore nelle chiamate (es playlist ID non valido/trovato)

								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(message.getGuildId()
										.get()
										.asLong(), false);

								if(guild == null) {
									guild = new Guild(message.getGuildId()
											.get()
											.asLong(), false);
									guildRepository.save(guild);
								}

								ContestDay contestDay = contestDayRepository.getContestDayByGuildAndDeleted(guild, false);

								if(contestDay == null)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("Insert Contest Day!"));

								Winner lastWinner = winnerRepository.findTopByGuildAndDeletedOrderByWinnerDateDesc(guild,
										false);
								String last_winner;

								if(lastWinner != null) {
									if(!lastWinner.getWinnerDate()
											.withHour(23)
											.withMinute(59)
											.withSecond(59)
											.isBefore(LocalDateTime.now()
													.withHour(0)
													.withMinute(0)
													.withSecond(1)
													.minusDays(7))) {
										last_winner = lastWinner.getUser()
												.getSpotifyId();
									} else
										last_winner = "";
								} else
									last_winner = "";

								System.out.println("Last winner: " + last_winner);

								final List<Items> itemsFiltered = playlistMapped.getItems()
										.stream()
										.filter(item -> checkDate(LocalDateTime.parse(item.getAdded_at()
														.replace("T", " ")
														.replace("Z", ""), formatter),
												contestDay.getDay()
														.getValue(),
												item.getTrack()
														.getName()))
										.filter(item -> !item.getAdded_by()
												.getId()
												.equalsIgnoreCase(last_winner))
										.collect(Collectors.toList());

								String accesstoken = loginSpotify().getAccess_token();

								List<ContestTrack> contestTracks = new LinkedList<>();

								Contest contest_ = new Contest(guild, LocalDateTime.now().withHour(1).withMinute(0).withSecond(0), false);

								contest_ = contestRepository.save(contest_);

								for(Items item : itemsFiltered) {

									User user = userRepository.getUserBySpotifyIdAndDeleted(item.getAdded_by()
											.getId(), false);
									if(user == null) {
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage("Link Spotify ID: " +
														item.getAdded_by()
																.getId() + " to a Discord ID first!"));
									}

									fields.add(EmbedCreateFields.Field.of(emojisss[atEmbed.get()] + " " + item.getTrack()
													.getName(),
											item.getTrack()
													.getAlbum()
													.getAllArtists(),
											true));

									if(atEmbed.get() % 2 == 1)
										fields.add(EmbedCreateFields.Field.of("\u200b", "\u200b", true));

									UserTrack userTrack = new UserTrack(item.getAdded_by()
											.getId(),
											getUser(item.getAdded_by()
													.getId(), accesstoken),
											item.getTrack()
													.getId(),
											item.getTrack()
													.getName(),
											item.getTrack()
													.getAlbum()
													.getAllArtists());

									//System.out.println("UserTrack: " + userTrack);

									songReaction.put(emojisss[atEmbed.get()], userTrack);

									Song song_ = songRepository.getSongBySpotifyIdAndDeleted(item.getTrack()
											.getId(), false);

									if(song_ == null) {
										song_ = new Song(guild,
												item.getTrack()
												.getName(),
												item.getTrack()
														.getId(),
												item.getTrack()
														.getAlbum()
														.getAllArtists(),
												user,
												false);
										songRepository.save(song_);
									}

									contestTracks.add(new ContestTrack(contest_,
											guild,
											song_,
											emojisss[atEmbed.get()],
											0,
											false));

									atEmbed.getAndIncrement();
								}

								contestTrackRepository.saveAll(contestTracks);

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								boolean role_ = guild.getRoleId() != null && guild.getRoleId() != 0;

								Role role = role_ ? discord_guild.getRoleById(Snowflake.of(guild.getRoleId()))
										.block() : null;

								if(role == null && role_)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"Role not found with saved ID"));

								//System.out.println(songReaction);

								EmbedCreateSpec embed = EmbedCreateSpec.builder()
										.title("Weekly poll")
										.author(EmbedCreateFields.Author.of("Emanuele Melini",
												"https://github.com/EmanueleMelini",
												"https://avatars.githubusercontent.com/u/73402425?v=4"))
										.color(Color.GREEN)
										.description((role_ ? role.getMention() + "\n" : "") +
												"Votazione settimanale della miglior canzone nella playlist di EXP!\nRegole:\n - Si vota ogni mercoledì la canzone preferita più \"originale\"\n - Chi vince una votazione ha la possibilità di inserire una canzone aggiuntiva al prossimo inserimento\n - Le canzoni inserite dal vincitore della settimana precedente non possono essere votate")
										.addAllFields(fields)
										.thumbnail(thumbnail)
										.url(href)
										.timestamp(Instant.now())
										.build();

								AtomicInteger atCount = new AtomicInteger(0);

								Contest finalContest = contestRepository.getContestById(contest_.getId());

								return Mono.from(channel.createMessage(MessageCreateSpec.create()
												.withEmbeds(embed)
												.withAllowedMentions(AllowedMentions.builder()
														.allowRole(role_ ? role.getId() : Snowflake.of(0))
														.build()))
										.flatMap(msg -> {
											messID = msg.getId();
											finalContest.setMess(messID.asLong());
											contestRepository.save(finalContest);
											return Mono.from(Flux.fromIterable(itemsFiltered)
													.flatMap(track -> msg.addReaction(ReactionEmoji.unicode(emojisss[atCount.getAndIncrement()]))));
										}));
							})
							.then();


					Mono<Void> close = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!closelegacy"))
									return Mono.empty();

								Message message = event.getMessage();
								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								if(messID == null)
									return channel.createMessage("Start contest first!");

								Message contest_message = channel.getMessageById(messID)
										.block();

								if(contest_message == null)
									return channel.createMessage("No contest found, internal error");

								if(contest_message.getReactions()
										.stream()
										.noneMatch(Reaction::selfReacted))
									return channel.createMessage("Contest already closed!");

								boolean testClose = false;
								if(message.getContent()
										.split(" ").length == 2)
									if(message.getContent()
											.split(" ")[1].equalsIgnoreCase("-t"))
										testClose = true;

								List<Reaction> reactions = contest_message.getReactions();

								Map<UserTrack, Integer> top = new HashMap<>();
								LinkedHashMap<UserTrack, Integer> topSorted = new LinkedHashMap<>();

								for(Reaction reaction : reactions) {
									if(reaction.selfReacted()) {
										Optional<String> opt = reaction.getEmoji()
												.asUnicodeEmoji()
												.map(ReactionEmoji.Unicode::getRaw);
										String rawUnicode = opt.orElse("");
										UserTrack userTrack = songReaction.get(rawUnicode);
										Integer count = reaction.getCount();

										top.put(userTrack, count);

										//System.out.println(userTrack.toString());

									}
								}

								top.entrySet()
										.stream()
										.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
										.forEachOrdered(x -> topSorted.put(x.getKey(), x.getValue()));

								Integer count_win = topSorted.entrySet()
										.iterator()
										.next()
										.getValue();

								LinkedHashMap<UserTrack, Integer> tieMap = new LinkedHashMap<>();

								topSorted.entrySet()
										.stream()
										.filter(x -> x.getValue()
												.equals(count_win))
										.forEach(x -> tieMap.put(x.getKey(), x.getValue()));

								contest_message.removeAllReactions()
										.block();

								AtomicInteger atTie = new AtomicInteger(0);

								if(tieMap.entrySet()
										.size() > 1) {
									songReaction.clear();
									tieMap.forEach((userTrack, votes) -> {
										songReaction.put(emojisss[atTie.getAndIncrement()], userTrack);
									});
									tieMessID = messID;
									return channel.createMessage("Contest ended with a tie, start tiebreaker with !tie.");
								}

								String thumbnail = playlistSpecThis.getImages()
										.get(0)
										.getUrl();

								String href = playlistSpecThis.getExternal_urls()
										.getSpotify();

								List<EmbedCreateFields.Field> fields = new LinkedList<>();

								topSorted.forEach((key, value) -> fields.add(EmbedCreateFields.Field.of(
										key.getTrackName() + " - " + key.getTrackAuthors(),
										key.getSpotifyUserName() + " - Voti: " + (value - 1),
										false)));

								UserTrack userWinner = topSorted.entrySet()
										.iterator()
										.next()
										.getKey();

								String winner_id = userWinner.getSpotifyUserID();

								User user = userRepository.getUserBySpotifyIdAndDeleted(winner_id, false);
								if(user == null) {
									return channel.createMessage("Found Spotify ID: " + winner_id + " unlinked!");
								}

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								Member winner_d = discord_guild.getMemberById(Snowflake.of(user.getDiscordId()))
										.block();

								if(winner_d == null)
									return channel.createMessage("Discord User not found with saved Discord ID!");

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(contest_message.getGuildId()
										.get()
										.asLong(), false);

								if(guild == null) {
									guild = new Guild(contest_message.getGuildId()
											.get()
											.asLong(), false);
									guildRepository.save(guild);
								}

								boolean role_ = guild.getRoleId() != null && guild.getRoleId() != 0;

								Role role = role_ ? discord_guild.getRoleById(Snowflake.of(guild.getRoleId()))
										.block() : null;

								if(role == null && role_)
									return channel.createMessage("Role not found with saved ID");

								EmbedCreateSpec embed = EmbedCreateSpec.builder()
										.title("Weekly poll" + (testClose ? " **[Test]**" : ""))
										.author(EmbedCreateFields.Author.of("Emanuele Melini",
												"https://github.com/EmanueleMelini",
												"https://avatars.githubusercontent.com/u/73402425?v=4"))
										.color(Color.GREEN)
										.description((role_ ? role.getMention() + "\n" : "") + "Risultati contest:\n")
										.addAllFields(fields)
										.addField(EmbedCreateFields.Field.of("\u200b", "\u200b", false))
										.addField(EmbedCreateFields.Field.of(
												"Il vicitore del contest è: **" + winner_d.getDisplayName() +
														"**!\nRicorda che hai diritto a inserire ben due canzoni domani!",
												"\u200b",
												true))
										.thumbnail(thumbnail)
										.url(href)
										.timestamp(Instant.now())
										.build();

								if(winnerRepository.getWinnersByUserAndDeleted(user, false)
										.isEmpty()) {
									Winner winner = new Winner(guild,
											getUser(winner_id, loginSpotify().getAccess_token()),
											user,
											LocalDateTime.now());
									if(!testClose)
										winnerRepository.save(winner);
								}

								messID = null;

								return channel.createMessage(MessageCreateSpec.create()
										.withEmbeds(embed)
										.withAllowedMentions(AllowedMentions.builder()
												.allowUser(winner_d.getId())
												.allowRole(role_ ? role.getId() : Snowflake.of(0))
												.build()));
							})
							.then();

					Mono<Void> contest_day = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!day"))
									return Mono.empty();

								Message message = event.getMessage();
								String[] content = message.getContent()
										.split(" ");
								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								String dayStr = content[1];

								//ContestDay.Day day;
								DayOfWeek day;

								switch(dayStr) {
									case "MONDAY":
										day = MONDAY;
										break;
									case "TUESDAY":
										day = TUESDAY;
										break;
									case "WEDNESDAY":
										day = WEDNESDAY;
										break;
									case "THURSDAY":
										day = THURSDAY;
										break;
									case "FRIDAY":
										day = FRIDAY;
										break;
									case "SATURDAY":
										day = SATURDAY;
										break;
									case "SUNDAY":
										day = SUNDAY;
										break;
									default:
										return channel.createMessage("Insert correct DAY");
								}

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(message.getGuildId()
										.get()
										.asLong(), false);
								if(guild == null) {
									guild = new Guild(message.getGuildId()
											.get()
											.asLong(), false);
									guildRepository.save(guild);
								}

								ContestDay contestDay = new ContestDay(guild, day, false);
								contestDayRepository.save(contestDay);

								return channel.createMessage("Contest day inserted correctly!");
							})
							.then();

					Mono<Void> link_discord = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!link"))
									return Mono.empty();

								Message message = event.getMessage();
								String[] content = message.getContent()
										.split(" ");
								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								String spotify_id = content[1];

								long discord_id;

								try {
									discord_id = Long.parseLong(content[2]);
								} catch(NumberFormatException e) {
									return channel.createMessage("Insert a correct discord ID");
								}

								User userS = userRepository.getUserBySpotifyIdAndDeleted(spotify_id, false);

								if(userS != null)
									return channel.createMessage("There is already a linked user with given Spotify ID!");

								User userD = userRepository.getUserByDiscordIdAndDeleted(discord_id, false);

								if(userD != null)
									return channel.createMessage("There is already a linked user with given Discord ID");

								User userAll = userRepository.getUserByDiscordIdAndSpotifyIdAndDeleted(discord_id,
										spotify_id,
										false);

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								long discord_guild_id = discord_guild.getId()
										.asLong();

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(discord_guild_id, false);

								if(guild == null) {
									guild = new Guild(discord_guild_id, false);
									guildRepository.save(guild);
								}

								if(userAll == null) {
									userAll = new User(guild, spotify_id, discord_id, false);
									userRepository.save(userAll);
								} else
									return channel.createMessage(
											"There is already a linked user with given Spotify ID and Discord ID!");

								return channel.createMessage("Spotify ID linked correctly to Discord ID");
							})
							.then();

					Mono<Void> role_discord = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!role"))
									return Mono.empty();

								Message message = event.getMessage();
								String[] content = message.getContent()
										.split(" ");
								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								if(content.length < 2)
									return channel.createMessage("Insert Role ID");

								long roleid;
								try {
									roleid = Long.parseLong(content[1]);
								} catch(NumberFormatException e) {
									return channel.createMessage("Insert a correct Role ID");
								}

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								Role guild_role = discord_guild.getRoleById(Snowflake.of(roleid))
										.block();

								if(guild_role == null)
									return channel.createMessage("Role not found with given ID!");


								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(discord_guild.getId()
										.asLong(), false);

								if(guild == null) {
									guild = new Guild(discord_guild.getId()
											.asLong(), false);
								}

								guild.setRoleId(roleid);
								guildRepository.save(guild);

								return channel.createMessage("Role inserted correctly");
							})
							.then();

					Mono<Void> force_stop = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!forcestop"))
									return Mono.empty();

								Message message = event.getMessage();
								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								if(messID == null)
									return channel.createMessage("There is no started contest to stop!");

								Message contest_message = channel.getMessageById(messID)
										.block();

								if(contest_message == null)
									return channel.createMessage("No contest found, internal error");

								contest_message.removeAllReactions()
										.block();

								messID = null;

								return channel.createMessage("Contest stopped correctly");
							})
							.then();

					Mono<Void> tie_breaker = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!tie"))
									return Mono.empty();

								Message message = event.getMessage();

								if(tieMessID == null) {
									if(messID == null)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"There is no contest to tie!"));
									else
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"There is a contest ongoing!"));
								} else {
									if(messID == null)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"There is already a tiebreaker contest!"));
								}

								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								List<EmbedCreateFields.Field> fields = new LinkedList<>();

								AtomicInteger atEmbed = new AtomicInteger(0);

								//TODO: check messaggi di errore nelle chiamate (es playlist ID non valido/trovato)

								Guild guild = guildRepository.getGuildByGuildIdAndDeleted(message.getGuildId()
										.get()
										.asLong(), false);

								if(guild == null) {
									guild = new Guild(message.getGuildId()
											.get()
											.asLong(), false);
									guildRepository.save(guild);
								}

								ContestDay contestDay = contestDayRepository.getContestDayByGuildAndDeleted(guild, false);

								if(contestDay == null)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("Insert Contest Day!"));

								songReaction.forEach((reaction, userTrack) -> {

									fields.add(EmbedCreateFields.Field.of(
											emojisss[atEmbed.getAndIncrement()] + " " + userTrack.getTrackName(),
											userTrack.getTrackAuthors(),
											true));

									if(atEmbed.get() % 2 == 1)
										fields.add(EmbedCreateFields.Field.of("\u200b", "\u200b", true));

									//System.out.println("UserTrack: " + userTrack);

									atEmbed.getAndIncrement();
								});

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								boolean role_ = guild.getRoleId() != null && guild.getRoleId() != 0;

								Role role = role_ ? discord_guild.getRoleById(Snowflake.of(guild.getRoleId()))
										.block() : null;

								if(role == null && role_)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"Role not found with saved ID"));

								//System.out.println(songReaction);

								EmbedCreateSpec embed = EmbedCreateSpec.builder()
										.title("Tie breaker")
										.author(EmbedCreateFields.Author.of("Emanuele Melini",
												"https://github.com/EmanueleMelini",
												"https://avatars.githubusercontent.com/u/73402425?v=4"))
										.color(Color.GREEN)
										.description((role_ ? role.getMention() + "\n" : "") +
												"Spareggio della votazione settimanale.\nRegole:\n - In caso di spareggio a 2 i contendenti non possono votare.")
										.addAllFields(fields)
										.thumbnail(playlistSpecThis.getImages()
												.get(0)
												.getUrl())
										.url(playlistSpecThis.getExternal_urls()
												.getSpotify())
										.timestamp(Instant.now())
										.build();

								AtomicInteger atCount = new AtomicInteger(0);


								return Mono.from(channel.createMessage(MessageCreateSpec.create()
												.withEmbeds(embed)
												.withAllowedMentions(AllowedMentions.builder()
														.allowRole(role_ ? role.getId() : Snowflake.of(0))
														.build()))
										.flatMap(msg -> {
											messID = msg.getId();
											tieMessID = null;
											return Mono.from(Flux.fromStream(songReaction.keySet()
															.stream())
													.flatMap(track -> msg.addReaction(ReactionEmoji.unicode(emojisss[atCount.getAndIncrement()]))));
										}));

							})
							.then();

					Mono<Void> addReaction = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!react"))
									return Mono.empty();

								Message message = event.getMessage();
								String[] content = message.getContent()
										.split(" ");

								if(content.length < 3)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"Insert message ID and reaction number!"));

								MessageChannel channel = message.getChannel()
										.block();

								if(channel == null)
									return Mono.empty();

								discord4j.core.object.entity.Guild discord_guild = message.getGuild()
										.block();

								if(discord_guild == null)
									return Mono.empty();

								String mess_react = content[1];
								String count = content[2];

								long messid_react;

								try {
									messid_react = Long.parseLong(mess_react);
								} catch(NumberFormatException e) {
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("Insert correct ID!"));
								}

								int count_react;

								try {
									count_react = Integer.parseInt(count);
								} catch(NumberFormatException e) {
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"Insert correct number of reactions!"));
								}

								Message message_react = channel.getMessageById(Snowflake.of(messid_react))
										.block();

								if(message_react == null)
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"No message found with given ID!"));

								List<Integer> uselessList = new LinkedList<>();

								for(int i = 0; i < count_react; i++) {
									uselessList.add(i);
								}

								AtomicInteger atCount = new AtomicInteger(0);

								return Mono.from(Flux.fromStream(uselessList.stream())
										.flatMap(ignored -> message_react.addReaction(ReactionEmoji.unicode(emojisss[atCount.getAndIncrement()]))));

							})
							.then();

					return testBot.and(contest)
							.and(print)
							.and(close)
							.and(contest_day)
							.and(link_discord)
							.and(role_discord)
							.and(force_stop)
							.and(tie_breaker)
							.and(disconnectLennosc)
							.and(kickLennosc)
							.and(reactionAddEvent)
							.and(reactionRemoveEvent)
							.and(contestDB)
							.and(addReaction)
							.and(playlist_id);
				});
		client.block();

	}

	/**
	 * A method that use the Spotify's API to get the data of all the Songs on a given Playlist
	 *
	 * @param playlist The Spotify playlist ID
	 * @return A Playlist JSON Object that contains the Spotify call result
	 */
	public Playlist getPlaylist(String playlist) {

		ObjectMapper mapper = new ObjectMapper();
		Playlist playlistMapped = new Playlist();

		try {

			String accessToken = loginSpotify().getAccess_token();

			// TODO: Cambiare da offset a loop di chiamate con next (https://developer.spotify.com/documentation/web-api/reference/#/operations/get-playlist)

			URL urlPlaylist = new URL("https://api.spotify.com/v1/playlists/" + playlist +
					"/tracks?fields=items(added_by.id,track.id,track.name,track.album.artists,added_at)&offset=50");
			HttpURLConnection connPlaylist = (HttpURLConnection) urlPlaylist.openConnection();

			connPlaylist.setRequestProperty("Authorization", "Bearer " + accessToken);

			connPlaylist.setRequestProperty("Content-Type", "application/json");
			connPlaylist.setRequestMethod("GET");


			BufferedReader inPlaylist = new BufferedReader(new InputStreamReader(connPlaylist.getInputStream()));
			String outputPlaylist;

			StringBuilder responsePlaylist = new StringBuilder();
			while((outputPlaylist = inPlaylist.readLine()) != null) {
				responsePlaylist.append(outputPlaylist);
			}

			inPlaylist.close();

			//System.out.println("Items: " + responsePlaylist);

			playlistMapped = mapper.readValue(responsePlaylist.toString(), Playlist.class);

		} catch(IOException e) {
			System.out.println("IO " + e.getMessage());
		}

		return playlistMapped;
	}

	/**
	 * A method that use the Spotify's API to get the User displayed name from a given User ID
	 *
	 * @param id          The Spotify User ID
	 * @param accessToken The Spotify API access token
	 * @return The User displayed name
	 */
	public String getUser(String id, String accessToken) {

		try {

			URL urlUser = new URL("https://api.spotify.com/v1/users/" + id);
			HttpURLConnection connectionUser = (HttpURLConnection) urlUser.openConnection();

			connectionUser.setRequestProperty("Content-Type", "application/json");
			connectionUser.setRequestProperty("Authorization", "Bearer " + accessToken);
			connectionUser.setRequestMethod("GET");

			BufferedReader inUser = new BufferedReader(new InputStreamReader(connectionUser.getInputStream()));

			String outputUser;

			StringBuilder responseUser = new StringBuilder();
			while((outputUser = inUser.readLine()) != null) {
				responseUser.append(outputUser);
			}

			inUser.close();
			//System.out.println("User: " + responseUser);

			return new JSONObject(responseUser.toString()
					.replace(":", ": ")).getString("display_name");

		} catch(Exception e) {
			System.out.println(e.getMessage());
			return "Error";
		}

	}

	/**
	 * A method that use the Spotify's API to get the data of a given Playlist
	 *
	 * @param playlist The Spotify Playlist ID
	 * @return A PlaylistSpec JSON Object that contains the Spotify call result
	 */
	public PlaylistSpec getPlaylistSpec(String playlist) {

		ObjectMapper mapper = new ObjectMapper();
		PlaylistSpec playlistSpecMapped = new PlaylistSpec();

		try {

			String accessTokenSpec = loginSpotify().getAccess_token();

			URL urlSpec = new URL(
					"https://api.spotify.com/v1/playlists/" + playlist + "?fields=external_urls.spotify,images.url");
			HttpURLConnection connSpec = (HttpURLConnection) urlSpec.openConnection();

			connSpec.setRequestProperty("Authorization", "Bearer " + accessTokenSpec);
			connSpec.setRequestProperty("Content-Type", "application/json");
			connSpec.setRequestMethod("GET");

			BufferedReader inSpec = new BufferedReader(new InputStreamReader(connSpec.getInputStream()));
			String outputSpec;

			StringBuilder responseSpec = new StringBuilder();
			while((outputSpec = inSpec.readLine()) != null) {
				responseSpec.append(outputSpec);
			}

			inSpec.close();

			//System.out.println("Spec: " + responseSpec);
			playlistSpecMapped = mapper.readValue(responseSpec.toString(), PlaylistSpec.class);

		} catch(IOException e) {
			System.out.println("IO " + e.getMessage());
		}

		return playlistSpecMapped;
	}

	/**
	 * A method that print in console the full output of the Spotify call to the Playlist's API
	 *
	 * @param playlist The Spotify Playlist ID
	 */
	public void printAllPlaylist(String playlist) {

		try {

			String accessTokenAll = loginSpotify().getAccess_token();

			URL urlAll = new URL("https://api.spotify.com/v1/playlists/" + playlist);
			HttpURLConnection connAll = (HttpURLConnection) urlAll.openConnection();

			connAll.setRequestProperty("Authorization", "Bearer " + accessTokenAll);
			connAll.setRequestProperty("Content-Type", "application/json");
			connAll.setRequestMethod("GET");

			BufferedReader inAll = new BufferedReader(new InputStreamReader(connAll.getInputStream()));
			String outputAll;

			StringBuilder responseAll = new StringBuilder();
			while((outputAll = inAll.readLine()) != null) {
				responseAll.append(outputAll);
			}

			inAll.close();

			System.out.println("!Print: " + responseAll);

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * A metod that calls Spotify's API for login
	 *
	 * @return A Login JSON Object that contains the Spotify call return
	 */
	public Login loginSpotify() {

		//TODO: usare Spotify flow con il refresh del token invece di crearne uno nuovo ogni chiamata

		String clientID = getClientID();
		String clientIDSecret = getCliendIDsecret();

		ObjectMapper mapper = new ObjectMapper();
		Login loginMapped = new Login();

		try {

			String paramsLogin = "grant_type=client_credentials";
			byte[] postDataLogin = paramsLogin.getBytes();
			String clientLoginTo64 = clientID + ":" + clientIDSecret;
			byte[] accessLogin = clientLoginTo64.getBytes();
			int postDataLenghtLogin = postDataLogin.length;

			URL urlLogin = new URL("https://accounts.spotify.com/api/token");
			HttpURLConnection connLogin = (HttpURLConnection) urlLogin.openConnection();
			connLogin.setDoOutput(true);
			connLogin.setRequestMethod("POST");
			String base64 = Base64.getEncoder()
					.encodeToString(accessLogin);

			connLogin.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connLogin.setRequestProperty("Authorization", "Basic " + base64);
			connLogin.setRequestProperty("charset", "utf-8");
			connLogin.setRequestProperty("Content-Lenght", Integer.toString(postDataLenghtLogin));

			try(DataOutputStream wr = new DataOutputStream(connLogin.getOutputStream())) {
				wr.write(postDataLogin);
			}

			BufferedReader inLogin = new BufferedReader(new InputStreamReader(connLogin.getInputStream()));
			String outputLogin;

			StringBuilder responseLogin = new StringBuilder();
			while((outputLogin = inLogin.readLine()) != null) {
				responseLogin.append(outputLogin);
			}

			//System.out.println("Login: " + responseLogin);
			inLogin.close();

			loginMapped = mapper.readValue(responseLogin.toString(), Login.class);

		} catch(IOException e) {
			System.out.println("IO " + e.getMessage());
		}

		return loginMapped;
	}

	/**
	 * A method that (for now) check if the given date is after the last Wednesday
	 *
	 * @param date The Date to be checked
	 * @return A boolean: true if the Date is after, false if the Date is before
	 */
	public boolean checkDate(LocalDateTime date, int contestDay, String song) {

		LocalDateTime ora = LocalDateTime.now()
				.withHour(20)
				.withMinute(0)
				.withSecond(0);

		int days = ora.getDayOfWeek()
				.getValue() - contestDay;

		days = days <= 0 ? days + 7 : days;

		/*
		System.out.println("Song: " + song);
		System.out.println("Date 1: " + date);
		System.out.println("ContestDay: " + contestDay);
		System.out.println("Days: " + days);
		System.out.println("Result: " + ora.minusDays(days)
				.isBefore(date));
		*/

		/*
		switch(ora.getDayOfWeek()) {
			case MONDAY:
				days = 5;
				break;
			case TUESDAY:
				days = 6;
				break;
			case WEDNESDAY:
				days = 7;
				break;
			case THURSDAY:
				days = 1;
				break;
			case FRIDAY:
				days = 2;
				break;
			case SATURDAY:
				days = 3;
				break;
			case SUNDAY:
				days = 4;
				break;
			default:
				days = 0;
				break;
		}
		 */

		return ora.minusDays(days)
				.isBefore(date);
	}

	public List<EmbedCreateFields.Field> createUpdatedEmbed(List<ContestTrack> contestTracks) throws IllegalArgumentException {

		List<EmbedCreateFields.Field> fields = new LinkedList<>();

		AtomicInteger atEmbed = new AtomicInteger(0);

		for(ContestTrack song : contestTracks) {
			User user = userRepository.getUserBySpotifyIdAndDeleted(song.getSong()
					.getUser()
					.getSpotifyId(), false);
			if(user == null) {
				throw new IllegalArgumentException("Link Spotify ID: " + song.getSong()
						.getUser()
						.getSpotifyId() + " to a Discord ID first!");
			}

			fields.add(EmbedCreateFields.Field.of(emojisss[atEmbed.get()] + " " + song.getSong()
							.getName() + " - Votes: " + song.getCount(),
					song.getSong()
							.getAuthors(),
					true));

			if(atEmbed.get() % 2 == 1)
				fields.add(EmbedCreateFields.Field.of("\u200b", "\u200b", true));

			atEmbed.getAndIncrement();
		}

		return fields;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpotifyBestSongWeeklyPoll.class);
	}

	@Override
	public void run(String... args) throws Exception {
		mainSpotify();
	}

}
