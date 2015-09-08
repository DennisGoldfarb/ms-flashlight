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
package edu.unc.flashlight.server.util.schedule;

import java.sql.Timestamp;
import java.util.Date;

import net.sf.gilead.core.hibernate.HibernateUtil;
import edu.unc.flashlight.server.dao.DaoCommand;
import edu.unc.flashlight.server.dao.DaoManager;
import edu.unc.flashlight.server.util.FileUtil;

public class Cleanup {
	private static final long serialVersionUID = 1L;
	private final Scheduler scheduler = new Scheduler();
    private final int hourOfDay, minute, second;
    private HibernateUtil gileadHibernateUtil;

    public Cleanup(int hourOfDay, int minute, int second, HibernateUtil gileadHibernateUtil) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.second = second;
        this.gileadHibernateUtil = gileadHibernateUtil;
    }
    
    public void stop() {
    	scheduler.cancel();
    }

    public void start() {
        scheduler.schedule(new SchedulerTask() {
            public void run() {
            	try {
            	DaoCommand<Void> daoCommand = new DaoCommand<Void>() {
        			public Void execute(DaoManager<Void> manager) {		
        				
        				Long purgeTime = new Date().getTime()-(1000*60*60*24);
        				Timestamp now = new Timestamp(purgeTime);
    
        				manager.getUserResultDAO().deleteDataForOldUsers(now);
        				manager.getExperimentDataDAO().deleteDataForOldUsers(now);	
        				manager.getExperimentDAO().deleteDataForOldUsers(now);	
        				manager.getUserFDRDAO().deleteDataForOldUsers(now);
        				manager.getUserDAO().deleteDataForOldUsers(now);
        				FileUtil.deleteOldFiles(purgeTime);
        				return null;
        			}
        		};
        		new DaoManager<Void>(gileadHibernateUtil.getSessionFactory()){}.execute(daoCommand);
            	} catch (Exception e) {
            		System.out.println(e.getMessage());
            	}
            }
        }, new DailyIterator(hourOfDay, minute, second));
    }
}
