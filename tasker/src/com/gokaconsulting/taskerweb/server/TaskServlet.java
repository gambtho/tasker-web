package com.gokaconsulting.taskerweb.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Logger;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;


import javax.jdo.PersistenceManager;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.gson.Gson;
import java.util.List;
import javax.jdo.Query;
//import com.google.gson.GsonBuilder;

import com.gokaconsulting.taskerweb.server.Task;
import com.gokaconsulting.taskerweb.server.PMF;

/*
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
		Gson gson = new Gson();
		
		String taskID = req.getParameter("task");
		String userID = req.getParameter("user");
		if(taskID!=null)
		{
			logger.info("Task requested: " + taskID);


		    try {
		    	Key k = KeyFactory.createKey(Task.class.getSimpleName(), Long.valueOf(taskID));
		        Task t = pm.getObjectById(Task.class, k);
		        logger.info("Title is: " + t.getTitle());
		        String json = gson.toJson(t);
		        
		        resp.setContentType("text/html"); 
		        resp.setCharacterEncoding("utf-8"); 
		        resp.getWriter().write(json);
		        
		    } finally {
		        pm.close();
		    }	
		}
		else
		{
			//return all tasks
			logger.info("Returning all tasks for: " + userID);

		        Query q = pm.newQuery(Task.class);
		        q.setFilter("creator == userID");
		        q.setOrdering("createDate desc");
		        q.declareParameters("String userID");
		        
		        try {
		        
		        @SuppressWarnings("unchecked")
				List<Task> results = (List<Task>)q.execute(userID);
		        if(!results.isEmpty()) {
		        	logger.info("Count of tasks found for user: " + userID + " is: " + results.size());
		        	for (Task t: results)
		        	{
		        		gson.toJson(t, resp.getWriter());
		        	}
		        }
		        else
		        {
		        	logger.info("No tasks found for user: " + userID);
		        }
		        resp.setContentType("text/html"); 
		        resp.setCharacterEncoding("utf-8"); 
		        
		    } finally {
		        pm.close();
		    }	
		}
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		String taskID = req.getParameter("task");
//		String userID = req.getParameter("user");
		if(taskID!=null)
		{
			logger.info("Delete requested for Task: " + taskID);

		    try {
		    	Key k = KeyFactory.createKey(Task.class.getSimpleName(), Long.valueOf(taskID));
		        Task t = pm.getObjectById(Task.class, k);
		        pm.deletePersistent(t);
		        logger.info("Delete succesful for Task: " + taskID);        
		    } finally {
		        pm.close();
		    }	
		}
		else {
			//TODO: error if no task is passed to delete
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		String taskID = req.getParameter("task");

		if (taskID != null) {

			logger.info("Task to be updated: " + taskID);
			try {
				Key k = KeyFactory.createKey(Task.class.getSimpleName(),
						Long.valueOf(taskID));
				Task t = pm.getObjectById(Task.class, k);

				DateFormat formatter = new SimpleDateFormat("dd-MM-yy-HH-mm-ss");
				Date createDate = null;

				String title = req.getParameter("title");
				String creator = req.getParameter("creator");
				// TODO: shouldn't really allow update of create date
				if (req.getParameter("createDate") != null) {
					try {
						createDate = (Date) formatter.parse(req
								.getParameter("createDate"));
					} catch (ParseException e) {
						logger.severe("Failed to convert create date: "
								+ req.getParameter("createDate")
								+ " for update");

					}
				}
				
				t.setTitle(title);
				t.setCreateDate(createDate);
				t.setCreator(creator);
				
			} finally {
				pm.close();
			}
		}

	}
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
    	
		DateFormat formatter = new SimpleDateFormat("dd-MM-yy-HH-mm-ss");
		
    	String title = req.getParameter("title");
    	String creator = req.getParameter("creator");
    	Date createDate = null;
    	try {
			createDate = (Date)formatter.parse(req.getParameter("createDate"));
		} catch (ParseException e) {
			logger.severe("Failed to convert create date: " + req.getParameter("createDate") + " for insert");
		}
    	
    	Task t = new Task(title, creator, createDate);
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
    	try {
    		pm.makePersistent(t);
    		logger.info("New task saved, title: " + t.getTitle() + " id: " + t.getKey());
    	} finally {
    		pm.close();
    	}
	}
}
