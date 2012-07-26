package com.gokaconsulting.taskerweb.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import java.io.InputStream;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.nio.ByteBuffer;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.jdo.PersistenceManager;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

import com.google.gson.Gson;
import java.util.List;
import javax.jdo.Query;

import com.google.gson.GsonBuilder;

import com.gokaconsulting.taskerweb.server.Task;
import com.gokaconsulting.taskerweb.server.PMF;



/*
 import java.io.PrintWriter;
 import javax.jdo.PersistenceManagerFactory;
 import javax.jdo.JDOHelper;
 import com.google.appengine.api.users.User;
 import com.google.appengine.api.users.UserService;
 import com.google.appengine.api.users.UserServiceFactory;
 import com.google.appengine.api.memcache.MemcacheService;
 import com.google.appengine.api.memcache.MemcacheServiceException;
 import com.google.appengine.api.memcache.MemcacheServiceFactory;
 */

public class TaskServlet extends HttpServlet {
	private static final long serialVersionUID = 6608053225431400157L;
	private final Logger logger = Logger.getLogger(TaskServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();

		String taskID = req.getParameter("taskID");
		String userID = req.getParameter("user");
		if (taskID != null) {
			logger.info("Task requested: " + taskID);

			try {
				Key k = KeyFactory.createKey(Task.class.getSimpleName(),
						Long.valueOf(taskID));
				Task t = pm.getObjectById(Task.class, k);
				logger.info("Title is: " + t.getTitle());
				String json = gson.toJson(t);
				json = "{\"Task\":" + json;

				resp.setContentType("text/html");
				resp.setCharacterEncoding("utf-8");
				resp.getWriter().write(json);

			} finally {
				pm.close();
			}
		} else {
			// return all tasks
			if (userID != null) {
				logger.info("Returning all tasks for: " + userID);

				Query q = pm.newQuery(Task.class);
				q.setFilter("completor == userID");
				q.setOrdering("createDate desc");
				q.declareParameters("String userID");

				try {

					@SuppressWarnings("unchecked")
					List<Task> results = (List<Task>) q.execute(userID);
					logger.info("Count of tasks found for user: " + userID
							+ " is: " + results.size());
					if (!results.isEmpty()) {
						for (Task t : results) {
							logger.info("task ID is: " + t.getTaskID() + " title is: " + t.getTitle());
							if(t.getTaskID() == null)
							{
								//TODO: Figure out why this is happening
								logger.severe("Task id for task: " + t.getKey().getId() + " was 0");
								t.setTaskID(t.getKey().getId());
							}
						}
					}
					gson.toJson(results, resp.getWriter());

					resp.setContentType("application/json");
					// resp.setCharacterEncoding("utf-8");

				} finally {
					pm.close();
				}
			}		
		}
	}

	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		String taskID = req.getParameter("taskID");
		// String userID = req.getParameter("user");
		if (taskID != null && Integer.parseInt(taskID) > 0) {
			logger.info("Delete requested for Task: " + taskID);

			try {
				Key k = KeyFactory.createKey(Task.class.getSimpleName(),
						Long.valueOf(taskID));
				Task t = pm.getObjectById(Task.class, k);
				pm.deletePersistent(t);
				logger.info("Delete succesful for Task: " + taskID);
			} catch (Exception e){
				logger.severe(e.getStackTrace().toString());
				logger.severe("Unable to delete Task: " + taskID);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Task: " + " not found for deletion");
			} finally {
				pm.close();
			}
		} else {
			// TODO: error if no task is passed to delete
			logger.warning("Delete request without task ID");
		}
		resp.setContentType("application/json");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		String taskID = req.getParameter("taskID");

		if (taskID != null) {

			logger.info("Task to be updated: " + taskID);
			try {
				Key k = KeyFactory.createKey(Task.class.getSimpleName(),
						Long.valueOf(taskID));
				Task t = pm.getObjectById(Task.class, k);

				String title = req.getParameter("title");
				String creator = req.getParameter("creator");
				String taskDescription = req.getParameter("taskDescription");
				String completor = req.getParameter("completor");
				String status = req.getParameter("status");
				String dueParm = req.getParameter("dueDate");
				String completedParm = req.getParameter("completedDate");
				
				Date dueDate = null;
				Date completedDate = null;

				DateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss Z");

				if (dueParm != null) {
					try {
						logger.info("Due date will be: " + dueParm);
						dueDate = (Date) formatter.parse(dueParm);
					} catch (ParseException e) {
						logger.severe("Failed to convert due date: " + dueParm
								+ " for update");
					}
				}
				if (completedParm != null) {
					try {
						completedDate = (Date) formatter.parse(completedParm);
					} catch (ParseException e) {
						logger.severe("Failed to convert completed date: "
								+ completedParm + " for update");
					}
				}
				
				try {
					ServletFileUpload upload = new ServletFileUpload();


				      FileItemIterator iterator = upload.getItemIterator(req);
				      while (iterator.hasNext()) {
				        FileItemStream item = iterator.next();
				        InputStream stream = item.openStream();

				        if (item.isFormField()) {
				          logger.severe(" Rceived an update with a form field: " + item.getFieldName());
				        } else {
				          logger.warning("Received an update with an uploaded file: " + item.getFieldName() +
				                      ", name = " + item.getName());
 
				  	    FileService fileService = FileServiceFactory.getFileService();

					    AppEngineFile file = fileService.createNewBlobFile("image/png");
					    boolean lock = true;
					    FileWriteChannel writeChannel = fileService
					            .openWriteChannel(file, lock);
					    
				          int len;
				          byte[] buffer = new byte[8192];
				          while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
				        	  writeChannel.write(ByteBuffer.wrap(buffer, 0, len));
				          }
				          
						    writeChannel.closeFinally();

						    
						    
						  t.setBeforePhotoId(fileService.getBlobKey(file).getKeyString());
						  
						  
						  logger.info("Image succesfully stored");
				          
				        }
				      }
					} catch (Exception e) {
						logger.severe(e.getStackTrace().toString());
						logger.severe("Error processing image file for adding task for user: "
						+ creator + " with title: " + title);
					}

				t.setTitle(title);
				t.setCompletor(completor);
				t.setStatus(status);
				t.setTaskDescription(taskDescription);
				t.setDueDate(dueDate);
				t.setCompletedDate(completedDate);
				t.setCreator(creator);

				Gson gson = new GsonBuilder()
						.excludeFieldsWithoutExposeAnnotation().create();

				gson.toJson(t, resp.getWriter());
				resp.setContentType("application/json");

			} finally {
				pm.close();
			}
		}

		else {
			logger.severe("Post attempted without taskID");
		}

	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String title = req.getParameter("title");
		String creator = req.getParameter("creator");
		String taskDescription = req.getParameter("taskDescription");
		String completor = req.getParameter("completor");
		String status = req.getParameter("status");
		String dueParm = req.getParameter("dueDate");
		String beforePhotoId = "-1";
		String afterPhotoId = "-1";

		logger.info("Task to be addeed: " + title);

		Date createDate = new Date();
		Date dueDate = null;

//		DateFormat formatter = new SimpleDateFormat("dd-MM-yy-HH-mm");
		DateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss Z");

		if (dueParm != null) {
			try {
				dueDate = (Date) formatter.parse(dueParm);
			} catch (ParseException e) {
				logger.severe("Failed to convert due date: " + dueParm
						+ " for insert");
			}
		}

		
		try {
		ServletFileUpload upload = new ServletFileUpload();


	      FileItemIterator iterator = upload.getItemIterator(req);
	      while (iterator.hasNext()) {
	        FileItemStream item = iterator.next();
	        InputStream stream = item.openStream();

	        if (item.isFormField()) {
	          logger.severe(" Rceived an insert with a form field: " + item.getFieldName());
	        } else {
	          logger.warning("Received an insert with an uploaded file: " + item.getFieldName() +
	                      ", name = " + item.getName());
	          
	  	    FileService fileService = FileServiceFactory.getFileService();

		    AppEngineFile file = fileService.createNewBlobFile("image/png");
		    boolean lock = true;
		    FileWriteChannel writeChannel = fileService
		            .openWriteChannel(file, lock);
		    
	          int len;
	          byte[] buffer = new byte[8192];
	          while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
	        	  writeChannel.write(ByteBuffer.wrap(buffer, 0, len));
	          }
	          
			    writeChannel.closeFinally();

			  beforePhotoId =  fileService.getBlobKey(file).getKeyString(); 
			  logger.info("Image succesfully stored, key: " + beforePhotoId);
	          
	        }
	      }
		} catch (Exception e) {
			logger.severe(e.getStackTrace().toString());
			logger.severe("Error processing image file for adding task for user: "
			+ creator + " with title: " + title);
		}
		
		Task t = new Task(title, creator, createDate, taskDescription, dueDate,
				completor, status, beforePhotoId, afterPhotoId);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			pm.makePersistent(t);
			logger.info("New task saved, title: " + t.getTitle() + " id: "
					+ t.getKey());
			// TODO: Find better way to get ID for gson
			t.setTaskID(t.getKey().getId());
			if(t.getTaskID()==null || t.getTaskID() == 0)
			{
				logger.severe("Invalid task id created for task with title: " + t.getTitle());
			}

			Gson gson = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create();

			gson.toJson(t, resp.getWriter());
			resp.setContentType("application/json");

		} finally {
			pm.close();
		}
	}
	
	public String photoKey()
	{
		return "placeholder";
	}
	
	public byte[] readImageData(BlobKey blobKey, long blobSize) {
	    BlobstoreService blobStoreService = BlobstoreServiceFactory
	            .getBlobstoreService();
	    byte[] allTheBytes = new byte[0];
	    long amountLeftToRead = blobSize;
	    long startIndex = 0;
	    while (amountLeftToRead > 0) {
	        long amountToReadNow = Math.min(
	                BlobstoreService.MAX_BLOB_FETCH_SIZE - 1, amountLeftToRead);

	        byte[] chunkOfBytes = blobStoreService.fetchData(blobKey,
	                startIndex, startIndex + amountToReadNow);

	        allTheBytes = ArrayUtils.addAll(allTheBytes, chunkOfBytes);

	        amountLeftToRead -= amountToReadNow;
	        startIndex += amountToReadNow;
	    }

	    return allTheBytes;
	}

	public BlobKey writeImageData(byte[] bytes) throws IOException {
	    FileService fileService = FileServiceFactory.getFileService();

	    AppEngineFile file = fileService.createNewBlobFile("image/jpeg");
	    boolean lock = true;
	    FileWriteChannel writeChannel = fileService
	            .openWriteChannel(file, lock);

	    writeChannel.write(ByteBuffer.wrap(bytes));
	    writeChannel.closeFinally();

	    return fileService.getBlobKey(file);
	}
}
