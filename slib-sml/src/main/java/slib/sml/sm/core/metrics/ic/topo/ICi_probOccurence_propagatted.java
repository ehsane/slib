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
package slib.sml.sm.core.metrics.ic.topo;

import java.util.Map;
import org.openrdf.model.URI;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ProbOccurence;
import slib.sml.sm.core.engine.SM_Engine;
import slib.utils.ex.SLIB_Ex_Critic;

/**
 * ICi_resnik_1995
 *
 * TODO Add resnik citation
 *
 * p(c) defined as number of top down fashion path leading to c from the
 * reachable leaves from c + 1 max_pc = max p(c) for all concepts. IC(c) = -log(
 * p(c) / max_pc)
 *
 * IC is normalize considering formula given by Faria and al in order to produce
 * results [0,1]. ﻿Faria D, Pesquita C, Couto FM, Falcão A: Proteinon: A web
 * tool for protein semantic similarity. 2007.
 *
 *
 * @author Sébastien Harispe <sebastien.harispe@gmail.com>
 *
 */
public class ICi_probOccurence_propagatted implements ICtopo {

    /**
     *
     * @param allNbPathLeadingToAllVertex
     * @return the IC of all URIs specified in the given map.
     * @throws SLIB_Ex_Critic
     */
    public Map<URI, Double> compute(Map<URI, Integer> allNbPathLeadingToAllVertex) throws SLIB_Ex_Critic {

        return ProbOccurence.compute(allNbPathLeadingToAllVertex, 0);
    }

    @Override
    public Map<URI, Double> compute(IC_Conf_Topo conf, SM_Engine manager)
            throws SLIB_Ex_Critic {
        return compute(manager.getnbPathLeadingToAllVertex());
    }
}
