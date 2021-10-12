package it.emanuelemelini.spotifybestsongweeklypoll;

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
import org.json.JSONArray;
import org.json.JSONObject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SpotifyBestSongWeeklyPoll {

	private final String clientID;
	private final String cliendIDsecret;

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

	private static SpotifyBestSongWeeklyPoll instance;

	private Map<String, String> songReaction = new HashMap<>();

	private Snowflake messID;

	public static SpotifyBestSongWeeklyPoll getInstance() {
		return instance;
	}

	public static SpotifyBestSongWeeklyPoll open(String clientID, String clientIDsecret) throws IllegalStateException {

		if(instance != null)
			throw new IllegalStateException();

		return instance = new SpotifyBestSongWeeklyPoll(clientID, clientIDsecret);
	}

	public SpotifyBestSongWeeklyPoll(String clientID, String clientIDsecret) {
		this.clientID = clientID;
		this.cliendIDsecret = clientIDsecret;
	}

	public String getCliendIDsecret() {
		return cliendIDsecret;
	}

	public String getClientID() {
		return clientID;
	}

	public static void main(String[] args) {

		Mono<Void> client = DiscordClient.create(args[0])
				.withGateway((GatewayDiscordClient gatewayDiscordClient) -> {
					Mono<Void> mess = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

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

									SpotifyBestSongWeeklyPoll instance = SpotifyBestSongWeeklyPoll.getInstance();

									if(instance == null)
										return event.getMessage()
												.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"Insert clientID and secret clientID first"));

									instance.printAllPlaylist(command[1]);
								}
								return Mono.empty();
							})
							.then();

					Mono<Void> cid = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {
								Message message = event.getMessage();
								String[] command = message.getContent()
										.split(" ");


								if(command[0].equalsIgnoreCase("!client")) {

									if(command.length <= 2)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"Insert clientID and secrest clientID"));

									try {
										SpotifyBestSongWeeklyPoll.open(command[1], command[2]);
									} catch(IllegalStateException e) {
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"clientID and secret cliendID already inserted!"));
									}

									return message.getChannel()
											.flatMap(messageChannel -> messageChannel.createMessage(
													"clientID and secredt clientID inserted correctly!"));
								}
								return Mono.empty();
							})
							.then();

					Mono<Void> comm = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								SpotifyBestSongWeeklyPoll instance = SpotifyBestSongWeeklyPoll.getInstance();

								Message message = event.getMessage();

								String[] command = message.getContent()
										.split(" ");

								String playlist;

								Map<String, String> mapResult;

								if(command[0].equalsIgnoreCase("!contest")) {

									if(instance == null)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"Insert clientID and secret clientID first"));

									if(command.length < 2)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage("Insert playlist id!"));

									playlist = command[1];

									mapResult = instance.getPlaylist(playlist);

									String[] hrefthumb = instance.getPlaylistSpec(playlist);

									String thumbnail = hrefthumb[0];
									String href = hrefthumb[1];

									List<EmbedCreateFields.Field> fields = new LinkedList<>();

									AtomicInteger atEmbed = new AtomicInteger(0);

									mapResult.forEach((nameUser, name) -> {
										fields.add(EmbedCreateFields.Field.of(instance.emojisss[atEmbed.get()] + " " + name,
												nameUser,
												true));
										if(atEmbed.get() % 2 == 1)
											fields.add(EmbedCreateFields.Field.of("\u200b", "\u200b", false));
										instance.songReaction.put(instance.emojisss[atEmbed.get()], name);
										atEmbed.getAndIncrement();
									});

									System.out.println(instance.songReaction);

									EmbedCreateSpec embed = EmbedCreateSpec.builder()
											.title("Weekly poll")
											.author(EmbedCreateFields.Author.of("Emanuele Melini",
													"https://github.com/EmanueleMelini",
													"https://avatars.githubusercontent.com/u/73402425?v=4"))
											.color(Color.GREEN)
											.description("Votazione settimanale della miglior canzone nella playlist di EXP!")
											.addAllFields(fields)
											.thumbnail(thumbnail)
											.url(href)
											.timestamp(Instant.now())
											.build();

									AtomicInteger atCount = new AtomicInteger(0);

									return message.getChannel()
											.flatMap(messageChannel -> Mono.from(messageChannel.createMessage(embed)
													.flatMap(msg -> {
														instance.messID = msg.getId();
														return Mono.from(Flux.fromIterable(mapResult.values())
																.flatMap(name -> msg.addReaction(ReactionEmoji.unicode(instance.emojisss[atCount.getAndIncrement()]))));
													})));

								}


								return Mono.empty();
							})
							.then();


					Mono<Void> close = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								SpotifyBestSongWeeklyPoll instance = SpotifyBestSongWeeklyPoll.getInstance();

								if(!event.getMessage()
										.getContent()
										.split(" ")[0].equalsIgnoreCase("!close"))
									return Mono.empty();

								if(instance == null)
									return event.getMessage()
											.getChannel()
											.flatMap(channel -> channel.createMessage("Start contest first!"));

								Message message;
								try {
									message = event.getMessage()
											.getChannel()
											.block()
											.getMessageById(instance.messID)
											.block();
								} catch(NullPointerException e) {
									System.out.println(e.getMessage());
									return event.getMessage()
											.getChannel()
											.flatMap(channel -> channel.createMessage("Start contest first!"));
								}

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

										String song = instance.songReaction.get(rawUnicode);
										Integer count = reaction.getCount();

										top.put(song, count);

									}
								}

								top.entrySet()
										.stream()
										.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
										.forEachOrdered(x -> topSorted.put(x.getKey(), x.getValue()));

								topSorted.forEach((key, value) -> {
									reacmess.append(key)
											.append(" - ")
											.append(value - 1)
											.append("\n");
								});

								return event.getMessage()
										.getChannel()
										.flatMap(channel -> channel.createMessage(reacmess.toString()));
							})
							.then();

					return mess.and(comm)
							.and(cid)
							.and(print)
							.and(close);
				});
		client.block();

	}

	public Map<String, String> getPlaylist(String playlist) {

		SpotifyBestSongWeeklyPoll instance = SpotifyBestSongWeeklyPoll.getInstance();
		Map<String, String> mapR = new HashMap<>();
		Map<String, String> mapReturn = new HashMap<>();

		try {

			String accessToken = instance.loginSpotify();

			URL url = new URL("https://api.spotify.com/v1/playlists/" + playlist +
					"/tracks?fields=items(added_by.id,track.name)");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestProperty("Authorization", "Bearer " + accessToken);

			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("GET");


			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;
			JSONArray array;

			StringBuffer response = new StringBuffer();
			while((output = in.readLine()) != null) {
				response.append(output);
			}

			in.close();

			System.out.println(response);

			String str = response.toString()
					.replace(" ", "");

			JSONObject object = new JSONObject(str);

			array = object.getJSONArray("items");

			for(int i = 0; i < array.length(); i++) {
				mapR.put(array.getJSONObject(i)
								.getJSONObject("added_by")
								.getString("id"),
						array.getJSONObject(i)
								.getJSONObject("track")
								.getString("name"));
			}

			mapR.forEach((id, name) -> {

				try {

					mapReturn.put(instance.getUser(id, accessToken), name);

				} catch(Exception e) {
					System.out.println(e.getMessage());
				}
			});

		} catch(IOException e) {
			System.out.println("IO " + e.getMessage());
		}

		return mapReturn;
	}

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
			System.out.println(responseUser);

			return new JSONObject(responseUser.toString()
					.replace(":", ": ")).getString("display_name");

		} catch(Exception e) {
			System.out.println(e.getMessage());
			return "Error";
		}

	}

	public String[] getPlaylistSpec(String playlist) {

		SpotifyBestSongWeeklyPoll instance = SpotifyBestSongWeeklyPoll.getInstance();

		String[] returned = new String[2];

		try {

			String accessTokenSpec = instance.loginSpotify();

			URL urlSpec = new URL("https://api.spotify.com/v1/playlists/" + playlist + "?fields=href,images.url");
			HttpURLConnection connSpec = (HttpURLConnection) urlSpec.openConnection();

			connSpec.setRequestProperty("Authorization", "Bearer " + accessTokenSpec);
			connSpec.setRequestProperty("Content-Type", "application/json");
			connSpec.setRequestMethod("GET");


			BufferedReader inSpec = new BufferedReader(new InputStreamReader(connSpec.getInputStream()));
			String outputSpec;

			StringBuffer responseSpec = new StringBuffer();
			while((outputSpec = inSpec.readLine()) != null) {
				responseSpec.append(outputSpec);
			}

			inSpec.close();

			System.out.println(responseSpec);

			String strSpec = responseSpec.toString()
					.replace(" ", "");

			JSONObject objectSpec = new JSONObject(strSpec);

			returned[0] = objectSpec.getJSONArray("images")
					.getJSONObject(0)
					.getString("url");
			returned[1] = objectSpec.getString("href");

		} catch(IOException e) {
			System.out.println("IO " + e.getMessage());
			returned[0] = "Error";
			returned[1] = "Error";
		}

		return returned;
	}

	public void printAllPlaylist(String playlist) {

		SpotifyBestSongWeeklyPoll instance = SpotifyBestSongWeeklyPoll.getInstance();

		try {

			String accessTokenAll = instance.loginSpotify();

			URL urlAll = new URL("https://api.spotify.com/v1/playlists/" + playlist);
			HttpURLConnection connAll = (HttpURLConnection) urlAll.openConnection();

			connAll.setRequestProperty("Authorization", "Bearer " + accessTokenAll);
			connAll.setRequestProperty("Content-Type", "application/json");
			connAll.setRequestMethod("GET");

			BufferedReader inAll = new BufferedReader(new InputStreamReader(connAll.getInputStream()));
			String outputAll;

			StringBuffer response = new StringBuffer();
			while((outputAll = inAll.readLine()) != null) {
				response.append(outputAll);
			}

			inAll.close();

			System.out.println(response);

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public String loginSpotify() {

		SpotifyBestSongWeeklyPoll instance = SpotifyBestSongWeeklyPoll.getInstance();

		String clientID = instance.getClientID();
		String clientIDSecret = instance.getCliendIDsecret();
		String accessToken;

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
			System.out.println("Base: " + base64);
			connLogin.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connLogin.setRequestProperty("Authorization", "Basic " + base64);
			connLogin.setRequestProperty("charset", "utf-8");
			connLogin.setRequestProperty("Content-Lenght", Integer.toString(postDataLenghtLogin));
			try(DataOutputStream wr = new DataOutputStream(connLogin.getOutputStream())) {
				wr.write(postDataLogin);
			}
			BufferedReader inLogin = new BufferedReader(new InputStreamReader(connLogin.getInputStream()));
			String outputLogin;

			StringBuffer responseLogin = new StringBuffer();
			while((outputLogin = inLogin.readLine()) != null) {
				responseLogin.append(outputLogin);
			}
			System.out.println("Login: " + responseLogin);
			inLogin.close();

			String strLogin = responseLogin.toString()
					.replace(":", ": ");
			System.out.println(strLogin);
			JSONObject objectLogin = new JSONObject(strLogin);
			System.out.println(objectLogin);
			accessToken = objectLogin.getString("access_token");
			System.out.println("Token: " + accessToken);

		} catch(IOException e) {
			System.out.println("IO " + e.getMessage());
			accessToken = "Error";
		}

		return accessToken;
	}

}
