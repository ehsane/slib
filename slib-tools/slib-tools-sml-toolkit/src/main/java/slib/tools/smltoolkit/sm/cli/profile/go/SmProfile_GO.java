/*

 Copyright or © or Copr. Ecole des Mines d'Alès (2012) 

 This software is a computer program whose purpose is to 
 process semantic graphs.

 This software is governed by the CeCILL  license under French law and
 abiding by the rules of distribution of free software.  You can  use, 
 modify and/ or redistribute the software under the terms of the CeCILL
 license as circulated by CEA, CNRS and INRIA at the following URL
 "http://www.cecill.info". 

 As a counterpart to the access to the source code and  rights to copy,
 modify and redistribute granted by the license, users are provided only
 with a limited warranty  and the software's author,  the holder of the
 economic rights,  and the successive licensors  have only  limited
 liability. 

 In this respect, the user's attention is drawn to the risks associated
 with loading,  using,  modifying and/or developing or reproducing the
 software by the user in light of its specific status of free software,
 that may mean  that it is complicated to manipulate,  and  that  also
 therefore means  that it is reserved for developers  and  experienced
 professionals having in-depth computer knowledge. Users are therefore
 encouraged to load and test the software's suitability as regards their
 requirements in conditions enabling the security of their systems and/or 
 data to be ensured and,  more generally, to use and operate it in the 
 same conditions as regards security. 

 The fact that you are presently reading this means that you have had
 knowledge of the CeCILL license and that you accept its terms.

 */
package slib.tools.smltoolkit.sm.cli.profile.go;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slib.sglib.algo.graph.utils.GraphActionExecutor;
import slib.tools.smltoolkit.SmlModuleCLI;
import slib.tools.smltoolkit.sm.cli.utils.SML_SM_module_XML_block_conf;
import slib.tools.smltoolkit.sm.cli.utils.XMLConfUtils;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;
import slib.utils.impl.Util;

/**
 *
 * @author Sébastien Harispe
 */
public class SmProfile_GO implements SmlModuleCLI {

    Logger logger = LoggerFactory.getLogger(SmProfile_GO.class);
    public String xmlconf;

    /**
     *
     * @param args
     * @throws SLIB_Exception
     */
    @Override
    public void execute(String[] args) throws SLIB_Exception {
        SmProfileGOHandler c = new SmProfileGOHandler(args);
        try {

            SML_SM_module_XML_block_conf smconf = c.getSmconf();
            boolean performGroupwise = smconf.mtype.equals(SmProfileGOCst.MTYPE_GROUPWISE);

            logger.info("Parameters");
            logger.info("---------------------------------------------------------------");
            logger.info("mType        : " + smconf.mtype);
            logger.info("Ontology     : " + smconf.ontologyPath);
            logger.info("Aspect       : " + smconf.aspect);
            
            if (performGroupwise) {
                logger.info("Annots       : " + smconf.annotsPath);
                logger.info("Annot Format : " + smconf.annotsFormat);
                logger.info("notfound     : " + smconf.notFound);
                logger.info("noannots     : " + smconf.noAnnots);
                logger.info("filter       : " + smconf.filter);
            }
            logger.info("Queries      : " + smconf.queries);
            logger.info("Output       : " + smconf.output);
            logger.info("pm           : " + smconf.pmShortFlag);
            logger.info("ic           : " + smconf.icShortFlag);

            if (performGroupwise) {
                logger.info("gm           : " + smconf.gmShortFlag);
            }

            logger.info("quiet        : " + Util.stringToBoolean(smconf.quiet));
            logger.info("threads      : " + smconf.threads);
            logger.info("notrgo       : " + Util.stringToBoolean(smconf.notrgo));
            logger.info("nonotrannots : " + Util.stringToBoolean(smconf.notrannots));
            logger.info("---------------------------------------------------------------");



            if (smconf.ontologyPath == null) {
                throw new SLIB_Ex_Critic("Please precise the location of the ontology");
            }
            if (smconf.queries == null) {
                throw new SLIB_Ex_Critic("Please precise the location of the queries");
            }
            if (smconf.output == null) {
                throw new SLIB_Ex_Critic("Please precise the location of the output file");
            }

            try {
                if (Integer.parseInt(smconf.threads) < 1) { //NumberFormatException will be thrown if not valid
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new SLIB_Ex_Critic("Please correct the number of threads allocated");
            }

            smconf.setGraphURI("http://g/");

            //Build XML File
            // Ontology TAG
            xmlconf = "<sglib>\n";

            xmlconf += "\t<opt  threads = \"" + smconf.threads + "\"  />\n\n";

            xmlconf += "\t<namespaces>\n\t\t<nm prefix=\"GO\" ref=\"" + smconf.graphURI + "\" />\n\t</namespaces>\n\n";
            xmlconf += "\t<graphs>    \n";
            xmlconf += "\t\t<graph uri=\"" + smconf.graphURI + "\"  >    \n";
            xmlconf += "\t\t\t<data>\n";
            xmlconf += "\t\t\t\t<file format=\"OBO\"   path=\"" + smconf.ontologyPath + "\"/>    \n";

            if (smconf.annotsPath != null) {

                if (smconf.annotsFormat == null || smconf.annotsFormat.equals("GAF2")) {
                    smconf.setAnnotsFormat("GAF2");
                    xmlconf += "\t\t\t\t<file format=\"" + smconf.annotsFormat + "\"   path=\"" + smconf.annotsPath + "\"/>    \n";
                } else if (smconf.annotsFormat.equals("TSV")) {
                    // no prefixObject because the string will contain a PREFIX, e.g. GO:XXXXXX
                    xmlconf += "\t\t\t\t<file format=\"TSV_ANNOT\"   path=\"" + smconf.annotsPath + "\" prefixSubject=\"" + smconf.graphURI + "\" header=\"false\"/>    \n";
                } else {
                    throw new SLIB_Ex_Critic("Unsupported file format " + smconf.annotsFormat);
                }

            }
            xmlconf += "\t\t\t</data>\n\n";

            String actions = "";

            String goAspectValue;
            String actionValue = "VERTICES_REDUCTION";
            if (smconf.aspect == null || smconf.aspect.equals("BP")) {
                goAspectValue = smconf.graphURI + "0008150";

            } else if (smconf.aspect.equals("MF")) {

                goAspectValue = smconf.graphURI + "0003674";

            } else if (smconf.aspect.equals("CC")) {

                goAspectValue = smconf.graphURI + "0005575";
            } else if (smconf.aspect.equals("GLOBAL")) {

                goAspectValue = GraphActionExecutor.REROOT_UNIVERSAL_ROOT_FLAG;
                actionValue = "REROOTING";

            } else { // expect custom=<GO term id>
                String[] data = smconf.aspect.split("=");
                if (data.length != 2) {
                    throw new SLIB_Ex_Critic("Cannot process the value " + smconf.aspect + " as a valid aspect for the GO");
                }
                goAspectValue = data[1];
                goAspectValue = goAspectValue.trim();
            }
            actions += "\t\t\t\t<action type=\"" + actionValue + "\" root_uri=\"" + goAspectValue + "\" />\n";

            if (!Util.stringToBoolean(smconf.notrgo)) {
                actions += "\t\t\t\t<action type=\"TRANSITIVE_REDUCTION\" target=\"CLASSES\" />\n";
            }
            if (smconf.annotsPath != null && !Util.stringToBoolean(smconf.notrannots)) {
                actions += "\t\t\t\t<action type=\"TRANSITIVE_REDUCTION\" target=\"INSTANCES\" />\n";
            }

            if (!actions.isEmpty()) {
                xmlconf += "\t\t\t<actions>\n" + actions + "\t\t\t</actions>\n";
            }
            xmlconf += "\t\t</graph>    \n";
            xmlconf += "\t</graphs>\n\n";

            if (smconf.filter != null) {
                if (!smconf.annotsFormat.equals("GAF_2")) {
                    throw new SLIB_Ex_Critic("Filtering can only be performed on annotation file of type GAF_2");
                }
                xmlconf += "\t<filters>\n" + XMLConfUtils.buildSML_FilterGAF2_XML_block(smconf.filter) + "\t</filters>\n";
            }


            xmlconf += XMLConfUtils.buildSML_SM_module_XML_block(smconf);

            xmlconf += "</sglib>\n";

            logger.info("XML configuration file generated");
            logger.info(xmlconf);
            logger.info("---------------------------------------------------------------");

        } catch (Exception e) {
            c.ending(e.getMessage(), true, false, true);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }
}
