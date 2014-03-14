package eu.trentorise.smartcampus.social.managers;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.engine.beans.Comment;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.model.SocialComment;
import eu.trentorise.smartcampus.social.engine.repo.mongo.CommentRepository;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

@Component
@Transactional
public class SocialCommentManager {
	
	private static final Logger logger = Logger.getLogger(SocialCommentManager.class);
	private static final String deleteComment = "User '%s' has delete the comment.";
	
	private static final int DEF_PAGE_NUMBER = 0;
	private static final int DEF_PAGE_SIZE = 10000;
	
	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String MONGO_DB = "commentDb";
	private static final String MONGO_COLL = "comment";
	
	@Autowired
	CommentRepository commentRepository;
	
	public Comment create(String text, String user, String entityId){
		if(!StringUtils.hasLength(text)){
			throw new IllegalArgumentException("param 'text' should be valid");
		}
		if(!StringUtils.hasLength(user)){
			throw new IllegalArgumentException("param 'user' should be valid");
		}
		if(!StringUtils.hasLength(entityId)){
			throw new IllegalArgumentException("param 'entityId' should be valid");
		}
		
		//if(commentRepository.findByAuthorIgnoreCaseAndEntityId(user, entityId) != null){
		//	throw new IllegalArgumentException(String.format("comment for entity '%s' already inserted by user '%s'", entityId, user));
		//}
		
		SocialComment comment = new SocialComment(text, user, entityId);
		return commentRepository.save(comment).toComment();
	}
	
	public Comment readComment(String commentId){
		if(!StringUtils.hasLength(commentId)){
			logger.error("param 'commentId' is not valid");
			//throw new IllegalArgumentException("param 'commentId' should be valid");
			return null;
		}
		SocialComment result = commentRepository.findById(commentId);
		return result != null ? result.toComment() : null;
	}
	
	public String readComment_JSON(String commentId){
		String result = "";
		MongoClient mongoClient = null;
        try {
			mongoClient = new MongoClient(MONGO_HOST , MONGO_PORT);
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		}  catch (MongoException ex){
			logger.error(ex.getMessage());
		}
		DB db = mongoClient.getDB(MONGO_DB);
        DBCollection coll = db.getCollection(MONGO_COLL);
        
        // query with entityId
        BasicDBObject query = new BasicDBObject();
        
     	query.put("_id", new ObjectId(commentId));
     	result = coll.findOne(query).toString();
        
     	return result;
	}
	
	// Used in performance test
	public Comment readCommentsByAutorAndTextAndEntity(String author, String text, String entityId){
		SocialComment result = null;
		result = commentRepository.findByAuthorIgnoreCaseAndTextIgnoreCaseAndEntityId(author, text, entityId);
		return result != null ? result.toComment() : null;
	}
	
	// Used in performance test
	public String readCommentsByAutorAndTextAndEntity_JSON(String author, String text, String entityId){
		String result = "";
		MongoClient mongoClient = null;
        try {
			mongoClient = new MongoClient(MONGO_HOST , MONGO_PORT);
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		}  catch (MongoException ex){
			logger.error(ex.getMessage());
		}
		DB db = mongoClient.getDB(MONGO_DB);
        DBCollection coll = db.getCollection(MONGO_COLL);
        
        // query with entityId
        BasicDBObject query = new BasicDBObject("author", author).append("commentText", text).append("entityId", entityId);
     	result = coll.findOne(query).toString();
        
     	return result;
	}
	
	// Used in performance test
	public List<Comment> readCommentsByEntity(String entityId, Limit limit){
		if(!StringUtils.hasLength(entityId)){
			logger.error("param 'entityId' is not valid");
			//throw new IllegalArgumentException("param 'entityId' should be valid");
			return null;
		}
		Pageable pageable = null;
		
		// check limits
		if(limit != null){
			Sort sort = null;
			List<String> properties = new ArrayList<String>();
			if(!limit.getSortList().isEmpty()){
				for(String param: limit.getSortList()){
					properties.add(param);
				}
				if(limit.getDirection() == 0){
					sort = new Sort(Direction.ASC, properties);
				} else {
					sort = new Sort(Direction.DESC, properties);
				}
			}
			int pageNum = DEF_PAGE_NUMBER;
			int pageSize = DEF_PAGE_SIZE;
			if(limit.getPage() != 0){
				pageNum = limit.getPage();
			}
			if(limit.getPageSize() != 0){
				pageSize = limit.getPageSize();
			}
			pageable = new PageRequest(pageNum, pageSize, sort);
			
		} else {
			pageable = new PageRequest(DEF_PAGE_NUMBER, DEF_PAGE_SIZE);
		}
		
		List<SocialComment> result = null;
		if(limit!=null){
			if(limit.getFromDate() > 0 && limit.getToDate() > 0){
				result = commentRepository.findByEntityIdAndCreationTimeBetween(entityId, limit.getFromDate(), limit.getToDate(), pageable);
			} else if(limit.getFromDate() > 0){
				result = commentRepository.findByEntityIdAndCreationTimeGreaterThan(entityId, limit.getFromDate(), pageable);
			} else if(limit.getToDate() > 0){
				result = commentRepository.findByEntityIdAndCreationTimeLessThan(entityId, limit.getToDate(), pageable);
			} else {
				result = commentRepository.findByEntityId(entityId, pageable);
			}
		} else {
			result = commentRepository.findByEntityId(entityId, pageable);
		}
		
		return result != null ? SocialComment.toComment(result) : null;
	}
	
	public String readCommentsByEntity_JSON(String entityId, Limit limit){
		String result = "";
		if(!StringUtils.hasLength(entityId)){
			logger.error("param 'entityId' is not valid");
			//throw new IllegalArgumentException("param 'entityId' should be valid");
			return "";
		}
		MongoClient mongoClient = null;
        try {
			mongoClient = new MongoClient(MONGO_HOST , MONGO_PORT);
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		}  catch (MongoException ex){
			logger.error(ex.getMessage());
		}
		DB db = mongoClient.getDB(MONGO_DB);
        DBCollection coll = db.getCollection(MONGO_COLL);
        
        // query with entityId
        BasicDBObject query = new BasicDBObject("entityId", entityId);
        
        DBCursor cursor = null;
        
        // check limits
     	if(limit != null){
     		BasicDBObject sort = null;
     		List<String> properties = new ArrayList<String>();
     		
     		// date limit
     		if(limit.getFromDate() > 0 && limit.getToDate() > 0){
     			query.append("creationTime", new BasicDBObject("$gt", limit.getFromDate()).append("$lt", limit.getToDate()));
     		} else if(limit.getFromDate() > 0 && limit.getToDate() == 0){
     			query = query.append("creationTime", new BasicDBObject("$gt", limit.getFromDate()));
     		} else if(limit.getFromDate() == 0 && limit.getToDate() > 0){
     			query = query.append("creationTime", new BasicDBObject("$lt", limit.getToDate()));
     		}
     		
     		// sort
     		if(!limit.getSortList().isEmpty()){
     			for(String param: limit.getSortList()){
     				properties.add(param);
     			}
     			sort = createSortObject(properties, limit.getDirection());
     		}
     		
     		// pagination
     		if((limit.getPageSize() != 0) && (limit.getPage() >= 0)){
     			int skipPages = limit.getPage() * limit.getPageSize();
     			cursor = coll.find(query).sort(sort).limit(limit.getPageSize()).skip(skipPages);
     		} else if (limit.getPageSize() != 0){
     			cursor = coll.find(query).sort(sort).limit(limit.getPageSize());
     		} else {
     			cursor = coll.find(query).sort(sort);
     		}
     	} else {
     		cursor = coll.find(query);
     	}
        
        result = convertResult(cursor);	// not performed: cicle to all the elements
        
        //CustomConversions custom = new CustomConversions(Arrays.asList(new DBObjetctToStringConverter()));
        //MongoTemplate temp = new MongoTemplate(mongoClient, MONGO_DB);

        //Query query2 = new Query(where("entityId").is(entityId));
        
        //List<String> results = temp.find(query2, String.class);
        
        //for(String res:results){
        //	result = result.concat(res);
        //}
        
		return result;
	}
	
	public Comment delete(String commentId, String user){
		if(!StringUtils.hasLength(commentId)){
			throw new IllegalArgumentException("param 'commentId' should be valid");
		}
		if(!StringUtils.hasLength(user)){
			throw new IllegalArgumentException("param 'user' should be valid");
		}
		
		SocialComment comment = commentRepository.findOne(commentId);
		comment.setDeleted(true);
		comment.setText(String.format(deleteComment, user));
		return commentRepository.save(comment).toComment();
	}
	
	public boolean remove(String commentId, String user){
		if(!StringUtils.hasLength(commentId)){
			logger.error("param 'commentId' should be valid");
			return true;
		}
		if(!StringUtils.hasLength(user)){
			logger.error("param 'author' should be valid");
			return true;
		}
		try{
			commentRepository.delete(commentId);
		} catch(Exception ex){
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean removeByEntity(String entityId){
		if(!StringUtils.hasLength(entityId)){
			logger.error("null 'entityId' passed to the manager.");
			return true;
		}
		
		try{
			List<SocialComment> entityComments = commentRepository.findByEntityId(entityId, null);
			if(entityComments!= null && !entityComments.isEmpty()){
				for(SocialComment comment:entityComments){
					commentRepository.delete(comment.getId());
				}
			} else {
				logger.warn(String.format("no comment found for entity '%s'.", entityId));
			}
		} catch(Exception ex){
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}
	
	private String convertResult(DBCursor cursor){
    	String json = "";
		try{
        	while(cursor.hasNext()) {
        	       json = json + cursor.next().toString();
        	   }
        } finally{
        	cursor.close();
        }
		return json;
    }
	
	private BasicDBObject createSortObject(List<String> params, int direction){
		BasicDBObject sort = null;
		if(direction == 1){
			direction = -1;
		}
		if(params.isEmpty()){
			return sort;
		}
		if(params.size() == 1){
			sort = new BasicDBObject(params.get(0), direction);
		} else {
			sort = new BasicDBObject(params.get(0), direction);
			for(int i = 1; i < params.size(); i++){
				sort.append(params.get(i),direction);
			}
		}
		return sort;
	}
	
	

}
