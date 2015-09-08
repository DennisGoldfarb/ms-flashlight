/*******************************************************************************
 * Copyright 2012 The University of North Carolina at Chapel Hill.
 *  All Rights Reserved.
 * 
 *  Permission to use, copy, modify OR distribute this software and its
 *  documentation for educational, research and non-profit purposes, without
 *  fee, and without a written agreement is hereby granted, provided that the
 *  above copyright notice and the following three paragraphs appear in all
 *  copies.
 * 
 *  IN NO EVENT SHALL THE UNIVERSITY OF NORTH CAROLINA AT CHAPEL HILL BE
 *  LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 *  CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE
 *  USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY
 *  OF NORTH CAROLINA HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGES.
 * 
 *  THE UNIVERSITY OF NORTH CAROLINA SPECIFICALLY DISCLAIM ANY
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE
 *  PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 *  NORTH CAROLINA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
 *  UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 *  The authors may be contacted via:
 * 
 *  US Mail:           Dennis Goldfarb
 *                     Wei Wang
 * 
 *                     Department of Computer Science
 *                       Sitterson Hall, CB #3175
 *                       University of N. Carolina
 *                       Chapel Hill, NC 27599-3175
 * 
 *                     Ben Major
 * 
 *                     Department of Cell Biology and Physiology 
 *                       Lineberger Comprehensive Cancer Center
 *                       University of N. Carolina
 *                       Chapel Hill, NC 27599-3175
 * 
 *  Email:             dennisg@cs.unc.edu
 *                     weiwang@cs.unc.edu
 *                     ben_major@med.unc.edu
 * 
 *  Web:               www.unc.edu/~dennisg/
 ******************************************************************************/
package edu.unc.flashlight.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.hibernate.HibernateUtil;
import net.sf.gilead.gwt.GwtConfigurationHelper;
import net.sf.gilead.gwt.PersistentRemoteService;

import org.hibernate.SessionFactory;

import edu.unc.flashlight.server.dao.DaoCommand;
import edu.unc.flashlight.server.dao.DaoManager;
import edu.unc.flashlight.server.util.schedule.Cleanup;
import edu.unc.flashlight.shared.model.ExperimentRole;
import edu.unc.flashlight.shared.model.ExperimentType;
import edu.unc.flashlight.shared.model.upload.UploadProgress;
import edu.unc.flashlight.shared.model.upload.UploadResult;
import edu.unc.flashlight.shared.validation.DatabaseObjectConstraints;
import edu.unc.flashlight.shared.validation.ValueInCollectionConstraint;

public abstract class HibernateServlet extends PersistentRemoteService {	
	private static final long serialVersionUID = 1L;
	public static final String USER_ID_ATT = "user_id";
	private static final String UPLOAD_PROGRESS_ATT = "upload_progress";
	private static final String UPLOAD_RESULT_ATT = "upload_resukt";

	protected static DatabaseObjectConstraints doc;
	
	protected static Cleanup cleanup;

	public HibernateUtil gileadHibernateUtil;

	public void init(final ServletConfig sc) throws ServletException {
		super.init(sc);
		gileadHibernateUtil = new HibernateUtil(edu.unc.flashlight.server.HibernateUtil.getSessionFactory());
		PersistentBeanManager persistentBeanManager = GwtConfigurationHelper.initGwtStatelessBeanManager(gileadHibernateUtil);
		setBeanManager(persistentBeanManager);
		if (doc == null) loadConstraints();
		if (cleanup == null) {	
			cleanup = new Cleanup(4, 0, 0, gileadHibernateUtil);
			cleanup.start();
		}
		
	}
	
	public void destroy() {
		try {
			if (cleanup != null) {
				cleanup.stop();
				System.out.println("Cleanup thread stopped");
			}
		} catch (Exception e) {
			System.out.println("Cleanup thread stop failed");
		}
		try {
			if (gileadHibernateUtil != null) {
				gileadHibernateUtil.closeCurrentSession();
			}
		} catch (Exception e) {
			
		}
		super.destroy();
	}

	public SessionFactory getSessionFactory() {
		return gileadHibernateUtil.getSessionFactory();
	}

/*	public void setUploadProgress(UploadProgress uploadProgress) {
		this.getThreadLocalRequest().getSession().setAttribute(UPLOAD_PROGRESS_ATT, uploadProgress);
	}*/
	
	public void setUploadProgress(UploadProgress uploadProgress, HttpSession session) {
		session.setAttribute(UPLOAD_PROGRESS_ATT, uploadProgress);
	}

	public UploadProgress getUploadProgress() {
		return (UploadProgress) this.getThreadLocalRequest().getSession().getAttribute(UPLOAD_PROGRESS_ATT);
	}
	
	public UploadProgress getUploadProgress(HttpSession session) {
		return (UploadProgress) session.getAttribute(UPLOAD_PROGRESS_ATT);
	}
	
	public void setUploadResult(UploadResult uploadResult) {
		this.getThreadLocalRequest().getSession().setAttribute(UPLOAD_RESULT_ATT, uploadResult);
	}
	
	public void setUploadResult(UploadResult uploadResult, HttpSession session) {
		session.setAttribute(UPLOAD_RESULT_ATT, uploadResult);
	}
	
	public UploadResult getUploadResult() {
		return (UploadResult) this.getThreadLocalRequest().getSession().getAttribute(UPLOAD_RESULT_ATT);
	}
	
	public UploadResult getUploadResult(HttpSession session) {
		return (UploadResult) session.getAttribute(UPLOAD_RESULT_ATT);
	}

	public void setCurrentUserID(Long userID) {
		this.getThreadLocalRequest().getSession().setAttribute(USER_ID_ATT, userID);
	}
	
	public void setCurrentUserID(Long userID, HttpSession session) {
		session.setAttribute(USER_ID_ATT, userID);
	}

	public Long getCurrentUserID() {
		Long id = (Long) this.getThreadLocalRequest().getSession().getAttribute(USER_ID_ATT);	
		if (id == null) id = -1L;
		return id;
	}
	
	public Long getCurrentUserID(HttpSession session) {
		Long id = (Long) session.getAttribute(USER_ID_ATT);	
		if (id == null) id = -1L;
		return id;
	}

	public HttpSession getHTTPSession() {
		return getThreadLocalRequest().getSession();
	}
	
	public String getHTTPSessionID() {
		return getThreadLocalRequest().getSession().getId();
	}

	public void loadConstraints() {
		DaoCommand<Void> daoCommand = new DaoCommand<Void>() {
			public Void execute(DaoManager<Void> manager) {
				doc = new DatabaseObjectConstraints();
				doc.expTypeConstraint = new ValueInCollectionConstraint<ExperimentType>(manager.getExperimentTypeDAO().getAll());
				doc.expRoleConstraint = new ValueInCollectionConstraint<ExperimentRole>(manager.getExperimentRoleDAO().getAll());

	//			db_network = new DatabaseNetwork(manager.getGeneInteractionDAO().getAll());
	//			db_network_low = new DatabaseNetwork(manager.getGeneInteractionDAO().getAllLow());
	//			biological_process = new OntologyNetwork(manager.getOntologyTermDAO().getAllByType("Biological Process"));
	//			cellular_component = new OntologyNetwork(manager.getOntologyTermDAO().getAllByType("Cellular Component"));
	//			human_phenotype = new OntologyNetwork(manager.getOntologyTermDAO().getAllByType("Human Phenotype"));
	//			mouse_phenotype = new OntologyNetwork(manager.getOntologyTermDAO().getAllByType("Mouse Phenotype"));
	//			disease_ontology = new OntologyNetwork(manager.getOntologyTermDAO().getAllByType("Disease Ontology"));
				
				
				return null;
			}
		};
		try {
			new DaoManager<Void>(getSessionFactory()){}.execute(daoCommand);
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
	}
}
