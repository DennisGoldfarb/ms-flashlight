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

import java.util.List;

import edu.unc.flashlight.client.service.UserService;
import edu.unc.flashlight.server.dao.DaoCommand;
import edu.unc.flashlight.server.dao.DaoManager;
import edu.unc.flashlight.shared.exception.FlashlightException;
import edu.unc.flashlight.shared.model.User;

public class UserServiceImpl extends HibernateServlet implements UserService {
	private static final long serialVersionUID = 1L;

	public void createGuestUser() throws Exception {
		DaoCommand<User> daoCommand = new DaoCommand<User>() {
			public User execute(DaoManager<User> manager) {
				User u = manager.getUserDAO().createGuestUser();
				setCurrentUserID(u.getId());
				return u;
			}
		};
		new DaoManager<User>(getSessionFactory()){}.execute(daoCommand);
	}
	
	public Long getLoggedInUserID() {
		return getCurrentUserID();
	}
	
	public List<User> getPublicUsers() throws FlashlightException {
		DaoCommand<List<User>> daoCommand = new DaoCommand<List<User>>() {
			public List<User> execute (DaoManager<List<User>> manager) {
				return manager.getUserDAO().getPublicUsers();
			}
		};
		return new DaoManager<List<User>>(getSessionFactory()){}.execute(daoCommand);
	}

}
