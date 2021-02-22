package piqmee.operators;

import beast.core.Description;
import beast.core.Input;
import beast.util.Randomizer;
import piqmee.tree.QuasiSpeciesIncidence;
import piqmee.util.BeastRandomGenerator;
import piqmee.util.RandomGenerator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Emma Rusch
 */
@Description("Scales all attachment times of a randomly selected incidence by a specified scale factor.")
public class QuasiSpeciesIncidencesScale extends QuasiSpeciesTreeOperator {
    public Input<Double> scaleFactorInput = new Input<>("scaleFactor",
            "Scaling is restricted to the range [1/scaleFactor, scaleFactor]");

    private final RandomGenerator random;

    public QuasiSpeciesIncidencesScale() {
        this(new BeastRandomGenerator());
    }

    public QuasiSpeciesIncidencesScale(RandomGenerator random) {
        this.random = random;
    }

    @Override
    public void initAndValidate() {
        super.initAndValidate();
        if (qsTree.getIncidences().length == 0) {
            System.out.println("In QuasiSpeciesIncidencesScale operator --- "
                    + "there are no incidences. The QuasiSpeciesIncidencesScalce "
                    + "operator cannot be used. Please remove it from your xml file.");
        }
        if (scaleFactorInput.get() == null) {
            throw new IllegalArgumentException("You need to input valid scaleFactorInput value." +
                    "It is set to null at the moment");
        }
    }

    @Override
    public double proposal() {
        qsTree.startEditing(null);

        QuasiSpeciesIncidence[] incidences = qsTree.getIncidences();

        if (incidences.length == 0) {
            return 0.0;
        }

        // choose random incidence
        int randIncIdx = Randomizer.nextInt(incidences.length);
        QuasiSpeciesIncidence randIncidence = incidences[randIncIdx];
        ArrayList<Double> attachmentTimes = randIncidence.getAttachmentTimes();

        // choose scale factor
        double u = random.getNext();
        double f = u * scaleFactorInput.get() + (1.0 - u) / scaleFactorInput.get();

        // keep track of Hastings ratio
        double logf = Math.log(f);
        double logHastingsRatio = 0.0;

        // abort if attachment times would be < sampling time or >= origin
        if (attachmentTimes.get(0) * f < randIncidence.getSamplingTime() ||
                attachmentTimes.get(attachmentTimes.size() - 1) >= origin.getValue()) {
            return Double.NEGATIVE_INFINITY;
        }

        // scale all attachment times
        for (int i = 0; i < attachmentTimes.size(); i++) {
            attachmentTimes.set(i, attachmentTimes.get(i) * f);
            logHastingsRatio += logf;
        }

        return logHastingsRatio;
    }
}
