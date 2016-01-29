package quasispeciestree.tree;

import beast.core.Description;
import beast.evolution.tree.Node;

/**
 * @author Veronika Boskova created on 01/07/2015.
 */

@Description("A node in a quasi-species phylogenetic tree.")
public class QuasiSpeciesNode extends Node {


    @Override
    public void initAndValidate() throws Exception {

        super.initAndValidate();

    }

//    protected String haploAboveName;
//    protected String continuingHaploName;
    protected int haploAboveName;
    protected int continuingHaploName;

    /**
     * Obtain the quasi-species type/name, if any, starting on the branch above this node.
     *
     * @return quasi-species name
     */
//    public String getHaploAboveName() {return this.haploAboveName; }
    public int getHaploAboveName() {
        return this.haploAboveName;
    }

    /**
     * Sets the quasi-species starting on the branch above this node
     *
     * @param haploName New quasi-species name
     */
//    public void setHaploAboveName(String haploName) { this.haploAboveName = haploName; }
    public void setHaploAboveName(int haploName) { this.haploAboveName = haploName; }

    /**
     * Obtain the quasi-species type/name, of a haplotype that started earlier and continues on below this (internal) node.
     * Only non null (non -1) if this haplo arose at some previous node (so haploAboveName is non-null (non -1))
     *
     * @return quasi-species name
     */
//    public String getContinuingHaploName() { return this.continuingHaploName; }
    public int getContinuingHaploName() { return this.continuingHaploName; }

    /**
     * Sets the quasi-species haplotype as a continuing haplotype below this node
     *
     * @param haploName New quasi-species name
     */
//    public void setContinuingHaploName(String haploName) { this.continuingHaploName = haploName; }
    public void setContinuingHaploName(int haploName) { this.continuingHaploName = haploName; }


    /**
     * Set quasi-species tree for a copied node
     */

    public void setmTree(QuasiSpeciesTree tree) {
        this.m_tree = tree;
    }

    /**
     * @return shallow copy of node
     */
    public QuasiSpeciesNode shallowCopy() {
        QuasiSpeciesNode node = new QuasiSpeciesNode();
        node.height = height;
        node.setParent(this.getParent());
        if (getLeft()!=null) {
            node.setLeft(getLeft().copy());
            node.getLeft().setParent(node);
            if (getRight()!=null) {
                node.setRight(getRight().copy());
                node.getRight().setParent(node);
            }
        }

        node.haploAboveName = haploAboveName;
        node.continuingHaploName = continuingHaploName;

        node.labelNr = labelNr;
        node.metaDataString = metaDataString;
        node.ID = ID;

        return node;
    }

    /**
     * **************************
     * Methods ported from Node *
     ***************************
     */


    /**
     * @return (deep) copy of node
     */
    @Override
    public QuasiSpeciesNode copy() {
        QuasiSpeciesNode node = new QuasiSpeciesNode();
        node.height = height;
        node.labelNr = labelNr;
        node.metaDataString = metaDataString;
        node.setParent(null);
        node.ID = ID;

        node.haploAboveName = haploAboveName;
        node.continuingHaploName = continuingHaploName;

        if (getLeft()!=null) {
            node.setLeft(getLeft().copy());
            node.getLeft().setParent(node);
            if (getRight()!=null) {
                node.setRight(getRight().copy());
                node.getRight().setParent(node);
            }
        }
        return node;
    }

    /**
     * assign values from a tree in array representation *
     * @param nodes
     * @param node
     */
    @Override
    public void assignFrom(Node[] nodes, Node node) {
        height = node.getHeight();
        labelNr = node.getNr();
        metaDataString = node.metaDataString;
        setParent(null);
        ID = node.getID();

        QuasiSpeciesNode qsNode = (QuasiSpeciesNode)node;
        haploAboveName = qsNode.haploAboveName;
        continuingHaploName = qsNode.continuingHaploName;

        if (node.getLeft()!=null) {
            setLeft(nodes[node.getLeft().getNr()]);
            getLeft().assignFrom(nodes, node.getLeft());
            getLeft().setParent(this);
            if (node.getRight()!=null) {
                setRight(nodes[node.getRight().getNr()]);
                getRight().assignFrom(nodes, node.getRight());
                getRight().setParent(this);
            }
        }
    }

}
