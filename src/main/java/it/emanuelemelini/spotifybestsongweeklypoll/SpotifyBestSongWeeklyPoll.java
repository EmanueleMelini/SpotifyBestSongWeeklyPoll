package it.emanuelemelini.spotifybestsongweeklypoll;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.Reaction;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import it.emanuelemelini.spotifybestsongweeklypoll.lib.*;
import org.json.JSONObject;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A Discord bot that create a contest for voting the best song on the given playlist ID
 */
@SpringBootApplication
public class SpotifyBestSongWeeklyPoll implements CommandLineRunner {

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
	private Map<String, String> songReaction = new HashMap<>();

	/**
	 * The Discord message contest Snowflake
	 */
	private Snowflake messID;

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

								Message message = event.getMessage();

								if(message.getContent()
										.equalsIgnoreCase("Pipo"))
									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage("Palle"));

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

					Mono<Void> contest = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								Message message = event.getMessage();

								String[] command = message.getContent()
										.split(" ");

								String playlist;

								Map<String, String> mapResult;
								List<Track> tracksReturn;

								if(command[0].equalsIgnoreCase("!contest")) {

									if(command.length < 2)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage("Insert playlist id!"));

									playlist = command[1];

									//mapResult = getPlaylist(playlist);
									Playlist playlistMapped = getPlaylist(playlist);
									//tracksReturn = getPlaylist(playlist);

									/*
									String[] hrefthumb = getPlaylistSpec(playlist);
									String thumbnail = hrefthumb[0];
									String href = hrefthumb[1];
									 */

									PlaylistSpec playlistSpecMapped = getPlaylistSpec(playlist);
									String thumbnail = playlistSpecMapped.getImages()
											.get(0)
											.getUrl();
									String href = playlistSpecMapped.getExternal_urls()
											.getSpotify();

									List<EmbedCreateFields.Field> fields = new LinkedList<>();

									AtomicInteger atEmbed = new AtomicInteger(0);

									//TODO: check messaggi di errore nelle chiamate (es playlist ID non valido/trovato)

									/*
									mapResult.forEach((nameUser, name) -> {
										fields.add(EmbedCreateFields.Field.of(emojisss[atEmbed.get()] + " " + name,
												nameUser,
												true));
										if(atEmbed.get() % 2 == 1)
											fields.add(EmbedCreateFields.Field.of("\u200b", "\u200b", true));
										songReaction.put(emojisss[atEmbed.get()], name);
										atEmbed.getAndIncrement();
									});

									 */

									DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

									final List<Items> itemsFiltered = playlistMapped.getItems()
											.stream()
											.filter(item -> checkDate(LocalDateTime.parse(item.getAdded_at()
													.replace("T", " ")
													.replace("Z", ""), formatter)))
											.collect(Collectors.toList());

									for(Items item : itemsFiltered) {

										fields.add(EmbedCreateFields.Field.of(emojisss[atEmbed.get()] + " " + item.getTrack()
														.getName(),
												item.getTrack()
														.getAlbum()
														.getAllArtists(),
												true));
										if(atEmbed.get() % 2 == 1)
											fields.add(EmbedCreateFields.Field.of("\u200b", "\u200b", true));
										songReaction.put(emojisss[atEmbed.get()],
												item.getTrack()
														.getName());
										atEmbed.getAndIncrement();
									}

									System.out.println(songReaction);

									EmbedCreateSpec embed = EmbedCreateSpec.builder()
											.title("Weekly poll")
											.author(EmbedCreateFields.Author.of("Emanuele Melini",
													"https://github.com/EmanueleMelini",
													"https://avatars.githubusercontent.com/u/73402425?v=4"))
											.color(Color.GREEN)
											.description(
													"Votazione settimanale della miglior canzone nella playlist di EXP!\nRegole:\n - Si vota ogni mercoledì la canzone preferita più \"originale\"\n - Chi vince una votazione ha la possibilità di inserire una canzone aggiuntiva al prossimo inserimento\n - Le canzoni inserite dal vincitore della settimana precedente non possono essere votate")
											.addAllFields(fields)
											.thumbnail(thumbnail)
											.url(href)
											.timestamp(Instant.now())
											.build();

									AtomicInteger atCount = new AtomicInteger(0);

									/*
									return message.getChannel()
											.flatMap(messageChannel -> Mono.from(messageChannel.createMessage(embed)
													.flatMap(msg -> {
														messID = msg.getId();
														return Mono.from(Flux.fromIterable(mapResult.values())
																.flatMap(name -> msg.addReaction(ReactionEmoji.unicode(emojisss[atCount.getAndIncrement()]))));
													})));

									 */

									return message.getChannel()
											.flatMap(messageChannel -> Mono.from(messageChannel.createMessage(embed)
													.flatMap(msg -> {
														messID = msg.getId();
														return Mono.from(Flux.fromIterable(itemsFiltered)
																.flatMap(track -> msg.addReaction(ReactionEmoji.unicode(emojisss[atCount.getAndIncrement()]))));
													})));

								}


								return Mono.empty();
							})
							.then();


					Mono<Void> close = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!close"))
									return Mono.empty();

								Message message;
								try {
									message = event.getMessage()
											.getChannel()
											.block()
											.getMessageById(messID)
											.block();
								} catch(NullPointerException e) {
									System.out.println(e.getMessage());
									return event.getMessage()
											.getChannel()
											.flatMap(channel -> channel.createMessage("Start contest first!"));
								}

								if(message.getReactions()
										.stream()
										.noneMatch(Reaction::selfReacted))
									return event.getMessage()
											.getChannel()
											.flatMap(channel -> channel.createMessage("Contest already closed!"));

								List<Reaction> reactions = message.getReactions();

								StringBuilder reacmess = new StringBuilder();

								Map<String, Integer> top = new HashMap<>();
								LinkedHashMap<String, Integer> topSorted = new LinkedHashMap<>();

								for(Reaction reaction : reactions) {
									if(reaction.selfReacted()) {
										Optional<String> opt = reaction.getEmoji()
												.asUnicodeEmoji()
												.map(ReactionEmoji.Unicode::getRaw);
										String rawUnicode = opt.orElse("");
										String song = songReaction.get(rawUnicode);
										Integer count = reaction.getCount();

										top.put(song, count);

									}
								}

								top.entrySet()
										.stream()
										.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
										.forEachOrdered(x -> topSorted.put(x.getKey(), x.getValue()));

								//TODO: Embed

								List<EmbedCreateFields.Field> fields = new LinkedList<>();

								topSorted.forEach((key, value) -> {
									fields.add(EmbedCreateFields.Field.of(key, "Voti: " + (value - 1),
										false));
								});

								EmbedCreateSpec embed = EmbedCreateSpec.builder()
										.title("Weekly poll")
										.author(EmbedCreateFields.Author.of("Emanuele Melini",
												"https://github.com/EmanueleMelini",
												"https://avatars.githubusercontent.com/u/73402425?v=4"))
										.color(Color.GREEN)
										.description(
												"Risultati contest:\n")
										.addAllFields(fields)
										.timestamp(Instant.now())
										.build();

								topSorted.forEach((key, value) -> {
									reacmess.append(key)
											.append(" - ")
											.append(value - 1)
											.append("\n");
								});

								message.removeAllReactions()
										.block();

								return event.getMessage()
										.getChannel()
										.flatMap(channel -> channel.createMessage(embed));
							})
							.then();

					return testBot.and(contest)
							.and(print)
							.and(close);
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

			URL urlPlaylist = new URL("https://api.spotify.com/v1/playlists/" + playlist +
					"/tracks?fields=items(added_by.id,track.name,track.album.artists,added_at)");
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

			System.out.println("Items: " + responsePlaylist);

			playlistMapped = mapper.readValue(responsePlaylist.toString(), Playlist.class);

			for(Items item : playlistMapped.getItems()) {
				item.getAdded_by()
						.setId(getUser(item.getAdded_by()
								.getId(), accessToken));
			}

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

			StringBuffer responseUser = new StringBuffer();
			while((outputUser = inUser.readLine()) != null) {
				responseUser.append(outputUser);
			}

			inUser.close();
			System.out.println("User: " + responseUser);

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

			System.out.println("Spec: " + responseSpec);
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

			System.out.println("All: " + responseAll);

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

			System.out.println("Login: " + responseLogin);
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
	public boolean checkDate(LocalDateTime date) {

		//TODO: generalizzare il controllo da solo mercoledì a un giorno scelto da comando e salvato su db

		LocalDateTime ora = LocalDateTime.now();
		ora = ora.withHour(0)
				.withMinute(0)
				.withSecond(0);
		int days;

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

		return ora.minusDays(days)
				.isBefore(date);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpotifyBestSongWeeklyPoll.class);
	}

	@Override
	public void run(String... args) throws Exception {
		mainSpotify();
	}

}
