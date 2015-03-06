package com.citelic.networking.codec.encode;

import com.citelic.networking.Session;

public abstract class Encoder {

	protected Session session;

	public Encoder(Session session) {
		this.session = session;
	}

}
