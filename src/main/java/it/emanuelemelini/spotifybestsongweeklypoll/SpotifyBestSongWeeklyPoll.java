package it.emanuelemelini.spotifybestsongweeklypoll;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.json.JSONArray;
import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class SpotifyBestSongWeeklyPoll {

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

					Mono<Void> comm = gatewayDiscordClient.on(MessageCreateEvent.class, event -> {

								Message message = event.getMessage();
								String[] command = message.getContent()
										.split(" ");

								//TODO: auto token


								String playlist;
								String token;

								if(command[0].equalsIgnoreCase("!contest")) {

									if(command.length <= 2)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage("Insert playlist id!"));

									playlist = command[1];
									token = command[2];

									try {
										//.id,added_by.displayname

										//

										URL url = new URL("https://api.spotify.com/v1/playlists/" + playlist + "/tracks?fields=items(added_by.id,track.name)");
										HttpURLConnection conn = (HttpURLConnection) url.openConnection();

										conn.setRequestProperty("Authorization", "Bearer " + token);

										conn.setRequestProperty("Content-Type", "application/json");
										conn.setRequestMethod("GET");


										BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
										String output;
										Set<String> ids = new HashSet<>();
										JSONArray array;
										Set<String> names = new HashSet<>();

										StringBuffer response = new StringBuffer();
										while((output = in.readLine()) != null) {
											response.append(output);
										}

										in.close();
										System.out.println(response);
										String str = response.toString().replace(" ", "");
										JSONObject object = new JSONObject(str);
										array = object.getJSONArray("items");
										for(int i = 0; i < array.length(); i++) {
											ids.add(array.getJSONObject(i).getJSONObject("added_by").getString("id"));
											names.add(array.getJSONObject(i).getJSONObject("track").getString("name"));
										}

										String messObj = "";

										for(int i = 0; i < ids.size(); i++) {
											messObj = messObj + ids.toArray()[i] + ":" + names.toArray()[i] + "\n";
										}

										final String messagefinal = messObj;

										//TODO: better message and poll

										return message.getChannel().flatMap(messageChannel -> messageChannel.createMessage(messagefinal));

									} catch(IOException e) {
										System.out.printf("IO " + e.getMessage());
									}

								}

								return Mono.empty();
							})
							.then();

					return mess.and(comm);
				});
		client.block();

	}

}
