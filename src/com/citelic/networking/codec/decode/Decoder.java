package com.citelic.networking.codec.decode;

import com.citelic.networking.Session;
import com.citelic.networking.streaming.InputStream;

public abstract class Decoder {

	protected Session session;

	public Decoder(Session session) {
		this.session = session;
	}

	public abstract void decode(Session session, InputStream stream);

}
