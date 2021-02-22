package test.piqmee.distributions;

import beast.core.Description;
import beast.core.parameter.RealParameter;
import org.junit.Test;
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
    private QuasiSpeciesBirthDeathSkylineModel getQSBDSkyModel(QuasiSpeciesTree tree, RealParameter origin, boolean conditionOnSurvival,
                                                               RealParameter birth, RealParameter death, RealParameter sampling) {
        QuasiSpeciesBirthDeathSkylineModel model = new QuasiSpeciesBirthDeathSkylineModel();

        model.setInputValue("tree", tree);
        model.setInputValue("origin", origin);
        model.setInputValue("conditionOnSurvival", conditionOnSurvival);

        // test without rate change
        model.setInputValue("birthRate", birth);
        model.setInputValue("deathRate", death);
        model.setInputValue("samplingRate", sampling);

        model.initAndValidate();
        model.printTempResults = true;

        return model;
    }

    private QuasiSpeciesTree getQuasiSpeciesTree() {
        String newick = "(((t3 : 2, t4 : 1) : 3, t0 : 1) : 1 , (t1 : 2, t2 : 1) : 3);";
        return QuasiSpeciesTestCase.setTreeFromNewick(newick, new String[]{"N", "N", "N", "A", "C"});
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
        QuasiSpeciesBirthDeathSkylineModel qsbdSkyModel = getQSBDSkyModel(tree, origin, conditionOnSurvival, birth, death, sampling);

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
        QuasiSpeciesBirthDeathSkylineModel qsbdSkyModel = getQSBDSkyModel(tree, origin, conditionOnSurvival, birth, death, sampling);

        double actualLogCount = 0 + Math.log(3.0) + 0 + Math.log(3.0) + Math.log(3.0) + Math.log(1.0);
        double expectedLogCount = qsbdSkyModel.logNumberOfIncidenceTrees(tree);

        assertEquals(expectedLogCount, actualLogCount, 1e-5);
    }
}
