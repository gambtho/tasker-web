package com.gokaconsulting.taskerweb.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class PhotoServlet extends HttpServlet {

	private static final long serialVersionUID = 6085628075010152824L;
	private final Logger logger = Logger
			.getLogger(PhotoServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String blobString = req.getParameter("blobKey");
		if (blobString != null) {
			logger.info("Retrieving photo for: " + blobString);
			BlobKey blobKey = new BlobKey(req.getParameter("blobKey"));

			BlobstoreService blobStoreService = BlobstoreServiceFactory
					.getBlobstoreService();
			blobStoreService.serve(blobKey, resp);
		}
	}
}