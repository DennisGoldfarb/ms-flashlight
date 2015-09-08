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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import edu.unc.flashlight.client.service.UploadService;
import edu.unc.flashlight.server.dao.DaoCommand;
import edu.unc.flashlight.server.dao.DaoManager;
import edu.unc.flashlight.server.ms.APMSControlDataset;
import edu.unc.flashlight.server.ms.ClassifierResults;
import edu.unc.flashlight.server.ms.CompPASS;
import edu.unc.flashlight.server.ms.HGSCore;
import edu.unc.flashlight.server.ms.MassSpecScorer;
import edu.unc.flashlight.server.ms.SAINT;
import edu.unc.flashlight.server.parser.Parser;
import edu.unc.flashlight.server.parser.SpotliteParser;
import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.server.rf.IndirectPermutationGenerator;
import edu.unc.flashlight.server.rf.SpotliteClassifier;
import edu.unc.flashlight.server.util.FileUtil;
import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.exception.FlashlightException;
import edu.unc.flashlight.shared.exception.upload.UploadIOException;
import edu.unc.flashlight.shared.model.Experiment;
import edu.unc.flashlight.shared.model.ExperimentData;
import edu.unc.flashlight.shared.model.Gene;
import edu.unc.flashlight.shared.model.GenePair;
import edu.unc.flashlight.shared.model.User;
import edu.unc.flashlight.shared.model.SAINT.SaintParameters;
import edu.unc.flashlight.shared.model.upload.DataForScoring;
import edu.unc.flashlight.shared.model.upload.UploadProgress;
import edu.unc.flashlight.shared.model.upload.UploadResult;
import edu.unc.flashlight.shared.model.upload.UploadResultCount;
import edu.unc.flashlight.shared.model.upload.UploadRow;
import edu.unc.flashlight.shared.model.upload.UploadRowSAINT;
import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.util.Constants.MS_ALGORITHMS;
import edu.unc.flashlight.shared.util.Conversion;

public class UploadServiceImpl extends HibernateServlet implements UploadService{
	private static final long serialVersionUID = 1L;
	
	private void parseFileHelper(final String fileOnServer, final Long taxID, final Long role, final MS_ALGORITHMS alg, 
			final boolean useRandomForest, final SaintParameters saintParameters, final HttpSession session) {
		try {
			final UploadProgress progress = new UploadProgress();
			setUploadProgress(progress, session);
			final List<UploadRow> uploadRows = new ArrayList<UploadRow>();
			final String sid = session.getId();
			
			DaoCommand<User> cleanCommand = new DaoCommand<User>() {
				public User execute(DaoManager<User> manager) throws FlashlightException{	
					User u = manager.getUserDAO().getCurrentOrNewUser(getCurrentUserID(session));
					
					manager.getUserResultDAO().deleteDataForUser(getCurrentUserID(session));
					manager.getExperimentDataDAO().deleteDataForUser(getCurrentUserID(session));	
					manager.getExperimentDAO().deleteDataForUser(getCurrentUserID(session));	
					manager.getUserFDRDAO().deleteDataForUser(getCurrentUserID(session));
					return u;
				}
			};
			final User u = new DaoManager<User>(getSessionFactory()){}.execute(cleanCommand);
					
			DaoCommand<UploadResult> mappingCommand = new DaoCommand<UploadResult>() {
				public UploadResult execute(DaoManager<UploadResult> manager) throws FlashlightException{					
					UploadResult uploadResult = doMapping(fileOnServer,progress,manager,taxID,u,alg,sid,uploadRows,session);
					return uploadResult;
				}
			};
			
			final UploadResult uploadResult = new DaoManager<UploadResult>(getSessionFactory()){}.execute(mappingCommand);
			if (uploadResult.getErrors().size() > 0) {
				setUploadResult(uploadResult, session);
				return;
			}
			
			DaoCommand<GenePairScore[]> getDataSAINTCommand = new DaoCommand<GenePairScore[]>() {
				public GenePairScore[] execute(DaoManager<GenePairScore[]> manager) throws FlashlightException{				
					GenePairScore[] scores = manager.getExperimentDataDAO().getHashesForUser(u.getId(),2L);			
					return scores;
				}
			};
			
			DaoCommand<DataForScoring> getDataOtherCommand = new DaoCommand<DataForScoring>() {
				public DataForScoring execute(DaoManager<DataForScoring> manager) throws FlashlightException{	
					Long userID = u.getId();
					Map<Long,String> exp2bait = manager.getExperimentDAO().getExp2Bait(userID,2L);
					Map<Long,Map<String,Integer>> exp2prey2sc = manager.getExperimentDataDAO().getExp2Prey2SC(userID,2L);
					Map<Long,String> c_exp2bait = manager.getExperimentDAO().getExp2Bait(userID,1L);
					Map<Long,Map<String,Integer>> c_exp2prey2sc = manager.getExperimentDataDAO().getExp2Prey2SC(userID,1L);
					Map<String,Integer> prey2length = manager.getGeneDAO().getLengthsForUser(userID);
					Set<Long> expsForUser = new HashSet<Long>(manager.getExperimentDAO().getExp(userID, 2L));
					
					return new DataForScoring(exp2bait, exp2prey2sc, c_exp2bait, c_exp2prey2sc, prey2length, expsForUser);
				}
			};
			
			final ClassifierResults results;
			if (uploadRows.size() > 0 && uploadRows.get(0) instanceof UploadRowSAINT) {
				final GenePairScore[] scores = new DaoManager<GenePairScore[]>(getSessionFactory()){}.execute(getDataSAINTCommand);	
				results = calculateMassSpecScores(scores, u.getId(), progress, role, sid, uploadRows);
			} else {
				final DataForScoring dataForScoring = new DaoManager<DataForScoring>(getSessionFactory()){}.execute(getDataOtherCommand);
				results = calculateMassSpecScores(dataForScoring, u.getId(), progress, sid, alg, saintParameters, session);
			}
			
			final IndirectPermutationGenerator permGen;		
			final Map<String,Double[]> input;
			
			DaoCommand<IndirectPermutationGenerator> dataCommand1 = new DaoCommand<IndirectPermutationGenerator>() {
				public IndirectPermutationGenerator execute(DaoManager<IndirectPermutationGenerator> manager) throws FlashlightException{
				
					long numMappedBaits = manager.getExperimentDAO().getNumMappedBaits(u.getId());
					
					if (useRandomForest && numMappedBaits > 0) {		
						return manager.getGenePairDAO().getPermutationGenerator(u.getId());
					}	
					return null;
				}
			};
			
			permGen = new DaoManager<IndirectPermutationGenerator>(getSessionFactory()){}.execute(dataCommand1);
			
			DaoCommand<Map<String,Double[]>> dataCommand2 = new DaoCommand<Map<String,Double[]>>() {
				public Map<String,Double[]> execute(DaoManager<Map<String,Double[]>> manager) throws FlashlightException{
					
					if (permGen != null) {					
						return manager.getGenePairDAO().getFeatureScores(sid, results.scores);				
					}	
					return null;
				}
			};
			
			input = new DaoManager<Map<String,Double[]>>(getSessionFactory()){}.execute(dataCommand2);
			
			DaoCommand<Void> scoreCommand = new DaoCommand<Void>() {
				public Void execute(DaoManager<Void> manager) throws FlashlightException{
					if (permGen != null) {
						SpotliteClassifier rf = new SpotliteClassifier(alg);
						results.scores = rf.scoreData(input, results, permGen, new GenericCommand<Double>() {
							public void execute(Double d) {
								progress.setClassifierProgress(d);
								setUploadProgress(progress, session);
							}					
						});
					} 
					progress.setClassifierProgress(1);
					setUploadProgress(progress, session);
					return null;
				}
			};
			
			new DaoManager<Void>(getSessionFactory()){}.execute(scoreCommand);
				
			DaoCommand<Void> uploadCommand = new DaoCommand<Void>() {
				public Void execute(DaoManager<Void> manager) throws FlashlightException{
						
					int alg_id = manager.getClassificationAlgorithmDAO().getByName(alg.toString()).getId();
					manager.getUserResultDAO().bulkInsert(results.scores, sid, u.getId(), alg_id);
					progress.setUpdateProgress(.5);
					setUploadProgress(progress, session);
					manager.getUserFDRDAO().saveList(MassSpecScorer.calculateFDR(results.scores), u);
					progress.setUpdateProgress(1);
					setUploadProgress(progress, session);
					//setUploadProgress(new UploadProgress(), session);
					setUploadResult(uploadResult, session);
					return null;
				}
			};
			
			new DaoManager<Void>(getSessionFactory()){}.execute(uploadCommand);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void parseFile(final String fileOnServer, final Long taxID, final Long role, final MS_ALGORITHMS alg, 
			final boolean useRandomForest, final SaintParameters saintParameters) throws FlashlightException {
		
		final HttpSession session = getHTTPSession();
		new Thread(new Runnable() {
		    public void run() {
		        parseFileHelper(fileOnServer, taxID, role, alg, useRandomForest, saintParameters, session);
		    }
		}).start();
		
	}

	private UploadResult doMapping(String fileOnServer, final UploadProgress progress,
			DaoManager<UploadResult> manager, Long taxID, User u, MS_ALGORITHMS alg, String sid, List<UploadRow> uploadRows, final HttpSession session) throws UploadIOException{
		final UploadResultCount resultCount = new UploadResultCount();
		final String server_filename = FileUtil.createPath(sid+"_"+fileOnServer);
		Parser parser = new SpotliteParser(server_filename, doc, new GenericCommand<Double>() {
			public void execute(Double d) {
				progress.setParsingProgress(d);
				setUploadProgress(progress, session);
			}					
		});
		
		//Determine which parser should be used
		/*if (alg == MS_ALGORITHMS.SAINT) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(server_filename)));
				if (br.readLine().split(Constants.SAINT_DELIMITER).length > SpotliteParser.Columns.values().length) {
					parser = new SAINTParser(server_filename, doc, new GenericCommand<Double>() {
						public void execute(Double d) {
							progress.setParsingProgress(d);
							setUploadProgress(progress);
						}					
					});
				}
				br.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			} 
		} */
		
		UploadResult uploadResult = parser.parse();
		FileUtil.deleteFile(server_filename);
		if (uploadResult.getErrors().size() > 0) return uploadResult; // Require 0 errors for upload			
		
		uploadRows.addAll(parser.getUploadRows());
		setCurrentUserID(u.getId(), session);

		Map<String, Set<Long>> map = manager.getGeneDAO().convertToGeneID(parser.getIds(), taxID, sid, 
				new GenericCommand<Double>() {
			public void execute(Double d) {
				progress.setMappingProgress(d);
				setUploadProgress(progress, session);
			}					
		});

		/** assertion **/
		/*if (map.keySet().size() != parser.ids.size()) {
			uploadResult.addError(new UploadError(0,0,"",new FlashlightException("ID Mapping Error")));
			return uploadResult;
		}*/

		List<String> duplicates = manager.getExperimentDAO().getDuplicateExperiments(u.getId(),
				parser.getName2Experiment().keySet(), sid);

		uploadResult = parser.addErrorsForDuplicateExperiments(duplicates);
		if (uploadResult.getErrors().size() > 0) return uploadResult; // Require 0 errors for upload


		Set<Long> mappedBaits = new HashSet<Long>();
		Map<String, Set<Long>> notMappedBaits = new HashMap<String,Set<Long>>();
		Set<Long> mappedPreys = new HashSet<Long>();
		Map<String, Set<Long>> notMappedPreys = new HashMap<String,Set<Long>>();
		Set<String> mappedUniqueInteractions = new HashSet<String>();
		Set<String> notMappedUniqueInteractions = new HashSet<String>();
		Set<Long> indistinguishableIds = new HashSet<Long>();
		
		int i = 0;
		for (Experiment e : parser.getName2Experiment().values()) {
			Set<Long> bait_id = map.get(e.getBait().getOfficialSymbol());
			e.setUser(u);
			if (bait_id != null && bait_id.size() == 1 && !e.getExperimentType().getName().equalsIgnoreCase("C")) {
				e.getBait().setOfficialSymbol(null);
				e.getBait().setId(bait_id.iterator().next());	
				resultCount.numExperiments++;
				mappedBaits.add(e.getBait().getId());
			} else if (e.getExperimentType().getName().equalsIgnoreCase("C")) {					
				e.getBait().setId((-1L)*resultCount.numControls);
				resultCount.numControls++;
			} else {
				resultCount.numExperimentsNotMapped++;
				if (!notMappedBaits.containsKey(e.getBait().getOfficialSymbol()))
					notMappedBaits.put(e.getBait().getOfficialSymbol(), new HashSet<Long>());
				if (bait_id != null && bait_id.size() > 0) {
					notMappedBaits.get(e.getBait().getOfficialSymbol()).addAll(bait_id);
					indistinguishableIds.addAll(bait_id);
				}
				e.setBait(null);
			}
			manager.getExperimentDAO().save(e);
			if (i % 20 == 0) { //20, same as the JDBC batch size
		        //flush a batch of inserts and release memory:
		        manager.flush();
		        manager.clear();
			}
			i++;
		}
		
		if (manager.getExperimentDataDAO().checkForRandomInsertionBug(u.getId())) {
			System.err.println("Error: random insertions!!!!! NO!!!!!!!!!!");
			throw new UploadIOException(new Exception("An unexpected error occurred."));
		}
		
		resultCount.numBaits = mappedBaits.size();
		resultCount.numBaitsNotMapped = notMappedBaits.size();
		
		try{	
			i = 0;
			for (UploadRow r : parser.getUploadRows()) {
				Experiment e = parser.getName2Experiment().get(r.experimentID);				
				
				Set<Long> prey_id = map.get(r.preyID);
			
				String genePairHash = null;
				String id = null;
				
				if (e.getExperimentType() == doc.expTypeConstraint.valueInCollectionByString("T")) {
					try {
					if (e.getBait() == null) {
						resultCount.numInteractionsNotMapped++;
						if (prey_id == null) { 								//Bait didn't map - Prey didn't map
							notMappedUniqueInteractions.add(Conversion.doGenePairHash(r.preyID, r.baitID));
							if (!notMappedPreys.containsKey(r.preyID)) notMappedPreys.put(r.preyID, new HashSet<Long>());
						} else if (prey_id.size() > 1) { 					//Bait didn't map - Prey indistinguishable
							notMappedUniqueInteractions.add(Conversion.doGenePairHash(r.preyID, r.baitID));
							indistinguishableIds.addAll(prey_id);
							if (!notMappedPreys.containsKey(r.preyID)) notMappedPreys.put(r.preyID, new HashSet<Long>());
							notMappedPreys.get(r.preyID).addAll(prey_id);
						} else { 											//Bait didn't map - Prey good
							id =  prey_id.iterator().next().toString();
							mappedPreys.add(Long.valueOf(id));
							notMappedUniqueInteractions.add(Conversion.doGenePairHash(id, r.baitID));		
						}
					} else {
						if (prey_id == null) { 								//Bait good - Prey didn't map
							resultCount.numInteractionsNotMapped++;
							notMappedUniqueInteractions.add(Conversion.doGenePairHash(r.preyID, r.baitID));
							if (!notMappedPreys.containsKey(r.preyID)) notMappedPreys.put(r.preyID, new HashSet<Long>());
						} else if (prey_id.size() > 1) { 					//Bait good - Prey indistinguishable
							resultCount.numInteractionsNotMapped++;
							notMappedUniqueInteractions.add(Conversion.doGenePairHash(r.preyID, r.baitID));
							indistinguishableIds.addAll(prey_id);
							if (!notMappedPreys.containsKey(r.preyID)) notMappedPreys.put(r.preyID, new HashSet<Long>());
							notMappedPreys.get(r.preyID).addAll(prey_id);
						} else { 											//Bait good - Prey good
							id = prey_id.iterator().next().toString();
							if (!e.getBait().getId().toString().equals(id.toString())) {
								genePairHash = Conversion.doGenePairHash(e.getBait().getId().toString(), id.toString());
								mappedPreys.add(Long.valueOf(id));
								resultCount.numInteractions++;
								mappedUniqueInteractions.add(genePairHash);
							}
						}
					}
					} catch (Exception ex) {
						int x = 1;
					}
				} else { // Control experiment
					if (prey_id != null && prey_id.size() == 1) { // Prey good
						id = prey_id.iterator().next().toString();
					}
				}
				
				
				String preyNiceName = (id == null) ? r.preyID : id;
				
				ExperimentData ed = new ExperimentData();
				ed.setExperiment(e);
				if (id != null) {
					ed.setPrey((Gene) manager.getSession().load(Gene.class, Long.parseLong(id)));
				}
				ed.setPreyNiceName(preyNiceName);
				ed.setPreyUploadId(r.preyID);
				ed.setSpectralCount(r.sc);
				if (genePairHash != null) {
					ed.setGenePair((GenePair) manager.getSession().load(GenePair.class, genePairHash));
				}	
				manager.getExperimentDataDAO().save(ed);
				if (i % 20 == 0) { //20, same as the JDBC batch size
			        //flush a batch of inserts and release memory:
			        manager.flush();
			        manager.clear();
			        progress.setMappingProgress(.80 + .2*(i/(float)parser.getUploadRows().size()) );
					setUploadProgress(progress, session);
				}
				i++;
			}
			progress.setMappingProgress(1d);
			setUploadProgress(progress, session);

			resultCount.numPreys = mappedPreys.size();
			resultCount.numPreysNotMapped = notMappedPreys.size();
			resultCount.numUniqueInteractions = mappedUniqueInteractions.size();
			resultCount.numUniqueInteractionsNotMapped = notMappedUniqueInteractions.size();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			throw new UploadIOException(e);
		}
		
		Map<Long, String> id2sym = manager.getGeneDAO().getOfficialSymbols(indistinguishableIds, sid);

		uploadResult.setNotMappedFilename(writeNotMappedFile(notMappedBaits,notMappedPreys,sid,id2sym));
		uploadResult.setResultCount(resultCount);
		//manager.getExperimentDataDAO().bulkInsert(filename);
		//FileUtil.deleteFile(filename);
		return uploadResult;
	}


	private ClassifierResults calculateMassSpecScores(GenePairScore[] scores, 
			Long userID, final UploadProgress progress, long role, String sid, List<UploadRow> uploadRows) throws FlashlightException {

		Map<String,Map<String,Double>> bait2prey2score = new HashMap<String,Map<String,Double>>();
		for (UploadRow ur : uploadRows) {
			if (ur instanceof UploadRowSAINT) {
				UploadRowSAINT row = (UploadRowSAINT) ur;
				if (!bait2prey2score.containsKey(row.baitID)) 
					bait2prey2score.put(row.baitID, new HashMap<String,Double>());
				if (!bait2prey2score.get(row.baitID).containsKey(row.preyID)) 
					bait2prey2score.get(row.baitID).put(row.preyID, row.prob);
			}		
		}
		
		for (GenePairScore gps : scores) {
			if (bait2prey2score.containsKey(gps.getBaitNiceName()) && 
					bait2prey2score.get(gps.getBaitNiceName()).containsKey(gps.getPreyNiceName())) {
				gps.setMsScore(Math.max(gps.getMsScore(),bait2prey2score.get(gps.getBaitNiceName()).get(gps.getPreyNiceName())));
			}
			if (bait2prey2score.containsKey(gps.getPreyNiceName()) && 
					bait2prey2score.get(gps.getPreyNiceName()).containsKey(gps.getBaitNiceName())) {
				gps.setMsScore(Math.max(gps.getMsScore(),bait2prey2score.get(gps.getPreyNiceName()).get(gps.getBaitNiceName())));
			}
		}
		ClassifierResults results = new ClassifierResults(scores);
		
		return results;
	}
	/**
	 * This function will iterate through a list of mass spec data scorers
	 * each one will take as input: exp2bait, exp2prey2sc, prey2length, ap2exp
	 * each one will output: gene_pair_hash2score
	 * this will be combined into a list of genePairScores (gene_pair_hash, HGSCore, CompPASS)
	 * afterwards, we will get the gene_pair data for each and use it as input to the RF classifier
	 * the classifier will return a gene_pair_hash2score
	 * we will then need to delete all scores  previously held by the user (crap, probably slow)
	 * we will then bulk insert the score data
	 */
	private ClassifierResults calculateMassSpecScores(DataForScoring dataForScoring, Long userID, final UploadProgress progress, 
			String sid, MS_ALGORITHMS alg, SaintParameters saintParameters, final HttpSession session) throws FlashlightException {
		Map<Long,String> exp2bait = dataForScoring.exp2bait;
		Map<Long,Map<String,Integer>> exp2prey2sc = dataForScoring.exp2prey2sc;
		Map<Long,String> c_exp2bait = dataForScoring.c_exp2bait;
		Map<Long,Map<String,Integer>> c_exp2prey2sc = dataForScoring.c_exp2prey2sc;
		Map<String,Integer> prey2length = dataForScoring.prey2length;
		Set<Long> expsForUser = dataForScoring.expsForUser;
		
		APMSControlDataset data = new APMSControlDataset(exp2bait,exp2prey2sc,prey2length,c_exp2bait,c_exp2prey2sc,expsForUser);
		//if (c_exp.size() > 0) data = new APMSControlDataset(exp2bait,exp2prey2sc,prey2length,c_exp,c_exp2prey2sc,expsForUser);
		//else data = new APMSDataset(exp2bait,exp2prey2sc,prey2length,expsForUser);
		

		/*try{
			//use buffering
			OutputStream file = new FileOutputStream("COMP.ser");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try{
				output.writeObject(data);
			}
			finally{
				output.close();
			}
		}  
		catch(Exception  ex){
			System.out.println(ex.getLocalizedMessage());
		}

		
		
		return null;*/
		
		
		MassSpecScorer scorer;
		switch (alg) {
			case HGSCore:
				scorer = new HGSCore(data);
				break;
			case CompPASS:
				scorer = new CompPASS(data);
				break;
			case SAINT:
				scorer = new SAINT(data,sid,saintParameters);
				break;
			default:
				scorer = new CompPASS(data);
		}
		
		ClassifierResults scores = scorer.calculateScores(new GenericCommand<Double>() {
			public void execute(Double d) {
				progress.setMSScoreProgress(d);
				setUploadProgress(progress, session);
			}					
		});

		return scores;
	}

	private String writeNotMappedFile(Map<String,Set<Long>> notMappedBaits, Map<String,Set<Long>> notMappedPreys, String sid,
			Map<Long,String> id2sym) {
		String filename = FileUtil.createPath(Constants.NOT_MAPPED_FILE+sid);
		try {
			FileWriter fstream = new FileWriter(filename, false);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Unmapped Baits\tIndistinguishable Genes: ID(Official Symbol)\n");
			for (String b : notMappedBaits.keySet()) {
				out.write(b);
				for (Long indistinguishable_id : notMappedBaits.get(b)) {
					out.write("\t" + indistinguishable_id.toString() + "(" + id2sym.get(indistinguishable_id) + ")");
				}
				out.write("\n");
			}
			out.write("Unmapped Prey\tIndistinguishable Genes\n");
			for (String p : notMappedPreys.keySet()) {
				out.write(p);
				for (Long indistinguishable_id : notMappedPreys.get(p)) {
					out.write("\t" + indistinguishable_id.toString() + "(" + id2sym.get(indistinguishable_id) + ")");
				}
				out.write("\n");
			}
			out.close();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return filename;
	}

}
