DELIMITER $$
DROP PROCEDURE IF EXISTS `GetAncestry` $$
CREATE PROCEDURE `GetAncestry`(IN id VARCHAR(20), IN relation_type_id BIGINT)
    DETERMINISTIC
BEGIN
    DECLARE ch, p VARCHAR(20);
    DECLARE name_ch, name_p VARCHAR(200);
    DECLARE done, does_exist INT DEFAULT FALSE;
    DECLARE cur_hierarchy CURSOR FOR 
    	(SELECT oh.child_ontology_term_id, oh.parent_ontology_term_id, t1.name, t2.name
        FROM ontology_hierarchy oh 
        inner join ontology_terms t1 on t1.ontology_term_id = oh.child_ontology_term_id
        inner join ontology_terms t2 on t2.ontology_term_id = oh.parent_ontology_term_id
        WHERE oh.child_ontology_term_id = id and oh.ontology_relation_type_id = relation_type_id); 
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
        
    OPEN cur_hierarchy;
    read_loop: LOOP
      FETCH cur_hierarchy INTO ch, p, name_ch, name_p;
      IF done THEN
        LEAVE read_loop;
      END IF;

      select 1 from tmp_GetAncestryGene where tmp_GetAncestryGene.name_ch = name_ch and tmp_GetAncestryGene.name_p = name_p into does_exist;
      IF done THEN  
      	INSERT into tmp_GetAncestryGene VALUES (name_ch,name_p);
      	CALL GetAncestry(p, relation_type_id);
      	set done = FALSE;
      END IF;

    END LOOP;

    CLOSE cur_hierarchy;
END $$

DROP PROCEDURE IF EXISTS `GetAncestryGene` $$
CREATE PROCEDURE `GetAncestryGene`(IN id_a BIGINT, IN id_b BIGINT, IN type_id BIGINT, IN relation_type_id BIGINT)
    DETERMINISTIC
BEGIN
    DECLARE ont_id, p VARCHAR(20);
    DECLARE name_ch, name_p VARCHAR(200);
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur_annotations CURSOR FOR (SELECT DISTINCT ga.ontology_term_id 
    	FROM gene_annotations ga inner join ontology_terms o on ga.ontology_term_id = o.ontology_term_id 
    	WHERE ga.gene_id IN (id_a, id_b) AND o.ontology_type_id = type_id);
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    SET @@session.max_sp_recursion_depth = 255; 
    CREATE TEMPORARY TABLE IF NOT EXISTS tmp_GetAncestryGene (name_ch VARCHAR(200), name_p VARCHAR(200)) engine=memory ;

    OPEN cur_annotations;

    read_loop: LOOP
      FETCH cur_annotations INTO ont_id;
      IF done THEN
        LEAVE read_loop;
      END IF;
      SET p = ont_id;
	  CALL GetAncestry(p, relation_type_id);
      set done = FALSE;
    END LOOP;

    CLOSE cur_annotations;

    SELECT DISTINCT * from tmp_GetAncestryGene;
    DROP TABLE tmp_GetAncestryGene;
END $$
DELIMITER ;