package it.emanuelemelini.spotifybestsongweeklypoll;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

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

									SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(token)
											.build();

									try {
										//TODO: resolve track name error
										Paging<PlaylistTrack> playtrack = spotifyApi.getPlaylistsItems(playlist)
												.fields("items(added_by.id,track.name)")
												.build()
												.execute();

										System.out.println(playtrack);
									} catch(Exception e) {
										System.out.println(e.getMessage());
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
