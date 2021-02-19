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
@Description("Randomly selects an attachment time from a randomly selected incidence "
        + "and moves it uniformly in an interval restricted by the closest previous "
        + "and next attachment times around the selected attachment time.")
public class QuasiSpeciesIncidenceAttachmentUniform extends QuasiSpeciesTreeOperator {
    @Override
    public void initAndValidate() {
        super.initAndValidate();
        if (qsTree.getIncidences().length == 0) {
            System.out.println("In QuasiSpeciesIncidenceAttachmentUniform operator --- "
                    + "there are no incidences. The QuasiSpeciesIncidenceAttachmentUniform "
                    + "operator cannot be used. Please remove it from your xml file.");
        }
    }

    /**
     * Change the attachment time.
     * Log Hastings ratio = log(1) = 0 since the interval is fixed around the selected attachment time to change.
     * @return log of Hastings ratio
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
        ArrayList<Double> old = new ArrayList<>(attachmentTimes);
        int randAttIdx = Randomizer.nextInt(attachmentTimes.size());

        // select interval around random attachment time to change
        int minIdx = randAttIdx == 0 ? 0 : randAttIdx - 1;
        int maxIdx = randAttIdx == attachmentTimes.size() - 1 ? attachmentTimes.size() - 1 : randAttIdx + 1;

        // randomly choose new time in selected interval
        double newTime = Double.NaN;
        // randomly choose new attachment time which does not already exist
        while (Double.isNaN(newTime) || attachmentTimes.contains(newTime))
            newTime = ThreadLocalRandom.current().nextDouble(attachmentTimes.get(minIdx), attachmentTimes.get(maxIdx));

        randIncidence.setOldTimeOfChangedCopy(attachmentTimes.get(randAttIdx));
        attachmentTimes.set(randAttIdx, newTime);
        randIncidence.setNewTimeOfChangedCopy(newTime);

        randIncidence.setAttachmentTimes(attachmentTimes);

        return 0.0;
    }
}
