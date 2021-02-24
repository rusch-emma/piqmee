package test.piqmee.distributions;

import beast.core.Description;
import beast.core.parameter.BooleanParameter;
import beast.core.parameter.RealParameter;
import beast.evolution.tree.Tree;
import beast.util.TreeParser;
import org.junit.Test;
import piqmee.distributions.BirthDeathSkylineModel;
import piqmee.distributions.QuasiSpeciesBirthDeathSkylineModel;
import piqmee.tree.QuasiSpeciesIncidence;
import piqmee.tree.QuasiSpeciesTree;
import test.piqmee.QuasiSpeciesTestCase;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * @author Emma Rusch
 */

@Description("Test the inclusion of incidence data in the QuasiSpeciesBirthDeathSkyModel with a small test dataset.")
public class QuasiSpeciesBDSkyIncidenceTests {
    private QuasiSpeciesBirthDeathSkylineModel getQSBDSkyModel(QuasiSpeciesTree tree, RealParameter origin,
                                                               boolean conditionOnSurvival, RealParameter birth,
                                                               RealParameter death, RealParameter sampling,
                                                               RealParameter rho, RealParameter rhoTimes) {
        QuasiSpeciesBirthDeathSkylineModel model = new QuasiSpeciesBirthDeathSkylineModel();

        model.setInputValue("tree", tree);
        model.setInputValue("origin", origin);
        model.setInputValue("conditionOnSurvival", conditionOnSurvival);

        model.setInputValue("birthRate", birth);
        model.setInputValue("deathRate", death);
        model.setInputValue("samplingRate", sampling);
        model.setInputValue("rho", rho);
        model.setInputValue("rhoSamplingTimes", rhoTimes);
        model.setInputValue("reverseTimeArrays", new BooleanParameter("false false false true false"));

        model.initAndValidate();
        model.printTempResults = true;

        return model;
    }

    private BirthDeathSkylineModel getBDSkyModel(Tree tree, RealParameter origin,
                                                 boolean conditionOnSurvival, RealParameter birth,
                                                 RealParameter death, RealParameter sampling,
                                                 RealParameter rho, RealParameter rhoTimes) {
        BirthDeathSkylineModel model = new BirthDeathSkylineModel();

        model.setInputValue("tree", tree);
        model.setInputValue("origin", origin);
        model.setInputValue("conditionOnSurvival", conditionOnSurvival);

        model.setInputValue("birthRate", birth);
        model.setInputValue("deathRate", death);
        model.setInputValue("samplingRate", sampling);
        model.setInputValue("rho", rho);
        model.setInputValue("rhoSamplingTimes", rhoTimes);
        model.setInputValue("reverseTimeArrays", new BooleanParameter("false false false true false"));

        model.initAndValidate();
        model.printTempResults = true;

        return model;
    }

    private QuasiSpeciesTree getQuasiSpeciesTree() {
        String newick = "(((t3 : 2, t4 : 1) : 3, t0 : 1) : 1 , (t1 : 2, t2 : 1) : 3);";
        return QuasiSpeciesTestCase.setTreeFromNewick(newick, new String[]{"N", "N", "N", "A", "C"});
    }

    private QuasiSpeciesTree getFullQuasiSpeciesTree() {
        String newick = "((t0 : 2, (t1 : 1, t2 : 1) : 1) : 3, (t3 : 1, t4 : 1) : 4);";
        return QuasiSpeciesTestCase.setTreeFromFullNewick(newick, new String[]{"A", "C", "G", "N", "N"});
    }

    private Tree getTree() {
        String newick = "((t0 : 2, (t1 : 1, t2 : 1) : 1) : 3, (t3 : 1, t4 : 1) : 4);";
        return new TreeParser(newick);
    }

    @Test
    public void testStoreIncidencesAndGenerateAttachmentTimes() {
        ArrayList<Double> actualSamplingTimes = new ArrayList<>();
        actualSamplingTimes.add(4.0);
        actualSamplingTimes.add(1.0);
        actualSamplingTimes.add(2.0);
        ArrayList<Double> actualAttachmentTimes = new ArrayList<>();
        actualAttachmentTimes.add(5.0);
        actualAttachmentTimes.add(3.0);
        actualAttachmentTimes.add(5.0);
        actualAttachmentTimes.add(2.0);
        actualAttachmentTimes.add(3.0);
        actualAttachmentTimes.add(4.0);

        QuasiSpeciesTree tree = getQuasiSpeciesTree();

        QuasiSpeciesIncidence[] incidences = tree.getIncidences();
        ArrayList<Double> expectedSamplingTimes = new ArrayList<>();
        ArrayList<Double> expectedAttachmentTimes = new ArrayList<>();

        for (QuasiSpeciesIncidence incidence : incidences) {
            expectedSamplingTimes.add(incidence.getSamplingTime());
            expectedAttachmentTimes.addAll(incidence.getAttachmentTimes());
        }

        assertEquals(expectedSamplingTimes, actualSamplingTimes);
        assertEquals(expectedAttachmentTimes, actualAttachmentTimes);
    }

    @Test
    public void testIncidenceLineageCountAtTime() {
        QuasiSpeciesTree tree = getQuasiSpeciesTree();

        final RealParameter origin = new RealParameter("7.0");
        final boolean conditionOnSurvival = false;
        final RealParameter birth = new RealParameter("2.0");
        final RealParameter death = new RealParameter("1.0");
        final RealParameter sampling = new RealParameter("0.5");
        final RealParameter rho = new RealParameter("0.5 0.5 0.5");
        final RealParameter rhoTimes = new RealParameter("4.0 1.0 2.0");

        QuasiSpeciesBirthDeathSkylineModel qsbdSkyModel = getQSBDSkyModel(tree, origin, conditionOnSurvival,
                                                                            birth, death, sampling, rho, rhoTimes);

        QuasiSpeciesIncidence[] incidences = tree.getIncidences();

        int expectedCount1 = qsbdSkyModel.incidenceLineageCountAtTime(incidences, 1.0);
        int expectedCount2 = qsbdSkyModel.incidenceLineageCountAtTime(incidences, 2.0);
        int expectedCount3 = qsbdSkyModel.incidenceLineageCountAtTime(incidences, 3.0);
        int expectedCount4 = qsbdSkyModel.incidenceLineageCountAtTime(incidences, 4.0);
        int expectedCount5 = qsbdSkyModel.incidenceLineageCountAtTime(incidences, 5.0);

        int actualCount1 = 0;
        int actualCount2 = 2;
        int actualCount3 = 2;
        int actualCount4 = 1;
        int actualCount5 = 0;

        assertEquals(expectedCount1, actualCount1);
        assertEquals(expectedCount2, actualCount2);
        assertEquals(expectedCount3, actualCount3);
        assertEquals(expectedCount4, actualCount4);
        assertEquals(expectedCount5, actualCount5);
    }

    @Test
    public void testLogNumberOfIncidenceTrees() {
        QuasiSpeciesTree tree = getQuasiSpeciesTree();

        final RealParameter origin = new RealParameter("7.0");
        final boolean conditionOnSurvival = false;
        final RealParameter birth = new RealParameter("2.0");
        final RealParameter death = new RealParameter("1.0");
        final RealParameter sampling = new RealParameter("0.5");
        final RealParameter rho = new RealParameter("0.5 0.5 0.5");
        final RealParameter rhoTimes = new RealParameter("4.0 1.0 2.0");

        QuasiSpeciesBirthDeathSkylineModel qsbdSkyModel = getQSBDSkyModel(tree, origin, conditionOnSurvival,
                                                                            birth, death, sampling, rho, rhoTimes);

        double actualLogCount = 0 + Math.log(3.0) + 0 + Math.log(3.0) + Math.log(3.0) + Math.log(1.0);
        double expectedLogCount = qsbdSkyModel.logNumberOfIncidenceTrees(tree);

        assertEquals(expectedLogCount, actualLogCount, 1e-5);
    }

    @Test
    public void testIncidencesContributionToTreeLogLikelihood() {
        final RealParameter origin = new RealParameter("7.0");
        final boolean conditionOnSurvival = false;
        final RealParameter birth = new RealParameter("2.0");
        final RealParameter death = new RealParameter("1.0");
        final RealParameter sampling = new RealParameter("0.5");
        final RealParameter rho = new RealParameter("0.5 0.5 0.5");
        final RealParameter rhoTimes = new RealParameter("4.0 1.0 2.0");

        QuasiSpeciesTree tree = getQuasiSpeciesTree();
        QuasiSpeciesBirthDeathSkylineModel qsBdSkyModel = getQSBDSkyModel(tree, origin, conditionOnSurvival,
                                                                            birth, death, sampling, rho, rhoTimes);

        // contribution without incidences, i.e. processFirstProductTerm + processMiddleTerm + processLastTerm
        double qsLogP = -18.571457786951814;

        // contribution from incidences
        double firstProductTerm = -29.611181748098154;
        double middleTerm = 0.0;
        double lastTerm = 0.0;
        double logNumberOfIncidenceTrees = qsBdSkyModel.logNumberOfIncidenceTrees(tree);

        double actualLogP = qsLogP + qsBdSkyModel.logNumberOfQSTrees(tree) +
                firstProductTerm + middleTerm + lastTerm + logNumberOfIncidenceTrees;

        double expectedLogP = qsBdSkyModel.calculateTreeLogLikelihood(tree);

        assertEquals(expectedLogP, actualLogP, 1e-5);
    }

    /**
     * Test the correct calculation of the contribution from incidences to the tree likelihood by calculating
     * the difference between the tree likelihood from the BirthDeathSkylineModel (i.e. without including the
     * contribution from incidences) and from the QuasiSpeciesBirthDeathSkylineModel (i.e. including the contribution
     * from incidences). If the calculation of the incidence contribution works as intended the difference should
     * be equal to the gamma factor, i.e. logNumberOfIncidenceTrees.
     */
    @Test
    public void testIncidencesContributionToTreeLogLikelihoodFromFullTree() {
        final RealParameter origin = new RealParameter("7.0");
        final boolean conditionOnSurvival = false;
        final RealParameter birth = new RealParameter("2.0");
        final RealParameter death = new RealParameter("1.0");
        final RealParameter sampling = new RealParameter("0.5");
        final RealParameter rho = new RealParameter("0.5 0.5 0.5");
        final RealParameter rhoTimes = new RealParameter("4.0 1.0 2.0");

        QuasiSpeciesTree qsTree = getFullQuasiSpeciesTree();
        QuasiSpeciesBirthDeathSkylineModel qsBdSkyModel = getQSBDSkyModel(qsTree, origin, conditionOnSurvival,
                                                                            birth, death, sampling, rho, rhoTimes);

        Tree normalTree = getTree();
        BirthDeathSkylineModel bdSkyModel = getBDSkyModel(normalTree, origin, conditionOnSurvival,
                                                            birth, death, sampling, rho, rhoTimes);

        // calculate tree likelihood for BDSkyModel
        double bdSkyLogP = bdSkyModel.calculateTreeLogLikelihood(normalTree);

        // calculate tree likelihood for QSBDSkyModel
        double qsBdSkyLogP = qsBdSkyModel.calculateTreeLogLikelihood(qsTree);

        double expectedLogPDelta = qsBdSkyLogP - bdSkyLogP;
        double actualLogPDelta = qsBdSkyModel.logNumberOfIncidenceTrees(qsTree);

        assertEquals(expectedLogPDelta, actualLogPDelta, 1e-5);
    }
}
