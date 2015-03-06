/*The MIT License (MIT)

Copyright (c) 2014 Marko Knol

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.*/
package knol.marko.voting;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connector {

	private final static String[] strings = ConnectorManager.STRINGS;

	private static Connection connection;

	public static Connection receiveConnection() {
		try {
			return Connector.connection = DriverManager.getConnection(
					"jdbc:mysql://" + strings[0] + "/" + strings[1],
					strings[2], strings[3]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static void createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection("jdbc:mysql://"
					+ strings[0] + "/" + strings[1], strings[2], strings[3]);
			System.out.println("Connection established.");
		} catch (Exception e) {
			System.out.println("Connection not established.");
			e.printStackTrace();
		}
	}
}
