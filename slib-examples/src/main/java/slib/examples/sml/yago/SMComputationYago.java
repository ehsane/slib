/* 
 *  Copyright or © or Copr. Ecole des Mines d'Alès (2012-2014) 
 *  
 *  This software is a computer program whose purpose is to provide 
 *  several functionalities for the processing of semantic data 
 *  sources such as ontologies or text corpora.
 *  
 *  This software is governed by the CeCILL  license under French law and
 *  abiding by the rules of distribution of free software.  You can  use, 
 *  modify and/ or redistribute the software under the terms of the CeCILL
 *  license as circulated by CEA, CNRS and INRIA at the following URL
 *  "http://www.cecill.info". 
 * 
 *  As a counterpart to the access to the source code and  rights to copy,
 *  modify and redistribute granted by the license, users are provided only
 *  with a limited warranty  and the software's author,  the holder of the
 *  economic rights,  and the successive licensors  have only  limited
 *  liability. 

 *  In this respect, the user's attention is drawn to the risks associated
 *  with loading,  using,  modifying and/or developing or reproducing the
 *  software by the user in light of its specific status of free software,
 *  that may mean  that it is complicated to manipulate,  and  that  also
 *  therefore means  that it is reserved for developers  and  experienced
 *  professionals having in-depth computer knowledge. Users are therefore
 *  encouraged to load and test the software's suitability as regards their
 *  requirements in conditions enabling the security of their systems and/or 
 *  data to be ensured and,  more generally, to use and operate it in the 
 *  same conditions as regards security. 
 * 
 *  The fact that you are presently reading this means that you have had
 *  knowledge of the CeCILL license and that you accept its terms.
 */
package slib.examples.sml.yago;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.openrdf.model.URI;
import slib.sglib.algo.graph.utils.GAction;
import slib.sglib.algo.graph.utils.GActionType;
import slib.sglib.algo.graph.validator.dag.ValidatorDAG;
import slib.sglib.io.conf.GDataConf;
import slib.sglib.io.conf.GraphConf;
import slib.sglib.io.loader.GraphLoaderGeneric;
import slib.sglib.io.util.GFormat;
import slib.sglib.model.graph.G;
import slib.sglib.model.impl.graph.memory.GraphMemory;
import slib.sglib.model.impl.repo.URIFactoryMemory;
import slib.sglib.model.repo.URIFactory;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Exception;
import slib.utils.impl.Timer;

/**
 *
 * @author Sébastien Harispe <sebastien.harispe@gmail.com>
 */
public class SMComputationYago {
    
    public static void main(String[] args) throws SLIB_Exception{
        
        /*
         * The data loading is composed of three steps:
         *  - (1) we load the yago taxonomy from the turtle file
         *  - (2) we type the vertices in order to specify the engine which are the vertices associated to classes (concepts)
         *       This treatment is required in order to perform some algorithms.
         *  - (3) We root vertices which are not rooted by owl:Thing. Algorithms require the processed graph to be connected
         *  i.e. to compute the Most Informative Common Ancestors of two concepts.
         * 
         * Notice that due to the size of the taxonomy, extra memory must be allocated to the JVM e.g. -Xmx3000m
         */
        
        Timer t = new Timer();
        t.start();
        
        String yagoTaxonomyFile = "/data/yago/yagoTaxonomy.ttl";
        
        URIFactory factory = URIFactoryMemory.getSingleton();
        URI yagoURI = factory.getURI("http://yago-knowledge.org/resource/");
        G g = new GraphMemory(yagoURI);
        
        // This is the configuration of the data 
        GDataConf dataConf = new GDataConf(GFormat.TURTLE, yagoTaxonomyFile);
        
        
        // We specify an action to root the vertices, typed as class without outgoing rdfs:subclassOf relationship 
        // Those vertices are linked to owl:Thing by an eddge x  rdfs:subClassOf owl:Thing 
        GAction actionRerootConf = new GAction(GActionType.REROOTING);
        
        // We now create the configuration we will specify to the generic loader
        GraphConf gConf = new GraphConf();
        gConf.addGDataConf(dataConf);
        gConf.addGAction(actionRerootConf);
        
        GraphLoaderGeneric.load(gConf,g);
        
        System.out.println(g.toString());
        
        // The taxonomy is now a rDAG, i.e. rooted Directed Acyclic Graph.
        // Check by yourself
        Set<URI> roots = new ValidatorDAG().getTaxonomicRoots(g);
        System.out.println("Roots: "+roots);
        
        
        // We compute the similarity between two concepts 
        URI wikiRugbyFoorballerURI = factory.getURI(yagoURI.stringValue() + "wikicategory_Rugby_footballers"); 
        URI WordnetSoccerPlayerURI = factory.getURI(yagoURI.stringValue() + "wordnet_soccer_player_110618342");

        // First we configure an intrincic IC 
        ICconf icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_DEPTH_MAX_NONLINEAR);
        // Then we configure the pairwise measure to use, we here choose to use Lin formula
        SMconf smConf = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);

        // We define the engine used to compute the similarity
        SM_Engine engine = new SM_Engine(g);


        double sim = engine.computePairwiseSim(smConf, wikiRugbyFoorballerURI, WordnetSoccerPlayerURI);
        System.out.println("Similarity: " + sim);

        /* 
         * Notice that the first computation is expensive as the engine compute the IC and extra information 
         * which are cached by the engine
         * Let's perform 10000 random computations (we only print some results).
         * We retrieve the set of vertices as a list
         */
        int totalComparison = 10000;

        List<URI> listVertices = new ArrayList<URI>(g.getV());
        int nbConcepts = listVertices.size();
        int id1, id2;
        URI c1, c2;
        String idC1, idC2;
        Random r = new Random();

        for (int i = 0; i < totalComparison; i++) {
            id1 = r.nextInt(nbConcepts);
            id2 = r.nextInt(nbConcepts);

            c1 = listVertices.get(id1);
            c2 = listVertices.get(id2);

            sim = engine.computePairwiseSim(smConf, c1, c2);

            if ((i + 1) % 1000 == 0) {
                idC1 = c1.getLocalName();
                idC2 = c2.getLocalName();

                System.out.println("Sim " + (i + 1) + "/" + totalComparison + "\t" + idC1 + "/" + idC2 + ": " + sim);
            }
        }
        t.stop();
        t.elapsedTime();
        
    }
    
}
