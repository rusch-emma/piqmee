package quasispeciestree.evolution.branchratemodel;

import beast.core.Description;
import beast.evolution.branchratemodel.BranchRateModel;
import beast.evolution.branchratemodel.RateStatistic;
import beast.evolution.tree.Node;
import beast.math.statistic.DiscreteStatistics;
import quasispeciestree.tree.QuasiSpeciesNode;
import quasispeciestree.tree.QuasiSpeciesTree;

/**
 *  @author Veronika Boskova created on 08/02/2018
 */
@Description("Makes sure the summary statistic is calculated properly also for QS trees ")
public class QuasiSpeciesRateStatistic extends RateStatistic {

    protected QuasiSpeciesTree tree = null;
    protected BranchRateModel branchRateModel = null;
    protected boolean internal = true;
    protected boolean external = true;

    final static int MEAN = 0;
    final static int VARIANCE = 1;
    final static int COEFFICIENT_OF_VARIATION = 2;

    // Empty constructor as required:
    public QuasiSpeciesRateStatistic() { };

    /**
     * calculate the three statistics from scratch *
     */
    @Override
    public double[] calcValues() {
        // literal copy from RateStatistic besides lines TODO & TODO //
        int length = 0;
        int offset = 0;
        int offsetForBranchLengthsAndRates = 0;

        final int nrOfLeafs = tree.getLeafNodeCount();
        final int nrOfQSDupl = tree.getTotalAttachmentCounts();
        final int nrOfLeafsAndQSDupl = nrOfQSDupl + nrOfLeafs;

        if (external) {
            length += nrOfLeafsAndQSDupl;
        }
        if (internal) {
            length += tree.getInternalNodeCount() - 1 + nrOfQSDupl;
        }

        // these are the rates for each branch as in the full tree when reconstructed from the QS tree
        final double[] rates = new double[length];
        // these are the rates for each branch length, where branch length for the tip is the total
        //  branch length spanned by the haplotype
        final double[] ratesForBranchLengths = new double[length];
        final double[] internalQSBranchLengths = new double[nrOfLeafs];
        final double[] internalQSBranchRates = new double[nrOfLeafs];
        // need those only for mean
        final double[] branchLengths = new double[length];

        final Node[] nodes = tree.getNodesAsArray();

        /** handle leaf nodes **/
        if (external) {
            int k = 0;
            for (int i = 0; i < nrOfLeafs; i++) {
                final QuasiSpeciesNode child = (QuasiSpeciesNode) nodes[i];
                double rate = branchRateModel.getRateForBranch(child);
                final Node parent = child.getParent();

                int haploAboveChild = child.getHaploAboveName();
                double[] attachTimes = child.getAttachmentTimesList();
                int attachTimeArrayLength = attachTimes.length;
                // count here only the QS subtrees without the partial branch above it!!!
                // it can be that there are no duplicates, so the branch is from the child to the parent
                if (haploAboveChild != -1 && attachTimeArrayLength == 1) {
                    branchLengths[i] = parent.getHeight() - child.getHeight();
                    rates[i] = rate;
                }
                // and now for each "external branch" from QS duplicates
                else {
                    double[] tipTimes = child.getTipTimesList();
                    branchLengths[i] = child.getTotalBranchLengths() - attachTimes[0] - tipTimes[0];

                    // the first external branch can be till the real internal node or till the next attachment time
                    if (parent.getHeight() > attachTimes[attachTimeArrayLength - 1]) {
                        branchLengths[i] += attachTimes[attachTimeArrayLength - 1] - tipTimes[0];
                        internalQSBranchLengths[i] = attachTimes[0] - attachTimes[attachTimeArrayLength - 1];
                    }
                    else {
                        branchLengths[i] += parent.getHeight() - tipTimes[0];
                        internalQSBranchLengths[i] = attachTimes[0] - parent.getHeight();
                    }
                    internalQSBranchRates[i] = rate;
                    for (int q = 0; q < attachTimeArrayLength; q++) {
                        rates[k + q] = rate;
                    }
                    k += attachTimeArrayLength;
                }
                ratesForBranchLengths[i] = rate;
            }
            offset = nrOfLeafsAndQSDupl;
            offsetForBranchLengthsAndRates = nrOfLeafs;
        }

        /** handle internal nodes **/
        if (internal) {
            final int n = tree.getNodeCount();
            int k = offset;
            int l = offsetForBranchLengthsAndRates;
            // contribution from the real internal nodes only
            for (int i = nrOfLeafs; i < n; i++) {
                final QuasiSpeciesNode child = (QuasiSpeciesNode) nodes[i];
                //orig-root branch has rate 1 --- defined in the relaxed clock models --- so do not include here
                if (!child.isRoot()) {
                    double parentHeight = child.getParent().getHeight();
                    int haploAboveChild = child.getHaploAboveName();
                    int haploContChild = child.getContinuingHaploName();
                    double rate = branchRateModel.getRateForBranch(child);
                    // Case 1: branch can be free - i.e. no haplo passing, branch has its rate
                    if (haploContChild == -1) {
                        branchLengths[l] = parentHeight - child.getHeight();
                        ratesForBranchLengths[l] = rate;
                        rates[k] = rate;
                        l++;
                    }
                    // Case 2: it can be that a haplo starts above this node, in that case
                    //          only the partial branch has the branch's rate
                    else if (haploAboveChild != -1) {
                        branchLengths[l] = parentHeight - ((QuasiSpeciesNode) tree.getNode(haploAboveChild)).getAttachmentTimesList()[0];
                        ratesForBranchLengths[l] = rate;
                        rates[k] = rate;
                        l++;
                    }
                    // Case 3: it can be that a haplo passes this branch, in that case the rate is the same as for the
                    //          corresponding tip branch rate
                    else {
                        rates[k] = branchRateModel.getRateForBranch(tree.getNode(haploContChild));
                    }
                    k++;
                }
            }
        }

        final double[] values = new double[3];
        double totalWeightedRate = 0.0;
        double totalTreeLength = 0.0;

        for (int i = 0; i < rates.length ; i++) {
            totalWeightedRate += ratesForBranchLengths[i] * branchLengths[i];
            totalTreeLength += branchLengths[i];
        }
        for (int i = 0; i < internalQSBranchLengths.length ; i++) {
            totalWeightedRate += internalQSBranchRates[i] * internalQSBranchLengths[i];
            totalTreeLength += internalQSBranchLengths[i];
        }

        // from here as in original RateStatistic class //
        values[MEAN] = totalWeightedRate / totalTreeLength;
        // Q2R why not?
        //  final double mean = DiscreteStatistics.mean(rates);
        //        values[VARIANCE] = DiscreteStatistics.variance(rates, mean);
        //        values[COEFFICIENT_OF_VARIATION] = Math.sqrt(D values[VARIANCE]) / mean;
        values[VARIANCE] = DiscreteStatistics.variance(rates);
        final double mean = DiscreteStatistics.mean(rates);
        values[COEFFICIENT_OF_VARIATION] = Math.sqrt(DiscreteStatistics.variance(rates, mean)) / mean;
        return values;
    }

}
