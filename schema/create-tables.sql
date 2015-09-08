CREATE TABLE gene_types
(
	gene_type_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, -- Change AUTO_INCREMENT to IDENTITY(1,1) for sql-server
	name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE taxonomy
(
	tax_id BIGINT NOT NULL PRIMARY KEY, -- No auto generation. This ID is supplied by the NCBI Taxonomy database.
	name VARCHAR(100) NOT NULL -- Should be unique, can't guarantee.
);

CREATE TABLE sequence_ref_types
(
	sequence_ref_type_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

CREATE TABLE sequences
(
	sequence_id VARCHAR(20) NOT NULL PRIMARY KEY, -- This ID is supplied by the UniProtKB/SwissProt database or IPI database
	sequence_ref_type_id BIGINT NOT NULL REFERENCES sequnce_ref_types(sequence_ref_type_id),
	sequence TEXT
);

CREATE TABLE gene_sequences
(
	gene_id BIGINT NOT NULL REFERENCES gene(entrez_gene_id),
	sequence_id CHAR(11) NOT NULL REFERENCES sequences(sequence_id),
	PRIMARY KEY(gene_id, sequence_id)
);

CREATE TABLE genes
(
	entrez_gene_id BIGINT NOT NULL PRIMARY KEY, -- No auto generation. This ID is supplied by the NCBI Gene database.
	tax_id BIGINT NOT NULL REFERENCES taxonomy(tax_id),
	official_symbol VARCHAR(50) NOT NULL, -- Should be unique, can't guarantee it though.
	description TEXT,
	chromosome VARCHAR(2),
	gene_type_id BIGINT REFERENCES gene_types(gene_type_id),
	is_obsolete BOOLEAN NOT NULL DEFAULT false
);
--Indexes (official_symbol) (entrez_gene_id, tax_id)

CREATE TABLE gene_aliases
(
	gene_id BIGINT NOT NULL REFERENCES gene(entrez_gene_id),
	name VARCHAR(100) NOT NULL,
	PRIMARY KEY(gene_id, name)
);
--Indexes (name)

CREATE TABLE homologene
(
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	group_id BIGINT NOT NULL,
	gene_id BIGINT NOT NULL,
	gene_symbol VARCHAR(50) NOT NULL,
	tax_id BIGINT NOT NULL REFERENCES taxonomy(tax_id)
);

CREATE TABLE classification_algorithms
(
	classification_algorithm_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL
);

----------------------------------------------------------------
---------------------- USER STUFF ------------------------------
----------------------------------------------------------------

CREATE TABLE user_types
(
	user_type_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(20)
);

CREATE TABLE users
(
	user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_type_id BIGINT NOT NULL REFERENCES user_types(user_type_id),
	experiment_role_id BIGINT NOT NULL REFERENCES experiment_roles(experiment_role_id),
	email VARCHAR(50),
	username VARCHAR(20) UNIQUE,
	password CHAR(20),
	purification_method VARCHAR(50),
	pubmed_id BIGINT,
	lab_name VARCHAR(50),
	created_time timestamp
);

----------------------------------------------------------------
--------------------------Mass Spec-----------------------------
----------------------------------------------------------------
CREATE TABLE experiment_types
(
	experiment_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(20) NOT NULL
);

CREATE TABLE affinity_purification_types
(
	affinity_purification_type_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL
);

CREATE TABLE experiment_roles
(
	experiment_role_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	rank INT NOT NULL,
	name VARCHAR(20)
);

CREATE TABLE experiments
(
	experiment_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id BIGINT NOT NULL REFERENCES users(user_id),
	experiment_role_id BIGINT NOT NULL REFERENCES experiment_roles(experiment_role_id),
	experiment_type_id BIGINT NOT NULL REFERENCES experiment_types(experiment_type_id),
	name VARCHAR(50) NOT NULL,
	bait_gene_id BIGINT REFERENCES genes(entrez_gene_id),
	bait_upload_id VARCHAR(50) NOT NULL,
	bait_nice_name VARCHAR(50) NOT NULL,
	UNIQUE KEY(user_id, name)
);
CREATE INDEX experiment_user_id_type ON experiments (user_id, experiment_type_id);
CREATE INDEX experiment_user_nice ON experiments (experiment_id,bait_nice_name);


CREATE TABLE experiment_data
(
	experiment_data_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	experiment_id BIGINT NOT NULL REFERENCES experiments(experiment_id),
	prey_gene_id BIGINT REFERENCES genes(entrez_gene_id),
	prey_upload_id VARCHAR(50) NOT NULL,
	prey_nice_name VARCHAR(50) NOT NULL,
	gene_pair_hash VARCHAR(50) REFERENCES gene_pairs(gene_pair_hash),
	spectral_count INT NOT NULL
);
CREATE INDEX experiment_data_exp_id ON experiment_data (experiment_id);
CREATE INDEX experiment_data_hash ON experiment_data (gene_pair_hash);
CREATE INDEX experiment_data_nice ON experiment_data (experiment_id,prey_nice_name);


CREATE TABLE user_results
(
	user_results_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id BIGINT NOT NULL REFERENCES users(user_id),
	bait_gene_id BIGINT REFERENCES genes(entrez_gene_id),
	prey_gene_id BIGINT REFERENCES genes(entrez_gene_id),
	bait_upload_id VARCHAR(50) NOT NULL,
	prey_upload_id VARCHAR(50) NOT NULL,
	bait_nice_name VARCHAR(50),
	prey_nice_name VARCHAR(50),
	gene_pair_hash VARCHAR(50) REFERENCES gene_pairs(gene_pair_hash),
	ms_score DOUBLE NOT NULL DEFAULT 0,
	ms_p_value DOUBLE NOT NULL DEFAULT 0,
	classifier_score DOUBLE,
	classifier_p_value DOUBLE,
	classification_algorithm_id INT NOT NULL REFERENCES classification_algorithms(classification_algorithm_id)
);
CREATE INDEX user_results_bait_id ON user_results (bait_gene_id);
CREATE INDEX user_results_prey_id ON user_results (prey_gene_id);
CREATE INDEX user_results_hash ON user_results (gene_pair_hash);
CREATE INDEX user_results_user_id ON user_results (user_id,gene_pair_hash,bait_gene_id,prey_gene_id,bait_upload_id,prey_upload_id);
CREATE INDEX user_results_prey_upload ON user_results (prey_upload_id,user_id);
CREATE INDEX user_results_bait_upload ON user_results (bait_upload_id,user_id);
CREATE INDEX user_results_prey_nice ON user_results (prey_nice_name,user_id);
CREATE INDEX user_results_bait_nice ON user_results (bait_nice_name,user_id);


CREATE TABLE user_fdr
(
	user_fdr_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id BIGINT NOT NULL REFERENCES users(user_id),
	fdr DOUBLE NOT NULL,
	threshold DOUBLE NOT NULL,
	num_accepted INT NOT NULL,
);

----------------------------------------------------------------
---------------------- OTHER STUFF -----------------------------
----------------------------------------------------------------
/** BP, CC, MR_human, MR_mouse, MR_worm, MR_chicken, MR_rat, MR_fish, MR_monkey, domain, homo_int, intercept **/
CREATE TABLE gene_pairs
(
	gene_pair_hash VARCHAR(50) NOT NULL PRIMARY KEY,
	bp_score DOUBLE,
	cc_score DOUBLE,
	coxp_human_score DOUBLE,
	coxp_mouse_score DOUBLE,
	coxp_worm_score DOUBLE,
	coxp_chicken_score DOUBLE,
	coxp_fly_score DOUBLE,
	coxp_rat_score DOUBLE,
	coxp_fish_score DOUBLE,
	coxp_monkey_score DOUBLE,
	coxp_dog_score DOUBLE,
	domain_score DOUBLE,
	human_phen_score DOUBLE,
	mouse_phen_score DOUBLE,
	disease_score DOUBLE,
	homo_int_score DOUBLE,
	is_known CHAR(1)
);

CREATE TABLE gene_pairs_imputed
(
	gene_pair_hash VARCHAR(50) NOT NULL PRIMARY KEY,
	bp_score DOUBLE,
	cc_score DOUBLE,
	coxp_human_score DOUBLE,
	coxp_mouse_score DOUBLE,
	coxp_worm_score DOUBLE,
	coxp_chicken_score DOUBLE,
	coxp_fly_score DOUBLE,
	coxp_rat_score DOUBLE,
	coxp_fish_score DOUBLE,
	coxp_monkey_score DOUBLE,
	coxp_dog_score DOUBLE,
	domain_score DOUBLE,
	human_phen_score DOUBLE,
	mouse_phen_score DOUBLE,
	disease_score DOUBLE,
	homo_int_score DOUBLE,
	is_known CHAR(1)
);

CREATE TABLE feature_bins
(
	gene_id INT NOT NULL,
	bin_index INT NOT NULL,
	bin_count DOUBLE NOT NULL,
	PRIMARY KEY (gene_id, bin_index)
);

CREATE TABLE gene_homology
(
	group_id BIGINT NOT NULL, -- No auto_increment, ID comes from NCBI's homologene database
	gene_id BIGINT NOT NULL REFERENCES genes(entrez_gene_id),
	PRIMARY KEY (group_id, gene_id)
);

CREATE TABLE experimental_systems
(
	experimental_system_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50),
	type VARCHAR(20)
);

CREATE TABLE gene_interactions
(
	gene_interaction_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	gene_id_a BIGINT NOT NULL REFERENCES genes(entrez_gene_id),
	gene_id_b BIGINT NOT NULL REFERENCES genes(entrez_gene_id),
	biogrid_id_a BIGINT NOT NULL,
	biogrid_id_b BIGINT NOT NULL,
	throughput CHAR(1) NOT NULL,
	experimental_system_id BIGINT NOT NULL REFERENCES experimental_systems(experimental_system_id),
	pubmed_id BIGINT
);

----------------------------------------------------------------
---------------------- ONTOLOGIES ------------------------------
----------------------------------------------------------------

CREATE TABLE ontology_types
(
	ontology_type_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE ontology_qualifiers
(
	ontology_qualifier_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE ontology_terms
(
	ontology_term_id VARCHAR(100) NOT NULL PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	ontology_type_id BIGINT NOT NULL REFERENCES ontology_types(ontology_type_id),
	ic FLOAT NOT NULL DEFAULT 0,
	count INT NOT NULL DEFAULT 0
);
CREATE INDEX id_type_index ON ontology_terms (ontology_term_id, ontology_type_id);

CREATE TABLE ontology_relation_types
(
	ontology_relation_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE gene_annotations
(
	gene_annotation_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	gene_id BIGINT NOT NULL REFERENCES genes(entrez_gene_id),
	ontology_term_id VARCHAR(100) NOT NULL REFERENCES ontology_terms(ontology_term_id),
	ontology_qualifier_id INT REFERENCES ontology_qualifiers(ontology_qualifier_id),
	pubmed_id BIGINT
);
CREATE INDEX gene_term_index ON gene_annotations (gene_id, ontology_term_id);

CREATE TABLE ontology_hierarchy
(
	parent_ontology_term_id VARCHAR(100) NOT NULL REFERENCES ontology_terms(ontology_term_id),
	child_ontology_term_id VARCHAR(100) NOT NULL REFERENCES ontology_terms(ontology_term_id),
	ontology_relation_type_id INT NOT NULL REFERENCES ontology_relation_types(ontology_relation_type_id),
	PRIMARY KEY (parent_ontology_term_id, child_ontology_term_id, ontology_relation_type_id)
);
CREATE INDEX child_parent_index ON ontology_hierarchy (child_ontology_term_id, parent_ontology_term_id);



