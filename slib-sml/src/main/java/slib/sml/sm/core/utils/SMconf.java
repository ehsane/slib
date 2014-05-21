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
package slib.sml.sm.core.utils;

import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.i.Conf;

/**
 * Representation of the configuration of a semantic measure.
 *
 * @author Sébastien Harispe <sebastien.harispe@gmail.com>
 */
public class SMconf extends Conf {

    /**
     * The id which refers to the configuration (expected to be unique).
     */
    private String id;

    /**
     * The flag which specifies the measure associated to the configuration.
     */
    private String flag;

    /**
     * The name of the class which refers to the measure implementation.
     */
    private String className;

    /**
     * The label of the configuration.
     */
    private String label;

    /**
     * The information content associated to the measure (if any).
     */
    private ICconf icConf;

    /**
     * Build an instance of configuration considering the given flag. The id of
     * the configuration (which must be unique) will be set to the value of the
     * flag.
     *
     * @param flag the string which defines the measure associated to the
     * configuration.
     * @throws SLIB_Ex_Critic
     */
    public SMconf(String flag) throws SLIB_Ex_Critic {
        this(flag, flag);
    }

    /**
     * Build an instance of configuration considering the given id and flag.
     *
     * @param id the id of the configuration (expected to be unique).
     * @param flag the string which defines the measure associated to the
     * configuration.
     * @throws SLIB_Ex_Critic
     */
    public SMconf(String id, String flag) throws SLIB_Ex_Critic {
        this(id, flag, id);
    }

    /**
     * Build an instance of configuration considering the given id, flag and
     * label.
     *
     * @param id the id of the configuration (expected to be unique).
     * @param flag the string which defines the measure associated to the
     * configuration.
     * @param label the label associated to the configuration.
     *
     * @throws SLIB_Ex_Critic
     */
    public SMconf(String id, String flag, String label) throws SLIB_Ex_Critic {
        this(id, flag, label, null);
    }

    /**
     * Build an instance of configuration considering the given flag and
     * Information Content configuration.
     *
     * @param flag the flag defining the semantic measure method associated to
     * the configuration.
     * @param icConf the IC configuration associated to the configuration
     * @throws SLIB_Ex_Critic
     */
    public SMconf(String flag, ICconf icConf) throws SLIB_Ex_Critic {
        this(flag, flag, flag, icConf);
    }

    /**
     * Build an instance of configuration considering the given flag and
     * Information Content configuration.
     *
     * @param id the id of the configuration (must be unique).
     * @param flag the flag defining the semantic measure method associated to
     * the configuration.
     * @param icConf the IC configuration associated to the configuration
     * @throws SLIB_Ex_Critic
     */
    public SMconf(String id, String flag, ICconf icConf) throws SLIB_Ex_Critic {
        this(id, flag, id, icConf);
    }

    /**
     * Build an instance of configuration considering the given id, flag and
     * Information Content configuration.
     *
     * @param id the id of the configuration (must be unique).
     * @param flag the flag defining the semantic measure method associated to
     * the configuration.
     * @param label the label associated to the configuration
     * @param icConf the IC configuration associated to the configuration
     * @throws SLIB_Ex_Critic
     */
    public SMconf(String id, String flag, String label, ICconf icConf) throws SLIB_Ex_Critic {

        this.id = id;
        this.flag = flag;
        this.label = label;
        this.icConf = icConf;
        init();
    }

    /**
     * Initialize the measure configuration with regard to the specified
     * parameters.
     *
     * @throws SLIB_Ex_Critic
     */
    private void init() throws SLIB_Ex_Critic {

        this.className = SMConstants.semanticMeasureClassName(this.flag);

        if (this.className == null) {
            throw new SLIB_Ex_Critic("Cannot resolve Semantic measure associated to flag: " + flag);
        }
    }

    /**
     * The IC configuration associated to the configuration.
     *
     * @return the IC configuration associated to the current configuration
     */
    public ICconf getICconf() {
        return icConf;
    }

    /**
     * Mutator of the IC configuration.
     *
     * @param ic the new IC configuration to consider
     */
    public void setICconf(ICconf ic) {
        this.icConf = ic;
    }

    @Override
    public String toString() {
        String out = "id : " + id + "\n";
        out += "flag : " + flag + "\n";
        out += "className : " + className + "\n";
        out += "label : " + label + "\n";
        out += "icConf : \n" + icConf + "\n";
        out += "Extra parameters : " + super.toString();
        return out;
    }

    /**
     * Add the parameter to the configuration - override existing value if the
     * parameter already exists.
     *
     * @param p the parameter key
     * @param v the value associated
     * @return the configuration object from which the method is called (auto
     * complete feature)
     */
    @Override
    public SMconf addParam(String p, Object v) {
        super.addParam(p, v);
        return this;
    }

    /**
     * @return the id of the configuration
     */
    public String getId() {
        return id;
    }

    /**
     * @return the flag of the configuration
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @return the name of the class which is associated to the configuration
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the label of the configuration
     */
    public String getLabel() {
        return label;
    }

    /**
     * 
     * @param label the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 
     * @return the information content configuration
     */
    public ICconf getIcConf() {
        return icConf;
    }

}
