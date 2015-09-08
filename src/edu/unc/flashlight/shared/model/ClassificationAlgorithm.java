package edu.unc.flashlight.shared.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import net.sf.gilead.pojo.gwt.LightEntity;

@NamedQueries({
	@NamedQuery(
			name = "ClassificationAlgorithm.all",
			query = "from ClassificationAlgorithm ca"
			),
	@NamedQuery(
			name = "ClassificationAlgorithm.byName",
			query = "from ClassificationAlgorithm ca where ca.name = :name"
			)
})

@Entity
@Table(name="classification_algorithms")
public class ClassificationAlgorithm extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="classification_algorithm_id")
	private int id;
	@Column(name="name")
	private String name;

	public ClassificationAlgorithm() {}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
