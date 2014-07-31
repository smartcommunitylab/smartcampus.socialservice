package eu.trentorise.smartcampus.social.dataexporter;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.client.smartcampus.SCWebApiClient;
import it.unitn.disi.sweb.webapi.model.entity.Entity;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopic;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicContentType;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicSource;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicStatus;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicSubject;
import it.unitn.disi.sweb.webapi.model.smartcampus.social.Community;
import it.unitn.disi.sweb.webapi.model.smartcampus.social.UserGroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DataRetriever {
	private static final String HOST = "sweb.smartcampuslab.it";
	private static final int PORT = 8080;

	private static final String DB_URL = "jdbc:mysql://localhost/acprovider-test";
	private static final String DB_USER = "ac";
	private static final String DB_PWD = "ac";

	private static final String SOCIAL_DB_URL = "jdbc:mysql://localhost/socialdb-test";
	private static final String SOCIAL_DB_USER = "social";
	private static final String SOCIAL_DB_PWD = "social";

	private static final String PROFILE_DB_HOST = "localhost";
	private static final int PROFILE_DB_PORT = 27017;
	private static final String PROFILE_DB_NAME = "profiledb";

	private static final String DEFAULT_APPID = "smartcampus";

	private static final boolean CONVERT_USERS = false;
	private static final boolean CONVERT_GROUPS = false;
	private static final boolean CONVERT_GROUP_MEMBERS = false;
	private static final boolean CONVERT_COMMUNITIES = false;
	private static final boolean CONVERT_COMMUNITY_MEMBERS = false;
	private static final boolean CONVERT_ENTITIES = true;
	private static final boolean CONVERT_SHARING = true;

	private static SCWebApiClient client;
	private static Connection connUserDb;
	private static Connection connSocialDb;

	private static Map<String, String> userData = new HashMap<String, String>();
	private static Map<Long, List<String>> communityData = new HashMap<Long, List<String>>();

	private static void init() {
		// init
		client = SCWebApiClient.getInstance(Locale.ENGLISH, HOST, PORT);
		System.out.println(String.format("sweb client initialized"));
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connUserDb = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
			System.out.println("Connected to user db " + DB_URL);
		} catch (Exception e) {
			exitMsg(String.format("Exception connecting to user db %, %s",
					DB_URL, e.getMessage()));
		}

		try {
			connSocialDb = DriverManager.getConnection(SOCIAL_DB_URL,
					SOCIAL_DB_USER, SOCIAL_DB_PWD);
			System.out.println("Connected to social db " + SOCIAL_DB_URL);
		} catch (Exception e) {
			exitMsg(String.format("Exception connecting to social db %, %s",
					SOCIAL_DB_URL, e.getMessage()));
		}
	}

	private static List<User> getUsers() {
		List<User> userIds = new ArrayList<User>();

		try {
			java.sql.Statement stmt = connUserDb.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id,social_id FROM user");
			while (rs.next()) {
				long userId = rs.getLong("id");
				long userSocialId = rs.getLong("social_id");
				userIds.add(new User(userId, userSocialId));
				userData.put("" + userSocialId, "" + rs.getLong("id"));
				if (CONVERT_COMMUNITY_MEMBERS) {
					it.unitn.disi.sweb.webapi.model.smartcampus.social.User swebUser = client
							.readUser(userSocialId);
					if (swebUser != null
							&& swebUser.getKnownCommunityIds() != null
							&& !swebUser.getKnownCommunityIds().isEmpty()) {
						for (Long c : swebUser.getKnownCommunityIds()) {
							List<String> members = communityData.get(c);
							if (members == null) {
								members = new ArrayList<String>();
							}

							members.add("" + userId);
							communityData.put(c, members);
						}
					}
				}

			}
			System.out
					.println(String.format("Founded %s users", userIds.size()));
			return userIds;
		} catch (Exception e) {
			exitMsg(String.format(
					"Exception getting users from user db %s, %s", DB_URL,
					e.getMessage()));
			return null;
		}
	}

	private static void insertUsers(List<User> users) {
		try {
			java.sql.PreparedStatement stmt = connSocialDb
					.prepareStatement("INSERT INTO social_user VALUES (?)");

			for (User u : users) {
				stmt.setString(1, u.getUserId().toString());
				stmt.addBatch();
			}

			boolean failure = false;
			int[] result = stmt.executeBatch();
			for (int r : result) {
				failure = failure || r < 0;
			}

			if (failure) {
				exitMsg(String.format(
						"Exception inserting users in social db %s",
						SOCIAL_DB_URL));
			}
		} catch (Exception e) {
			exitMsg(String.format("Exception connecting to user db %s, %s",
					DB_URL, e.getMessage()));
		}
	}

	private static void insertGroups(List<User> users) {
		java.sql.PreparedStatement stmt = null;
		try {
			stmt = connSocialDb
					.prepareStatement("INSERT INTO social_group VALUES (?,?,?,?,?)");

			for (User u : users) {
				it.unitn.disi.sweb.webapi.model.smartcampus.social.User swebUser;
				try {
					swebUser = client.readUser(u.getSocialId());
					if (swebUser != null) {
						Set<Long> gids = swebUser.getUserGroupIds();
						System.out.println(String.format(
								"User %s has %s social groups", u.getUserId(),
								gids.size()));
						stmt.clearBatch();
						for (Long gid : gids) {
							// group details
							UserGroup g = client.readUserGroup(gid);
							stmt.setLong(1, gid);
							stmt.setLong(2, -1l); // creation time
							stmt.setLong(3, -1l); // last update
							stmt.setString(4, g.getName());
							stmt.setString(5, "" + u.getUserId());
							stmt.addBatch();
						}
						stmt.executeBatch();
						System.out.println(String.format(
								"Converted groups for user %s", u.getUserId()));
					} else {
						System.out.println(String.format(
								"User %s not exist in SWEB", u.getSocialId()));
					}
				} catch (WebApiException e) {
					System.err.println(String.format(
							"Exception converting groups for user %s, %s",
							u.getUserId(), e.getMessage()));
				} catch (SQLException e) {
					System.err.println(String.format(
							"Exception converting groups for user %s, %s",
							u.getUserId(), e.getMessage()));
				}
			}
		} catch (SQLException e1) {
			System.err.println(String.format(
					"Exception creating statement to convert groups, %s",
					e1.getMessage()));
		}
	}

	private static void insertGroupMembers(List<User> users) {
		java.sql.PreparedStatement stmt = null;
		try {
			stmt = connSocialDb
					.prepareStatement("INSERT INTO group_members VALUES (?,?)");

			for (User u : users) {
				it.unitn.disi.sweb.webapi.model.smartcampus.social.User swebUser;
				try {
					swebUser = client.readUser(u.getSocialId());
					if (swebUser != null) {
						Set<Long> gids = swebUser.getUserGroupIds();
						System.out.println(String.format(
								"User %s has %s social groups", u.getUserId(),
								gids.size()));
						stmt.clearBatch();
						for (Long gid : gids) {
							Set<Long> members = client.readUserGroup(gid)
									.getUserIds();
							for (Long m : members) {
								String mId = userData.get(m.toString());
								if (mId != null) {
									stmt.setLong(1, gid);
									stmt.setString(2, mId);
									stmt.addBatch();
								} else {
									System.err
											.println(String
													.format("Member %s group %s of user %s not exist",
															m, gid,
															u.getUserId()));
								}
							}
						}
						stmt.executeBatch();
						if (gids.size() > 0)
							System.out.println(String.format(
									"Converted group members for user %s",
									u.getUserId()));
					} else {
						System.out.println(String.format(
								"User %s not exist in SWEB", u.getSocialId()));
					}
				} catch (WebApiException e) {
					System.err
							.println(String
									.format("Exception converting group members for user %s, %s",
											u.getUserId(), e.getMessage()));
				} catch (SQLException e) {
					System.err
							.println(String
									.format("Exception converting group members for user %s, %s",
											u.getUserId(), e.getMessage()));
				}
			}
		} catch (SQLException e1) {
			System.err
					.println(String
							.format("Exception creating statement to convert group members, %s",
									e1.getMessage()));
		}
	}

	private static void insertCommunity() {
		java.sql.PreparedStatement stmt = null;
		try {
			stmt = connSocialDb
					.prepareStatement("INSERT INTO social_community VALUES (?,?,?,?,?)");
			List<Community> communities = client.readCommunities();
			System.out.println(String.format(
					"There are %s communities in system", communities.size()));
			for (Community c : communities) {
				stmt.clearBatch();
				stmt.setLong(1, c.getId());
				stmt.setString(2, DEFAULT_APPID);
				stmt.setLong(3, -1l);
				stmt.setLong(4, -1l);
				stmt.setString(5, c.getName());
				stmt.addBatch();
			}
			stmt.executeBatch();
			System.out.println(String.format("Converted communities"));
		} catch (SQLException e1) {
			System.err.println(String.format(
					"Exception creating statement to convert communities, %s",
					e1.getMessage()));
		} catch (WebApiException e) {
			System.err.println(String.format(
					"Exception sweb communication to read communities, %s",
					e.getMessage()));
		}

	}

	private static void insertCommunityMembers() {
		java.sql.PreparedStatement stmt = null;
		try {
			stmt = connSocialDb
					.prepareStatement("INSERT INTO social_community_members VALUES (?,?)");
			for (Entry<Long, List<String>> entry : communityData.entrySet()) {
				System.out.println(String.format("Community %s has %s users",
						entry.getKey(), entry.getValue().size()));
				for (String m : entry.getValue()) {
					stmt.setLong(1, entry.getKey());
					stmt.setString(2, m);
					stmt.addBatch();
				}
			}
			stmt.executeBatch();
			System.out.println(String.format("Converted community members"));
		} catch (SQLException e1) {
			System.err
					.println(String
							.format("Exception creating statement to convert community members, %s",
									e1.getMessage()));
		}

	}

	private static void insertEntity(List<User> users) {
		java.sql.PreparedStatement stmt = null;
		try {
			stmt = connSocialDb
					.prepareStatement("INSERT INTO social_community_members VALUES (?,?)");

			for (User u : users) {
				LiveTopic filter = new LiveTopic();
				LiveTopicSource filterSource = new LiveTopicSource();
				long ownerId = u.getSocialId();
				filter.setActorId(ownerId); // <-- mandatory

				filterSource.setAllCommunities(true);
				filterSource.setAllUsers(true);
				filterSource.setAllKnownCommunities(true);
				filterSource.setAllKnownUsers(true);
				filterSource
						.setGroupIds(new HashSet<Long>(Arrays.asList(221l)));
				filter.setSource(filterSource);

				LiveTopicSubject subject = new LiveTopicSubject();
				subject.setAllSubjects(true); // <-- important
				// filter.setSubjects(Collections.singleton(subject));

				LiveTopicContentType type = new LiveTopicContentType();
				type.setAllTypes(true);
				// type.setEntityTypeIds(new HashSet<Long>());
				filter.setType(type); // <-- mandatory
				filter.setStatus(LiveTopicStatus.ACTIVE); // <--
				// mandatory

				List<Long> sharedIds;
				try {
					sharedIds = client.computeEntitiesForLiveTopic(filter,
							null, null);
					System.out.println(String.format("User %s has %s entities",
							u.getUserId(), sharedIds.size()));
					if (!sharedIds.isEmpty()) {
						List<Entity> results = client.readEntities(sharedIds,
								null);
						for (Entity e : results) {
							System.out.println(e.getOwnerId()
									+ " - "
									+ e.getId()
									+ " - "
									+ e.getAttributeByName("name").getValues()
											.get(0));
						}
					}
				} catch (WebApiException e1) {
					System.err.println(String.format(
							"Exception getting entities of user %s, %s",
							u.getUserId(), e1.getMessage()));
				}
			}

		} catch (SQLException e1) {
			System.err
					.println(String
							.format("Exception creating statement to convert community members, %s",
									e1.getMessage()));
		}
	}

	private static void insertEntitySharing() {

	}

	public static final void main(String[] a) {
		System.out.println(String.format("SWEB HOST: %s", HOST));
		System.out.println(String.format("SWEB PORT: %s", PORT));
		System.out.println(String.format("DB URL: %s", DB_URL));
		System.out.println(String
				.format("PROFILE DB HOST: %s", PROFILE_DB_HOST));
		System.out.println(String
				.format("PROFILE DB PORT: %s", PROFILE_DB_PORT));
		System.out.println(String
				.format("PROFILE DB NAME: %s", PROFILE_DB_NAME));

		init();

		List<User> users = getUsers();

		if (CONVERT_USERS) {
			insertUsers(users);
		}
		if (CONVERT_GROUPS) {
			insertGroups(users);
		}

		if (CONVERT_GROUP_MEMBERS) {
			insertGroupMembers(users);
		}

		if (CONVERT_COMMUNITIES) {
			insertCommunity();
		}

		if (CONVERT_COMMUNITY_MEMBERS) {
			insertCommunityMembers();
		}

		if (CONVERT_ENTITIES) {
			insertEntity(users);
		}
	}

	// public static final void main(String[] a) {
	// System.out.println(String.format("SWEB HOST: %s", HOST));
	// System.out.println(String.format("SWEB PORT: %s", PORT));
	// System.out
	// .println(String.format("DESTINATION FOLDER: %s", DESTINATION));
	// System.out.println(String.format("DB URL: %s", DB_URL));
	// System.out.println(String
	// .format("PROFILE DB HOST: %s", PROFILE_DB_HOST));
	// System.out.println(String
	// .format("PROFILE DB PORT: %s", PROFILE_DB_PORT));
	// System.out.println(String
	// .format("PROFILE DB NAME: %s", PROFILE_DB_NAME));
	//
	// List<User> userIds = new ArrayList<User>();
	//
	// // collect all userId
	// try {
	// Class.forName("com.mysql.jdbc.Driver").newInstance();
	// Connection conn = DriverManager.getConnection(DB_URL, DB_USER,
	// DB_PWD);
	// System.out.println("Connected to db " + DB_URL);
	// java.sql.Statement stmt = conn.createStatement();
	// ResultSet rs = stmt.executeQuery("SELECT id,social_id FROM user");
	// while (rs.next()) {
	// userIds.add(new User(rs.getLong("id"), rs.getLong("social_id")));
	// }
	// System.out
	// .println(String.format("Founded %s users", userIds.size()));
	// if (userIds.size() == 0) {
	// System.exit(0);
	// } else {
	// for (User u : userIds) {
	// try {
	//
	// LiveTopic filter = new LiveTopic();
	// LiveTopicSource filterSource = new LiveTopicSource();
	// long ownerId = u.getSocialId();
	// if (ownerId > 0) {
	// filter.setActorId(ownerId); // <-- mandatory
	// }
	//
	// filter.setSource(filterSource);
	//
	// LiveTopicSubject subject = new LiveTopicSubject();
	// subject.setAllSubjects(true); // <-- important
	// filter.setSubjects(Collections.singleton(subject));
	//
	// LiveTopicContentType type = new LiveTopicContentType();
	// type.setAllTypes(true);
	// type.setEntityTypeIds(new HashSet<Long>());
	// filter.setType(type); // <-- mandatory
	// filter.setStatus(LiveTopicStatus.ACTIVE); // <--
	// // mandatory
	//
	// List<Long> sharedIds = client
	// .computeEntitiesForLiveTopic(filter, null, null);
	// if (!sharedIds.isEmpty()) {
	// // filter to retrieve only name attribute of entity
	// // Filter f = new Filter(null, new HashSet<String>(
	// // Arrays.asList("name")), false, false, 0, null,
	// // null,
	// // null, null);
	// List<eu.trentorise.smartcampus.social.model.Entity> shared = new
	// ArrayList<eu.trentorise.smartcampus.social.model.Entity>(
	// sharedIds.size());
	// List<Entity> results = client.readEntities(
	// sharedIds, null);
	// ShareVisibility vis = null;
	// for (Entity e : results) {
	// if ("community".equals(e.getEtype().getName()))
	// continue;
	// if ("person".equals(e.getEtype().getName()))
	// continue;
	// if (addVisibility)
	// vis = getAssignments(ownerId, e.getId());
	// shared.add(socialConverter.toEntity(e, addUser,
	// vis));
	// }
	// return shared;
	// } else {
	// return Collections.emptyList();
	// }
	// } catch (WebApiException e) {
	// logger.error("Exception getting user shared content", e);
	// return Collections.emptyList();
	// }
	// }
	// }
	// } catch (URIException e) {
	// System.err.println(String.format(
	// "Exception open sweb stream fileId %s, %s", pictureUrl,
	// e.getMessage()));
	// } catch (WebApiException e) {
	// System.err.println(String.format(
	// "Exception open sweb stream fileId %s, %s", pictureUrl,
	// e.getMessage()));
	// } catch (IOException e) {
	// System.err.println(String.format(
	// "Exception writing on destination fileId %s, %s",
	// pictureUrl, e.getMessage()));
	// }
	// }

	private static void exitMsg(String msg) {
		System.err.println(msg);
		System.exit(-1);
	}

}

class User {
	private Long userId;
	private Long socialId;

	public User() {
	}

	public User(Long userId, Long socialId) {
		this.userId = userId;
		this.socialId = socialId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getSocialId() {
		return socialId;
	}

	public void setSocialId(Long socialId) {
		this.socialId = socialId;
	}
}
