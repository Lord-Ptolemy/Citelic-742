package com.citelic.networking;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.citelic.game.entity.player.Player;
import com.citelic.networking.codec.decode.ClientPacketsDecoder;
import com.citelic.networking.codec.decode.Decoder;
import com.citelic.networking.codec.decode.GrabPacketsDecoder;
import com.citelic.networking.codec.decode.LoginPacketsDecoder;
import com.citelic.networking.codec.decode.WorldPacketsDecoder;
import com.citelic.networking.codec.encode.Encoder;
import com.citelic.networking.codec.encode.GrabPacketsEncoder;
import com.citelic.networking.codec.encode.LoginPacketsEncoder;
import com.citelic.networking.codec.encode.WorldPacketsEncoder;
import com.citelic.networking.streaming.OutputStream;
import com.citelic.utility.IPBanL;

public class Session {

	private Channel channel;
	private Decoder decoder;
	private Encoder encoder;

	public Session(Channel channel) {
		this.channel = channel;
		if (IPBanL.isBanned(getIP())) {
			channel.disconnect();
			return;
		}
		setDecoder(0);
	}

	public final Channel getChannel() {
		return channel;
	}

	public final Decoder getDecoder() {
		return decoder;
	}

	public final Encoder getEncoder() {
		return encoder;
	}

	public GrabPacketsEncoder getGrabPackets() {
		return (GrabPacketsEncoder) encoder;
	}

	public GrabPacketsDecoder getGrabPacketsDecoder() {
		return (GrabPacketsDecoder) decoder;
	}

	public String getIP() {
		return channel == null ? "" : channel.getRemoteAddress().toString()
				.split(":")[0].replace("/", "");

	}

	public String getLocalAddress() {
		return channel.getLocalAddress().toString();
	}

	public LoginPacketsEncoder getLoginPackets() {
		return (LoginPacketsEncoder) encoder;
	}

	public WorldPacketsEncoder getWorldPackets() {
		return (WorldPacketsEncoder) encoder;
	}

	public final void setDecoder(int stage) {
		setDecoder(stage, null);
	}

	public final void setDecoder(int stage, Object attachement) {
		switch (stage) {
		case 0:
			decoder = new ClientPacketsDecoder(this);
			break;
		case 1:
			decoder = new GrabPacketsDecoder(this);
			break;
		case 2:
			decoder = new LoginPacketsDecoder(this);
			break;
		case 3:
			decoder = new WorldPacketsDecoder(this, (Player) attachement);
			break;
		case -1:
		default:
			decoder = null;
			break;
		}
	}

	public final void setEncoder(int stage) {
		setEncoder(stage, null);
	}

	public final void setEncoder(int stage, Object attachement) {
		switch (stage) {
		case 0:
			encoder = new GrabPacketsEncoder(this);
			break;
		case 1:
			encoder = new LoginPacketsEncoder(this);
			break;
		case 2:
			encoder = new WorldPacketsEncoder(this, (Player) attachement);
			break;
		case -1:
		default:
			encoder = null;
			break;
		}
	}

	public final ChannelFuture write(ChannelBuffer outStream) {
		if (outStream == null)
			return null;
		if (channel.isConnected()) {
			synchronized (channel) {
				return channel.write(outStream);
			}
		}
		return null;
	}

	public final ChannelFuture write(OutputStream outStream) {
		if (channel.isConnected()) {
			ChannelBuffer buffer = ChannelBuffers.copiedBuffer(
					outStream.getBuffer(), 0, outStream.getOffset());
			synchronized (channel) {
				return channel.write(buffer);
			}
		}
		return null;
	}
}