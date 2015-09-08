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
package edu.unc.flashlight.server.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.unc.flashlight.shared.exception.DaoException;
import edu.unc.flashlight.shared.exception.FlashlightException;

public abstract class DaoManager<T> {
	protected SessionFactory sessionFactory;
	protected Session session;
	protected GeneDAO geneDAO;
	protected UserDAO userDAO;
	protected ExperimentDAO experimentDAO;
	protected ExperimentTypeDAO experimentTypeDAO;
	protected GeneTypeDAO geneTypeDAO;
	protected SequenceDAO sequenceDAO;
	protected SequenceRefTypeDAO sequenceRefTypeDAO;
	protected TaxonomyDAO taxonomyDAO;
	protected UserTypeDAO userTypeDAO;
	protected ExperimentRoleDAO experimentRoleDAO;
	protected GeneInteractionDAO geneInteractionDAO;
	protected OntologyTermDAO ontologyTermDAO;
	protected GenePairDAO genePairDAO;
	protected ExperimentDataDAO experimentDataDAO;
	protected UserResultDAO userResultDAO;
	protected UserFDRDAO userFDRDAO;
	protected ClassificationAlgorithmDAO classificationAlgorithmDAO;

	public DaoManager(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public ClassificationAlgorithmDAO getClassificationAlgorithmDAO() {
		if (classificationAlgorithmDAO == null) classificationAlgorithmDAO = new ClassificationAlgorithmDAO(getSession());
		return classificationAlgorithmDAO;
	}
	
	public UserFDRDAO getUserFDRDAO() {
		if (userFDRDAO == null) userFDRDAO = new UserFDRDAO(getSession());
		return userFDRDAO;
	}
	
	public ExperimentDataDAO getExperimentDataDAO() {
		if (experimentDataDAO == null) experimentDataDAO = new ExperimentDataDAO(getSession());
		return experimentDataDAO;
	}
	
	public UserResultDAO getUserResultDAO() {
		if (userResultDAO == null) userResultDAO = new UserResultDAO(getSession());
		return userResultDAO;
	}
	
	public GenePairDAO getGenePairDAO() {
		if (genePairDAO == null) genePairDAO = new GenePairDAO(getSession());
		return genePairDAO;
	}
	
	public OntologyTermDAO getOntologyTermDAO() {
		if (ontologyTermDAO == null) ontologyTermDAO = new OntologyTermDAO(getSession());
		return ontologyTermDAO;
	}
	
	public GeneInteractionDAO getGeneInteractionDAO() {
		if (geneInteractionDAO == null) geneInteractionDAO = new GeneInteractionDAO(getSession());
		return geneInteractionDAO;
	}
	
	public ExperimentRoleDAO getExperimentRoleDAO() {
		if (experimentRoleDAO == null) experimentRoleDAO = new ExperimentRoleDAO(getSession());
		return experimentRoleDAO;
	}
	
	public UserTypeDAO getUserTypeDAO() {
		if (userTypeDAO == null) userTypeDAO = new UserTypeDAO(getSession());
		return userTypeDAO;
	}
	
	public TaxonomyDAO getTaxonomyDAO() {
		if (taxonomyDAO == null) taxonomyDAO = new TaxonomyDAO(getSession());
		return taxonomyDAO;
	}
	
	public SequenceRefTypeDAO getSequenceRefTypeDAO() {
		if (sequenceRefTypeDAO == null) sequenceRefTypeDAO = new SequenceRefTypeDAO(getSession());
		return sequenceRefTypeDAO;
	}
	
	public SequenceDAO getSequenceDAO() {
		if (sequenceDAO == null) sequenceDAO = new SequenceDAO(getSession());
		return sequenceDAO;
	}
	
	public GeneTypeDAO getGeneTypeDAO() {
		if (geneTypeDAO == null) geneTypeDAO = new GeneTypeDAO(getSession());
		return geneTypeDAO;
	}
	
	public ExperimentTypeDAO getExperimentTypeDAO() {
		if (experimentTypeDAO == null) experimentTypeDAO = new ExperimentTypeDAO(getSession());
		return experimentTypeDAO;
	}
	
	public ExperimentDAO getExperimentDAO() {
		if (experimentDAO == null) experimentDAO = new ExperimentDAO(getSession());
		return experimentDAO;
	}

	public GeneDAO getGeneDAO() {
		if (geneDAO == null) geneDAO = new GeneDAO(getSession());
		return geneDAO;
	}
	
	public UserDAO getUserDAO() {
		if (userDAO == null) userDAO = new UserDAO(getSession());
		return userDAO;
	}

	public Session getSession() {
		if (session == null) session = sessionFactory.getCurrentSession();
		if (!session.getTransaction().isActive()) session.beginTransaction();
		return session;
	}
	
	public void flush() {
		if (session == null) session.flush();
	}
	
	public void clear() {
		if (session == null) session.clear();
	}
	
	public T execute(DaoCommand<T> command) throws FlashlightException {
		try {
			T returnValue = command.execute(this);
			getSession().getTransaction().commit();
			return returnValue;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			System.err.print(e.getMessage());
			if (e instanceof HibernateException) throw new DaoException(e);
			else throw new FlashlightException(e.getMessage());
		} finally {

		}
	}
}
