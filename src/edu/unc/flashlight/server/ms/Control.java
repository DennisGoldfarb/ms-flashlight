package edu.unc.flashlight.server.ms;


public class Control { /*extends MassSpecScorer {
	private Map<Long, Long> c_exp2bait;
	private Map<Long, Map<Long, Integer>> c_exp2prey2sc;
	private Map<Long, Set<Long>> c_ap2exp;
	private Map<Long,Integer> c_ap2count;
	private Map<Long,Map<Long,Integer>> c_ap2prey2tsc;
	private Map<Long,Map<Long,Map<Long,Integer>>> ap2bait2prey2tsc;
	private Map<Long,Map<Long,Integer>> ap2bait2count;
	private Map<Long,Set<Long>> bait2exp;
	
	public Control(Map<Long, Long> exp2bait, Map<Long, Map<Long, Integer>> exp2prey2sc, Map<Long, Set<Long>> ap2exp, 
			final List<String> hashes, Map<Long, Long> c_exp2bait, Map<Long, Map<Long, Integer>> c_exp2prey2sc, Map<Long, Set<Long>> c_ap2exp) {
		super(exp2bait, exp2prey2sc, ap2exp, hashes);
		this.c_ap2exp = c_ap2exp;
		this.c_exp2bait = c_exp2bait;
		this.c_exp2prey2sc = c_exp2prey2sc;
		c_ap2count = new HashMap<Long,Integer>();
		c_ap2prey2tsc = new HashMap<Long,Map<Long,Integer>>();
		ap2bait2prey2tsc = new HashMap<Long,Map<Long,Map<Long,Integer>>>();
		ap2bait2count = new HashMap<Long,Map<Long,Integer>>();
		bait2exp = new HashMap<Long,Set<Long>>();
	}

	public double[] calculateScores(GenericCommand<Double> updateProgress) {
		formatData();
		//updateProgress.execute(.05);
		scoreData();
		//updateProgress.execute(.1);
		for (Long ap : ap2pair2score.keySet()) {
			for (int i = 0; i < hashes.size(); i++) {
				String hash = hashes.get(i);
				if (ap2pair2score.get(ap).containsKey(hash)) {
					double val = ap2pair2score.get(ap).get(hash);
					scores[i] = Math.max(val, scores[i]);
					if (scores[i] == Double.NEGATIVE_INFINITY) {
						int wtf = 1;
					}
				}
			}
		}
		return scores;
	}
	
	private class ScoringLoop implements Runnable {
		private int index;
		private long ap;
		public ScoringLoop(int i,long ap) {
			index = i;
			this.ap = ap;
		}
		public void run() {
			Object[] hashes = ap2pair2score.get(ap).keySet().toArray();
			Map<String, Double> ap_pair2score = ap2pair2score.get(ap);
			for (int i = index; i < hashes.length; i+=NUM_THREADS) {
				String hash = hashes[i].toString();
				
				if (c_ap2count.size() == 0) {
					ap_pair2score.put(hash, 0D);
					continue;
				}
				
				Pair<Long,Long> ids = Conversion.undoGenePairHash(hash);
				float score1 = 0;
				float score2 = 0;
				Long g1 = ids.getFirst();
				Long g2 = ids.getSecond();

				if (bait2exp.containsKey(g1) && ap2bait2prey2tsc.get(ap).containsKey(g1) && 
						ap2bait2prey2tsc.get(ap).get(g1).containsKey(g2)) 
					score1 = rateratio(ap2bait2prey2tsc.get(ap).get(g1).get(g2),
							c_ap2prey2tsc.containsKey(ap) && c_ap2prey2tsc.get(ap).containsKey(g2) ? c_ap2prey2tsc.get(ap).get(g2)+1 : 1,
							ap2bait2count.get(ap).get(g1),
							c_ap2count.containsKey(ap) ? c_ap2count.get(ap) : 1);
				
				if (bait2exp.containsKey(g2) && ap2bait2prey2tsc.get(ap).containsKey(g2) && 
						ap2bait2prey2tsc.get(ap).get(g2).containsKey(g1)) 
					score2 = rateratio(ap2bait2prey2tsc.get(ap).get(g2).get(g1),
							c_ap2prey2tsc.containsKey(ap) && c_ap2prey2tsc.get(ap).containsKey(g1) ? c_ap2prey2tsc.get(ap).get(g1)+1 : 1,
							ap2bait2count.get(ap).get(g2),
							c_ap2count.containsKey(ap) ? c_ap2count.get(ap) : 1);
				
				//double score = Math.max(score1,score2);
				double score = 1-((1-score1)*(1-score2));

				if (c_exp2bait.keySet().size() == 0) score = 1;
				ap_pair2score.put(hash, score);
			}
		}
	}
	
	private float rateratio(int X1,int X2, int n1,int n2) {
		float pRR =  n1/((float)n1+n2);
		return (float) pbinom(X1,X1+X2,pRR);
	}
	
	private double pbinom(int q,int size,float prob) {
		double ple = 0;
		for (int i = 0; i <= q; i++)
			ple += comb(size, i) * Math.pow(prob,i) * Math.pow(1-prob,size-i);
		return ple;
	}
	
	private double comb(int n, int k) {
		return Math.exp(Gamma.logGamma(n+1) - Gamma.logGamma(k+1) - Gamma.logGamma(n-k+1));
	}
	
	private void scoreData() {
		for (Long ap : ap2pair2score.keySet()) {
			ExecutorService executor = Executors.newFixedThreadPool(6);
			
			for (int i = 0; i < NUM_THREADS; i++) {
				Runnable worker = new ScoringLoop(i,ap);
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
				
			}
		}
	}
	
	private void formatData() {
		for (Long ap : ap2exp.keySet()){
			ap2bait2prey2tsc.put(ap, new HashMap<Long,Map<Long,Integer>>());
			ap2pair2score.put(ap, new HashMap<String,Double>());
			ap2bait2count.put(ap, new HashMap<Long,Integer>());
			Map<Long,Map<Long,Integer>> bait2prey2tsc = ap2bait2prey2tsc.get(ap);
			Map<Long,Integer> bait2count = ap2bait2count.get(ap);
			for (Long exp : ap2exp.get(ap)) {
				Long bait = exp2bait.get(exp);
				if (!bait2exp.containsKey(bait)) bait2exp.put(bait, new HashSet<Long>());
				bait2exp.get(bait).add(exp);
				if (!bait2prey2tsc.containsKey(bait)) {
					bait2prey2tsc.put(bait, new HashMap<Long,Integer>());
					bait2count.put(bait,0);
				}
				bait2count.put(bait, bait2count.get(bait)+1);
				Map<Long,Integer> prey2sc = exp2prey2sc.get(exp);
				Map<Long,Integer> prey2tsc = bait2prey2tsc.get(bait);
				for (Long prey : prey2sc.keySet()) {
					if (!prey2tsc.containsKey(prey)) prey2tsc.put(prey, 0);
					prey2tsc.put(prey,prey2tsc.get(prey) + prey2sc.get(prey));
					String hash = Conversion.doGenePairHash(bait, prey);
					if (!ap2pair2score.get(ap).containsKey(hash) && !prey.equals(bait)) ap2pair2score.get(ap).put(hash, 0D);
				}
			}
		}
		for (Long ap : c_ap2exp.keySet()){
			c_ap2prey2tsc.put(ap, new HashMap<Long,Integer>());
			c_ap2count.put(ap, 0);
			Map<Long,Integer> prey2tsc = c_ap2prey2tsc.get(ap);
			for (Long exp : c_ap2exp.get(ap)) {
				c_ap2count.put(ap,c_ap2count.get(ap) + 1);
				
				Map<Long,Integer> prey2sc = c_exp2prey2sc.get(exp);
				for (Long prey : prey2sc.keySet()) {
					if (!prey2tsc.containsKey(prey)) prey2tsc.put(prey, 0);
					prey2tsc.put(prey,prey2tsc.get(prey) + prey2sc.get(prey));
				}
			}
		}
	}*/
}