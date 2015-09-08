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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.unc.flashlight.client.service.IdConversionService;
import edu.unc.flashlight.server.dao.DaoCommand;
import edu.unc.flashlight.server.dao.DaoManager;

public class IdConversionServiceImpl extends HibernateServlet implements IdConversionService{
	private static final long serialVersionUID = 1L;
	private static int batch_size = 1000;

	private Collection<Collection<String>> batchInput(Collection<String> input){
		Collection<Collection<String>> batches = new HashSet<Collection<String>>();
		Iterator<String> itr = input.iterator();
		for (int i = 0; i < Math.ceil(input.size()/(double)batch_size); i++) {
			Collection<String> batch = new HashSet<String>();
			for (int j = 0; j < batch_size && itr.hasNext(); j++) {
				batch.add(itr.next());
			}
			batches.add(batch);
		}
		return batches;
	}

	/*public Set<Long> convertToGeneID(final Collection<String> input, final Long tax_id) throws Exception{
		final String sid = this.getThreadLocalRequest().getSession().getId();
		DaoCommand<Set<Long>> daoCommand = new DaoCommand<Set<Long>>() {
			public Set<Long> execute(DaoManager<Set<Long>> manager) {
				return manager.getGeneDAO().convertToGeneID(input, tax_id, sid);
			}
		};
		return new DaoManager<Set<Long>>(getSessionFactory()){}.execute(daoCommand);
		
		

	}*/

}
