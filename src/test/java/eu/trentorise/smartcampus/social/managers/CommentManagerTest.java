package eu.trentorise.smartcampus.social.managers;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Comment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class CommentManagerTest {

	@Autowired
	CommentManager manager;

	private Comment comment = null;
	private Comment comment1, comment2, comment3, comment4;
	private static final String AUTHOR1 = "Pinco Pallino";
	private static final String ENTITYID1 = "1";
	private static final String ENTITYID2 = "2";
	private static final String ENTITYID_NE = "9999";
	private static final String COMMENT_ID_NE = "111122223333aaaabbbbcccc";
	private static final String COMMENTTEXT1 = "Comment 1";
	private static final String COMMENTTEXT2 = "Comment 2";
	private static final String COMMENTTEXT3 = "Comment 3";
	private static final String COMMENTTEXT4 = "Comment 4";

	@Before
	public void createComment() {
		comment = manager.create(COMMENTTEXT1, AUTHOR1, ENTITYID1);
		comment1 = manager.create(COMMENTTEXT2, AUTHOR1, ENTITYID1);
		comment2 = manager.create(COMMENTTEXT3, AUTHOR1, ENTITYID2);
		comment3 = manager.create(COMMENTTEXT4, AUTHOR1, ENTITYID1);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void createCommentVoidParams() {
		comment4 = manager.create(null, null, null);
	}

	@Test
	public void createCommentNoText() {
		comment4 = manager.create(null, AUTHOR1, ENTITYID2);
		Assert.assertTrue(manager.remove(comment4.getId()));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void createCommentNoEntityId() {
		comment4 = manager.create(COMMENTTEXT4, AUTHOR1, null);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void createCommentNoAuthor() {
		comment4 = manager.create(COMMENTTEXT4, null, ENTITYID1);
	}

	@Test
	public void readComment() {
		Comment commentReaded = manager.readComment(comment.getId());
		Assert.assertTrue(commentReaded.getAuthor().compareTo(AUTHOR1) == 0);

		commentReaded = manager.readComment(comment1.getId());
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT2) == 0);

		commentReaded = manager.readComment(comment2.getId());
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT3) == 0);

		commentReaded = manager.readComment(comment3.getId());
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT4) == 0);
	}

	@Test
	public void readCommentNoParam() {
		Comment commentReaded = manager.readComment(null);
		Assert.assertTrue(commentReaded == null);
	}

	@Test
	public void readCommentNotExist() {
		Comment commentReaded = manager.readComment(COMMENT_ID_NE);
		Assert.assertTrue(commentReaded == null);
	}

	@Test
	public void readCommentsByEntityId() {
		List<Comment> commentsReaded = manager.readCommentsByEntity(ENTITYID2,
				null);
		Assert.assertTrue(commentsReaded.size() == 1);
	}

	@Test
	public void readCommentsByEntityIdNoParams() {
		List<Comment> commentsReaded = manager.readCommentsByEntity(null, null);
		Assert.assertTrue(commentsReaded.isEmpty());
	}

	@Test
	public void readCommentsByEntityIdNotExist() {
		List<Comment> commentsReaded = manager.readCommentsByEntity(
				ENTITYID_NE, null);
		Assert.assertTrue(commentsReaded.isEmpty());
	}

	@Test
	public void readCommentsByEntityIdAndTextAndAuthor() {
		Comment commentReaded = manager.readCommentsByAutorAndTextAndEntity(
				AUTHOR1, COMMENTTEXT1, ENTITYID1);
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT1) == 0);
	}

	@Test
	public void deleteComment() {
		Comment commentReaded = manager.delete(comment.getId(),
				comment.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());

		commentReaded = manager.delete(comment1.getId(), comment1.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());

		commentReaded = manager.delete(comment2.getId(), comment2.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());

		commentReaded = manager.delete(comment3.getId(), comment3.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void deleteCommentNoCommentId() {
		manager.delete(null, comment.getAuthor());
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void deleteCommentNoAuthor() {
		manager.delete(comment.getId(), null);
	}

	@Test
	public void deleteCommentNotExists() {
		Comment commentReaded = manager.delete(COMMENT_ID_NE, AUTHOR1);
		Assert.assertTrue(commentReaded == null);
	}

	@Test
	public void removeByEntity() {
		manager.removeByEntity(ENTITYID1);

		Comment commentReaded = manager.readComment(comment2.getId());
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT3) == 0);

		commentReaded = manager.readComment(comment.getId());
		Assert.assertTrue(commentReaded == null);
	}

	@Test
	public void removeCommentNoParam() {
		Assert.assertTrue(manager.remove(null));
	}

	@Test
	public void removeCommentNotExist() {
		Assert.assertTrue(manager.remove(COMMENT_ID_NE));
	}

	@After
	public void removeComment() {
		manager.remove(comment.getId());
		manager.remove(comment1.getId());
		manager.remove(comment2.getId());
		manager.remove(comment3.getId());
	}

}
