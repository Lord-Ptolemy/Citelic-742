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
import java.sql.ResultSet;
import java.sql.Statement;

public class Authenticator {

	public static Reward authenticateVote(String username) {
		username = username.replace(" ", "_");
		try {
			Connection connection = Connector.receiveConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT `rewardid` FROM `has_voted` WHERE `username` = '"
							+ username + "' AND `given` = '0'");
			if (result.next()) {
				Statement progressVote = connection.createStatement();
				progressVote
						.executeUpdate("UPDATE `has_voted` SET `given` = '1' WHERE `username` = '"
								+ username + "'");
				return new Reward(username, result.getInt("rewardid"));
			}
			result.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
