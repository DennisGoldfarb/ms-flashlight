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
		name = "OntologyRelationType.byName",
		query= "from OntologyRelationType o where name = :name"
	),
	@NamedQuery(
		name = "OntologyRelationType.all",
		query= "from OntologyRelationType o"
	)
})

@Entity
@Table(name="ontology_relation_types")
public class OntologyRelationType extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="ontology_relation_type_id")
	private Integer id;
	@Column(name="name")
	private String name;
	
	public OntologyRelationType() {}
	
	public String toString() {
		return name;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(final Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
}
