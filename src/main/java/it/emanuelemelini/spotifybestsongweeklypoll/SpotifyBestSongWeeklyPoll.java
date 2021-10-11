package it.emanuelemelini.spotifybestsongweeklypoll;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.json.JSONArray;
import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SpotifyBestSongWeeklyPoll {

	private final String clientID;
	private final String cliendIDsecret;

	private static SpotifyBestSongWeeklyPoll instance;

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
										SpotifyBestSongWeeklyPoll.open(command[1],
												command[2]);
									} catch(IllegalStateException e) {
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"clientID and secret cliendID already inserted!"));
									}

									return message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("clientID and secredt clientID inserted correctly!"));
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
								String clientID;
								String clientIDSecret;

								if(command[0].equalsIgnoreCase("!contest")) {

									if(instance == null)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(
														"Insert clientID and secret clientID first"));

									if(command.length <= 2)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage("Insert playlist id!"));

									playlist = command[1];
									clientID = instance.getClientID();
									clientIDSecret = instance.getCliendIDsecret();

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
										String accessToken = objectLogin.getString("access_token");
										System.out.println("Token: " + accessToken);

										URL url = new URL("https://api.spotify.com/v1/playlists/" + playlist +
												"/tracks?fields=items(added_by.id,track.name)");
										HttpURLConnection conn = (HttpURLConnection) url.openConnection();

										conn.setRequestProperty("Authorization", "Bearer " + accessToken);

										conn.setRequestProperty("Content-Type", "application/json");
										conn.setRequestMethod("GET");


										BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
										String output;
										JSONArray array;
										Map<String, String> mapR = new HashMap<>();

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

										StringBuilder messObj = new StringBuilder();

										mapR.forEach((id, name) -> {

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

												String nameUser = new JSONObject(responseUser.toString()
														.replace(":", ": ")).getString("display_name");

												messObj.append(nameUser)
														.append(":")
														.append(name)
														.append("\n");

											} catch(Exception e) {
												System.out.println(e.getMessage());
											}
										});

										final String messagefinal = messObj.toString();
										//TODO: better message and poll

										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage(messagefinal));

									} catch(IOException e) {
										System.out.println("IO " + e.getMessage());
									}

								}

								return Mono.empty();
							})
							.then();

					return mess.and(comm).and(cid);
				});
		client.block();

	}

}
