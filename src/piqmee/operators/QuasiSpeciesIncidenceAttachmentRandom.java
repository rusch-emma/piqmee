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
        if (qsTree.getIncidences().size() == 0) {
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
        if (qsTree.getIncidences().size() == 0) {
            return 0.0;
        }

        // choose random incidence
        List<String> incidenceTaxa = new ArrayList<>(qsTree.getIncidences().keySet());
        int randKey = Randomizer.nextInt(incidenceTaxa.size());
        QuasiSpeciesIncidence randIncidence = qsTree.getIncidences().get(incidenceTaxa.get(randKey));

        // choose random attachment time from incidence
        ArrayList<Double> attachmentTimes = randIncidence.getAttachmentTimes();
        int randIdx = Randomizer.nextInt(attachmentTimes.size());

        // choose random interval: lower bound = random, upper bound = next time above lower bound
        int minIdx = -1;
        // randomly choose lower bound != selected attachment time
        while (minIdx == -1 || minIdx == randIdx)
            minIdx = Randomizer.nextInt(attachmentTimes.size() - 1);
        int maxIdx = minIdx + 1;
        double tmin = attachmentTimes.get(minIdx);
        double tmax = attachmentTimes.get(maxIdx);

        // randomly choose new time in interval
        double newTime = Double.NaN;
        while (Double.isNaN(newTime) || attachmentTimes.contains(newTime))
            newTime = ThreadLocalRandom.current().nextDouble(tmin, tmax);

        // pick interval around selected incidence
        int oldMinIdx = randIdx == 0 ? 0 : randIdx - 1;
        int oldMaxIdx = randIdx == attachmentTimes.size() - 1 ? attachmentTimes.size() - 1 : randIdx + 1;
        double oldTmin = attachmentTimes.get(oldMinIdx);
        double oldTmax = attachmentTimes.get(oldMaxIdx);

        // set new attachment time
        randIncidence.setOldTimeOfChangedCopy(attachmentTimes.get(randIdx));
        attachmentTimes.set(randIdx, newTime);
        randIncidence.setNewTimeOfChangedCopy(newTime);

        randIncidence.setAttachmentTimes(attachmentTimes);

        // return log Hastings ratio
        return -Math.log(oldTmax - oldTmin) + Math.log(tmax - tmin);
    }
}
