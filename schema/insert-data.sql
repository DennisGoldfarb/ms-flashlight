-- GENE TYPES
INSERT INTO gene_types (name) VALUES ('protein-coding');
INSERT INTO gene_types (name) VALUES ('rRNA');
INSERT INTO gene_types (name) VALUES ('snRNA');
INSERT INTO gene_types (name) VALUES ('tRNA');
INSERT INTO gene_types (name) VALUES ('scRNA');
INSERT INTO gene_types (name) VALUES ('pseudo');
INSERT INTO gene_types (name) VALUES ('other');
INSERT INTO gene_types (name) VALUES ('miscRNA');
INSERT INTO gene_types (name) VALUES ('snoRNA');
INSERT INTO gene_types (name) VALUES ('unknown');
INSERT INTO gene_types (name) VALUES ('ncRNA');

-- Classification Algorithms
INSERT INTO classification_algorithms(name) VALUES ('HGSCore');
INSERT INTO classification_algorithms(name) VALUES ('CompPASS');
INSERT INTO classification_algorithms(name) VALUES ('SAINT');

-- Sequence Reference Types
INSERT INTO sequence_ref_types (name) VALUES ('UniProtKB/SwissProt');
INSERT INTO sequence_ref_types (name) VALUES ('IPI');
INSERT INTO sequence_ref_types (name) VALUES ('refseq');

-- User Types
INSERT INTO user_types (name) VALUES ('Guest');
INSERT INTO user_types (name) VALUES ('Member');

-- Experiment Types
INSERT INTO experiment_types (name) VALUES ('C');
INSERT INTO experiment_types (name) VALUES ('T');

-- Affinity Purifcation Types
INSERT INTO affinity_purification_types (name) VALUES ('TAP');
INSERT INTO affinity_purification_types (name) VALUES ('Strept');
INSERT INTO affinity_purification_types (name) VALUES ('Antibody');
INSERT INTO affinity_purification_types (name) VALUES ('FLAG');
INSERT INTO affinity_purification_types (name) VALUES ('HA');
INSERT INTO affinity_purification_types (name) VALUES ('Other');
INSERT INTO affinity_purification_types (name) VALUES ('S-tag');

-- Experiment Roles
INSERT INTO experiment_roles (rank, name) VALUES (0,'Public');
INSERT INTO experiment_roles (rank, name) VALUES (1,'Semi-Private');
INSERT INTO experiment_roles (rank, name) VALUES (2,'Private');

-- Ontology types
INSERT INTO ontology_types (ontology_type_id, name) VALUES (1, 'Biological Process');
INSERT INTO ontology_types (ontology_type_id, name) VALUES (2, 'Cellular Component');
INSERT INTO ontology_types (ontology_type_id, name) VALUES (3, 'Human Phenotype');
INSERT INTO ontology_types (ontology_type_id, name) VALUES (4, 'Mouse Phenotype');
INSERT INTO ontology_types (ontology_type_id, name) VALUES (5, 'Disease Ontology');

--Ontology terms
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/data/ontologies/term_out.tab' INTO TABLE ontology_terms;

-- Ontology hierarchy
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/data/ontologies/hier_out.tab' INTO TABLE ontology_hierarchy;

-- Experimental Systems
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/data/experimental_systems.tab' INTO TABLE experimental_systems;

-- BioGRID interactions
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/data/insertBioGRID.sql' INTO TABLE gene_interactions (gene_id_a, gene_id_b, biogrid_id_a, biogrid_id_b, throughput, experimental_system_id, pubmed_id);

-- Gene Pairs
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/data/gene2sim.tab' INTO TABLE gene_pairs (gene_id_a, gene_id_b, seq_similarity_score, gene_pair_hash);

-- TAXONOMY
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/taxonomy.tab' INTO TABLE taxonomy;
-- IPI Sequences
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/human_ipi.tab' INTO TABLE sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/bovine_ipi.tab' INTO TABLE sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/chick_ipi.tab' INTO TABLE sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/mouse_ipi.tab' INTO TABLE sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/rat_ipi.tab' INTO TABLE sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/arath_ipi.tab' INTO TABLE sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/danre_ipi.tab' INTO TABLE sequences;
-- UniProtKB/SwissProt Sequences
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/complete_swiss_prot.tab' INTO TABLE sequences;
-- GeneID to IPI Sequence Mapping
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/ipi2gene_id.tab' INTO TABLE gene_sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/ipi2bovin_gene_id.tab' INTO TABLE gene_sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/ipi2chick_gene_id.tab' INTO TABLE gene_sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/ipi2mouse_gene_id.tab' INTO TABLE gene_sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/ipi2danre_gene_id.tab' INTO TABLE gene_sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/ipi2rat_gene_id.tab' INTO TABLE gene_sequences;
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/ipi2arath_gene_id.tab' INTO TABLE gene_sequences;
-- GeneID to UniProtKB/SwissProt Sequence Mapping
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/sp2gene_id.tab' INTO TABLE gene_sequences;
-- Genes
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/insertGenes.tab' INTO TABLE genes;
-- Gene History
load data local infile '/Users/Dennis/Research/src/data/geneHistory.tab' into table genes (entrez_gene_id, tax_id, official_symbol, is_obsolete);
-- Gene Aliases
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/insertAliases.tab' INTO TABLE gene_aliases;
-- Gene annotations
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/data/ontologies/gene_annotations_out.tab' INTO TABLE gene_annotations;
-- Gene homology
LOAD DATA LOCAL INFILE '/Users/Dennis/Research/src/data/insertHomologene.tab' INTO TABLE homologene (group_id, gene_id, gene_symbol, tax_id);