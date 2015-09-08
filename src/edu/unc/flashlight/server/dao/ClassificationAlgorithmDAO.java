package edu.unc.flashlight.server.dao;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.unc.flashlight.shared.model.ClassificationAlgorithm;

public class ClassificationAlgorithmDAO extends GenericDAO<ClassificationAlgorithm> {

	public ClassificationAlgorithmDAO(Session session) {
		super(session);
	}
	
	public Set<ClassificationAlgorithm> getAll() {
		Query q = session.getNamedQuery("ClassificationAlgorithm.all");
		Set<ClassificationAlgorithm> result = new HashSet<ClassificationAlgorithm>(q.list());
		return result;
	}

	public ClassificationAlgorithm getByName(String name) {
		Query q = session.getNamedQuery("ClassificationAlgorithm.byName");
		q.setString("name", name);
		ClassificationAlgorithm result = (ClassificationAlgorithm) q.uniqueResult();
		return result;
	}
}
