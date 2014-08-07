package eu.trentorise.smartcampus.social.dataexporter;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.client.smartcampus.SCWebApiClient;
import it.unitn.disi.sweb.webapi.model.entity.Entity;
import it.unitn.disi.sweb.webapi.model.entity.EntityBase;
import it.unitn.disi.sweb.webapi.model.entity.Value;
import it.unitn.disi.sweb.webapi.model.smartcampus.ac.Operation;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopic;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicContentType;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicSource;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicStatus;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicSubject;
import it.unitn.disi.sweb.webapi.model.smartcampus.social.Community;
import it.unitn.disi.sweb.webapi.model.smartcampus.social.UserGroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mysql.jdbc.Statement;

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
	private static final String DEFAULT_NAMESPACE = "sweb";

	private static final boolean CONVERT_USERS = true;
	private static final boolean CONVERT_GROUPS = true;
	private static final boolean CONVERT_GROUP_MEMBERS = true;
	private static final boolean CONVERT_COMMUNITIES = true;
	private static final boolean CONVERT_COMMUNITY_MEMBERS = true;
	private static final boolean CONVERT_ENTITIES = true;

	private static SCWebApiClient client;
	private static Connection connUserDb;
	private static Connection connSocialDb;

	private static Map<String, String> userData = new HashMap<String, String>();
	private static Map<Long, List<String>> communityData = new HashMap<Long, List<String>>();
	private static Map<String, Long> eTypes = new HashMap<String, Long>();

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

		readEtypes();

	}

	private static void readEtypes() {
		java.sql.Statement stmt;
		try {
			stmt = connSocialDb.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT id,name FROM social_entity_type");
			while (rs.next()) {
				eTypes.put(rs.getString("name"), rs.getLong("id"));
			}

			System.out.println("ETypes loaded");
		} catch (SQLException e) {
			e.printStackTrace();
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

	private static Long getETypeRel(String name) {
		java.sql.PreparedStatement stmt = null;
		Long id = null;
		try {
			id = eTypes.get(name);
			if (id == null) {
				stmt = connSocialDb.prepareStatement(
						"INSERT INTO social_entity_type (name) VALUES (?)",
						Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, name);
				stmt.execute();
				ResultSet keys = stmt.getGeneratedKeys();
				keys.next();
				id = keys.getLong(1);
				eTypes.put(name, id);
				System.out.println(String.format("Added type %s, id: %s", name,
						id));
			}
		} catch (SQLException e1) {
			System.err
					.println(String
							.format("Exception creating statement to convert community members, %s",
									e1.getMessage()));
		}

		return id;
	}

	private static void insertCommunityEntity() {
		try {
			List<Community> comm = Collections.emptyList();
			comm = client.readCommunities();
			PreparedStatement stmt = connSocialDb
					.prepareStatement("INSERT INTO social_entity (uri,creation_time,last_modified_time,external_uri,local_id,name,namespace,community_owner,type,public_shared, rating, total_voters ) VALUES (?,?,?,?,?,?,?,?,?,false,0,0)");
			PreparedStatement userSharingStmt = connSocialDb
					.prepareStatement("INSERT INTO user_shared_with VALUES(?,?)");
			PreparedStatement groupSharingStmt = connSocialDb
					.prepareStatement("INSERT INTO group_shared_with VALUES(?,?)");
			PreparedStatement communitySharingStmt = connSocialDb
					.prepareStatement("INSERT INTO community_shared_with VALUES(?,?)");
			stmt.clearBatch();
			userSharingStmt.clearBatch();
			groupSharingStmt.clearBatch();
			communitySharingStmt.clearBatch();

			for (Community c : comm) {
				EntityBase eb = client.readEntityBase(c.getEntityBaseId());
				List<Entity> sharedEntities = client.readEntities(null,
						eb.getLabel(), null);
				System.out.println(String.format(
						"Community %s has %s entities", c.getId(),
						sharedEntities.size()));

				if (!sharedEntities.isEmpty()) {
					for (Entity e : sharedEntities) {
						if ("community".equals(e.getEtype().getName()))
							continue;
						if ("person".equals(e.getEtype().getName()))
							continue;

						stmt.setString(1, e.getId().toString());
						stmt.setLong(2, e.getCreationTime());
						stmt.setLong(3, e.getCreationTime());
						stmt.setString(4, e.getId().toString());
						stmt.setString(5, e.getId().toString());
						Value v = e.getAttributeByName("name").getFirstValue();
						stmt.setString(6, v == null ? "" : v.getStringValue());
						stmt.setString(7, DEFAULT_NAMESPACE);
						stmt.setLong(8, c.getId());
						if (e.getEtype() != null) {
							stmt.setLong(9, getETypeRel(e.getEtype().getName()));
						}
						stmt.addBatch();
						LiveTopicSource visibility = client.readAssignments(
								e.getId(), Operation.READ, c.getId());

						if (visibility.isAllUsers()) {
							stmt.addBatch(String
									.format("UPDATE social_entity SET public_shared=true WHERE uri='%s'",
											e.getId().toString()));
						}

						for (Long uid : visibility.getUserIds()) {
							userSharingStmt.setString(1, e.getId().toString());
							userSharingStmt.setString(2,
									userData.get(uid.toString()));
							userSharingStmt.addBatch();
						}

						for (Long gid : visibility.getGroupIds()) {
							groupSharingStmt.setString(1, e.getId().toString());
							groupSharingStmt.setLong(2, gid);
							groupSharingStmt.addBatch();
						}

						for (Long cid : visibility.getCommunityIds()) {
							communitySharingStmt.setString(1, e.getId()
									.toString());
							communitySharingStmt.setLong(2, cid);
							communitySharingStmt.addBatch();

						}
					}
					stmt.executeBatch();
					userSharingStmt.executeBatch();
					groupSharingStmt.executeBatch();
					communitySharingStmt.executeBatch();
					System.out.println(String.format("Entities converted"));
				}
			}
		} catch (WebApiException e) {
			System.err.println("Sweb exception converting community entities");
		} catch (SQLException e) {
			System.err
					.println("SQL exception converting community entities in new social schema");
		}
	}

	private static void insertEntity(List<User> users) {
		try {
			PreparedStatement stmt = null;
			PreparedStatement userSharingStmt = connSocialDb
					.prepareStatement("INSERT INTO user_shared_with VALUES(?,?)");
			PreparedStatement groupSharingStmt = connSocialDb
					.prepareStatement("INSERT INTO group_shared_with VALUES(?,?)");
			PreparedStatement communitySharingStmt = connSocialDb
					.prepareStatement("INSERT INTO community_shared_with VALUES(?,?)");

			stmt = connSocialDb
					.prepareStatement("INSERT INTO social_entity (uri,creation_time,last_modified_time,external_uri,local_id,name,namespace,owner,type,public_shared, rating, total_voters ) VALUES (?,?,?,?,?,?,?,?,?,false,0,0)");

			for (User u : users) {
				LiveTopic filter = new LiveTopic();
				LiveTopicSource filterSource = new LiveTopicSource();
				long ownerId = u.getSocialId();
				filter.setActorId(ownerId); // <-- mandatory

				filterSource.setUserIds(Collections.singleton(ownerId));
				filter.setSource(filterSource);

				LiveTopicSubject subject = new LiveTopicSubject();
				subject.setAllSubjects(true); // <-- important
				filter.setSubjects(Collections.singleton(subject));

				LiveTopicContentType type = new LiveTopicContentType();
				type.setAllTypes(true);
				type.setEntityTypeIds(new HashSet<Long>());
				filter.setType(type); // <-- mandatory
				filter.setStatus(LiveTopicStatus.ACTIVE); // <--
				// mandatory

				// user entities

				List<Long> sharedIds;
				try {
					sharedIds = client.computeEntitiesForLiveTopic(filter,
							null, null);
					System.out.println(String.format(
							"User %s (social %s) has %s entities",
							u.getUserId(), u.getSocialId(), sharedIds.size()));
					stmt.clearBatch();
					if (!sharedIds.isEmpty()) {
						List<Entity> results = client.readEntities(sharedIds,
								null);
						for (Entity e : results) {
							if ("community".equals(e.getEtype().getName()))
								continue;
							if ("person".equals(e.getEtype().getName()))
								continue;

							stmt.setString(1, e.getId().toString());
							stmt.setLong(2, e.getCreationTime());
							stmt.setLong(3, e.getCreationTime());
							stmt.setString(4, e.getId().toString());
							stmt.setString(5, e.getId().toString());
							Value v = e.getAttributeByName("name")
									.getFirstValue();
							stmt.setString(6,
									v == null ? "" : v.getStringValue());
							stmt.setString(7, DEFAULT_NAMESPACE);
							stmt.setString(8, u.getUserId().toString());
							if (e.getEtype() != null) {
								stmt.setLong(9, getETypeRel(e.getEtype()
										.getName()));
							}
							stmt.addBatch();
							LiveTopicSource visibility = client
									.readAssignments(e.getId(), Operation.READ,
											ownerId);

							if (visibility.isAllUsers()) {
								stmt.addBatch(String
										.format("UPDATE social_entity SET public_shared=true WHERE uri='%s'",
												e.getId().toString()));
							}

							for (Long uid : visibility.getUserIds()) {
								userSharingStmt.setString(1, e.getId()
										.toString());
								userSharingStmt.setString(2,
										userData.get(uid.toString()));
								userSharingStmt.addBatch();
							}

							for (Long gid : visibility.getGroupIds()) {
								groupSharingStmt.setString(1, e.getId()
										.toString());
								groupSharingStmt.setLong(2, gid);
								groupSharingStmt.addBatch();
							}

							for (Long cid : visibility.getCommunityIds()) {
								communitySharingStmt.setString(1, e.getId()
										.toString());
								communitySharingStmt.setLong(2, cid);
								communitySharingStmt.addBatch();

							}

							/**
							 * use for debug scope
							 */

							// System.out.println(String.format(
							// "%s,%s,%s,%s,%s,%s,%s", visibility
							// .isAllUsers(), visibility
							// .isAllKnownUsers(), visibility
							// .isAllKnownCommunities(),
							// visibility.isAllCommunities(), visibility
							// .getGroupIds().toString(),
							// visibility.getUserIds().toString(),
							// visibility.getCommunityIds().toString()));
						}
					}
					stmt.executeBatch();
					userSharingStmt.executeBatch();
					groupSharingStmt.executeBatch();
					communitySharingStmt.executeBatch();
					System.out.println(String.format("Entities converted"));
				} catch (WebApiException e1) {
					System.err.println(String.format(
							"Exception getting entities of user %s, %s",
							u.getUserId(), e1.getMessage()));
				}
			}
		} catch (SQLException e1) {
			System.err
					.println(String
							.format("Exception creating statement to convert sweb entities, %s",
									e1.getMessage()));
		}
	}

	public static final void main(String[] a) {
		System.out.println(String.format("SWEB HOST: %s", HOST));
		System.out.println(String.format("SWEB PORT: %s", PORT));
		System.out.println(String.format("DB URL: %s", DB_URL));
		System.out.println(String.format("DB USER: %s", DB_USER));
		System.out.println(String
				.format("PROFILE DB HOST: %s", PROFILE_DB_HOST));
		System.out.println(String
				.format("PROFILE DB PORT: %s", PROFILE_DB_PORT));
		System.out.println(String
				.format("PROFILE DB NAME: %s", PROFILE_DB_NAME));
		System.out.println(String.format("SOCIAL DB URL: %s", SOCIAL_DB_URL));
		System.out.println(String.format("SOCIAL DB USER: %s", SOCIAL_DB_USER));
		System.out.println(String.format("DEFAULT APPID: %s", DEFAULT_APPID));
		System.out.println(String.format("DEFAULT NAMESPACE: %s",
				DEFAULT_NAMESPACE));
		System.out.println(String.format("CONVERT USERS: %s", CONVERT_USERS));
		System.out.println(String.format("CONVERT GROUPS: %s", CONVERT_GROUPS));
		System.out.println(String.format("CONVERT GROUP MEMBERS: %s",
				CONVERT_GROUP_MEMBERS));
		System.out.println(String.format("CONVERT COMMUNITIES: %s",
				CONVERT_COMMUNITIES));
		System.out.println(String.format("CONVERT COMMUNITY MEMBERS: %s",
				CONVERT_COMMUNITY_MEMBERS));
		System.out.println(String.format("CONVERT ENTITIES: %s",
				CONVERT_ENTITIES));

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
		insertCommunityEntity();

		System.out.println("DONE!");
	}

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
