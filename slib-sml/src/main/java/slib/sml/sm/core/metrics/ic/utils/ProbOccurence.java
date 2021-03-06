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
package slib.sml.sm.core.metrics.ic.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.openrdf.model.URI;
import slib.sml.sm.core.metrics.ic.topo.ICtopo;
import slib.sml.sm.core.engine.SM_Engine;
import slib.utils.ex.SLIB_Ex_Critic;

/**
 *
 * @author Sébastien Harispe <sebastien.harispe@gmail.com>
 */
public class ProbOccurence implements ICtopo {

    /**
     * @param nbOccurrence
     * @param addToOccurrence
     * @return the probability of occurrence of all URIs specified in the given map.
     * @throws SLIB_Ex_Critic
     */
    public static Map<URI, Double> compute(Map<URI, Integer> nbOccurrence, int addToOccurrence) throws SLIB_Ex_Critic {

        long max = Collections.max(nbOccurrence.values());
        Map<URI, Double> results = new HashMap<URI, Double>();

        // we add to the max the number of occurrences added to each nodes

        max += addToOccurrence;

        double prob_occ;

        for (URI v : nbOccurrence.keySet()) {
            prob_occ = (double) (nbOccurrence.get(v) + addToOccurrence) / max;
            results.put(v, prob_occ);
        }

        return results;
    }

    @Override
    public Map<URI, Double> compute(IC_Conf_Topo conf, SM_Engine manager)
            throws SLIB_Ex_Critic {
        return compute(manager.getAllNbDescendantsInc(), 0);
    }
}
