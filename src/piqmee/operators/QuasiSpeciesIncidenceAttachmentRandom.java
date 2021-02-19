package piqmee.operators;

import beast.core.Description;
import beast.util.Randomizer;
import piqmee.tree.QuasiSpeciesIncidence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Emma Rusch
 */
@Description("Randomly selects an attachment time from a randomly selected incidence, "
        + "selects a new interval by randomly selecting another attachment time as the "
        + "lower bound and the next attachment time above it as the upper bound and "
        + "attaches the selected attachment time uniformly in that interval.")
public class QuasiSpeciesIncidenceAttachmentRandom extends QuasiSpeciesTreeOperator {
    @Override
    public void initAndValidate() {
        super.initAndValidate();
        if (qsTree.getIncidences().length == 0) {
            System.out.println("In QuasiSpeciesIncidenceAttachmentRandom operator --- "
                    + "there are no incidences. The QuasiSpeciesIncidenceAttachmentRandom "
                    + "operator cannot be used. Please remove it from your xml file.");
        }
    }

    /**
     * Change the attachment time.
     *
     * @return Log Hastings ratio = log((1 / (oldLowerBound - oldUpperBound)) / (1 / (newLowerBound - newUpperBound)))
     */
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

        // choose random attachment time from incidence
        ArrayList<Double> attachmentTimes = randIncidence.getAttachmentTimes();
        int randAttIdx = Randomizer.nextInt(attachmentTimes.size());

        // choose random interval: lower bound = random, upper bound = next time above lower bound
        int minIdx = -1;
        // randomly choose lower bound != selected attachment time
        while (minIdx == -1 || minIdx == randAttIdx)
            minIdx = Randomizer.nextInt(attachmentTimes.size() - 1);
        int maxIdx = minIdx + 1;
        double tMin = attachmentTimes.get(minIdx);
        double tMax = attachmentTimes.get(maxIdx);

        // randomly choose new time in interval
        double newTime = ThreadLocalRandom.current().nextDouble(tMin, tMax);

        // if attachment time already exists, put between bounds
        if (attachmentTimes.contains(newTime))
            newTime = (tMax + tMin) / 2;

        // pick interval around selected incidence
        int oldMinIdx = randAttIdx == 0 ? 0 : randAttIdx - 1;
        int oldMaxIdx = randAttIdx == attachmentTimes.size() - 1 ? attachmentTimes.size() - 1 : randAttIdx + 1;
        double oldTmin = attachmentTimes.get(oldMinIdx);
        double oldTmax = attachmentTimes.get(oldMaxIdx);

        // set new attachment time
        randIncidence.setOldTimeOfChangedCopy(attachmentTimes.get(randAttIdx));
        attachmentTimes.set(randAttIdx, newTime);
        randIncidence.setNewTimeOfChangedCopy(newTime);

        randIncidence.setAttachmentTimes(attachmentTimes);

        // return log Hastings ratio
        return -Math.log(oldTmax - oldTmin) + Math.log(tMax - tMin);
    }
}
