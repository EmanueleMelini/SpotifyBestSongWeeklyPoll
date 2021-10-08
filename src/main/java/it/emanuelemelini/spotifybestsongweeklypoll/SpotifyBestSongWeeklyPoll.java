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

								String playlist;
								String clientID;
								String clientIDSecret;

								if(command[0].equalsIgnoreCase("!contest")) {

									if(command.length <= 2)
										return message.getChannel()
												.flatMap(messageChannel -> messageChannel.createMessage("Insert playlist id!"));

									playlist = command[1];
									clientID = command[2];
									clientIDSecret = command[3];

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

										/*


										String params = "grant_type=refresh_token&refresh_token=" + token;
										byte[] postData = params.getBytes(StandardCharsets.UTF_8);
										String clientsIDs = clientID + ":" + clientIDSecret;
										byte[] access = clientsIDs.getBytes(StandardCharsets.UTF_8);
										int postDataLenght = postData.length;
										URL urlRefresh = new URL("https://accounts.spotify.com/api/token");
										HttpURLConnection connRefresh = (HttpURLConnection) urlRefresh.openConnection();
										connRefresh.setDoOutput(true);
										connRefresh.setRequestMethod("POST");
										connRefresh.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
										connRefresh.setRequestProperty("Authorization", "Basic " +
												java.util.Base64.getEncoder().encodeToString(access));
										connRefresh.setRequestProperty("charset", "utf-8");
										connRefresh.setRequestProperty("Content-Lenght", Integer.toString(postDataLenght));
										try(DataOutputStream wr = new DataOutputStream(connRefresh.getOutputStream())) {
											wr.write(postData);
										}
										BufferedReader inRefresh = new BufferedReader(new InputStreamReader(connRefresh.getInputStream()));
										String outputRefresh;

										StringBuffer responseRefresh = new StringBuffer();
										while((outputRefresh = inRefresh.readLine()) != null) {
											responseRefresh.append(outputRefresh);
										}
										System.out.println("Refresh: " + responseRefresh);
										inRefresh.close();
										*/


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

												URL urlUser = new URL(
														"https://api.spotify.com/v1/users/" + id);
												HttpURLConnection connectionUser = (HttpURLConnection) urlUser.openConnection();
												connectionUser.setRequestProperty("Content-Type", "application/json");
												connectionUser.setRequestProperty("Authorization",
														"Bearer " + accessToken);
												connectionUser.setRequestMethod("GET");
												BufferedReader inUser = new BufferedReader(new InputStreamReader(
														connectionUser.getInputStream()));

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

											}catch(Exception e) {
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

					return mess.and(comm);
				});
		client.block();

	}

}
