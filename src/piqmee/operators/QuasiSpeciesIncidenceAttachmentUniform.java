package piqmee.operators;

import beast.util.Randomizer;
import piqmee.tree.QuasiSpeciesIncidence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuasiSpeciesIncidenceAttachmentUniform extends QuasiSpeciesTreeOperator {
    @Override
    public void initAndValidate() {
        super.initAndValidate();
        if (qsTree.getIncidences().size() == 0) {
            System.out.println("In QuasiSpeciesIncidenceAttachmentRandom operator --- "
            + "there are no incidences. The QuasiSpeciesIncidenceAttachmentRandom "
            + "operator cannot be used. Please remove it from your xml file.");
        }
    }

    @Override
    public double proposal() {
        if (qsTree.getIncidences().size() == 0) {
            return 0.0;
        }

        // choose random incidence
        List<String> incidenceTaxons = new ArrayList<>(qsTree.getIncidences().keySet());
        int randKey = Randomizer.nextInt(incidenceTaxons.size());
        QuasiSpeciesIncidence randIncidence = qsTree.getIncidences().get(incidenceTaxons.get(randKey));

        // choose random attachment time from incidence
        ArrayList<Double> attachmentTimes = randIncidence.getAttachmentTimes();
        int randIdx = Randomizer.nextInt(attachmentTimes.size());

        // select interval around random attachment time to change
        int minIdx = randIdx == 0 ? 0 : randIdx - 1;
        int maxIdx = randIdx == attachmentTimes.size() - 1 ? attachmentTimes.size() - 1 : randIdx + 1;

        // randomly choose new time in selected interval
        double newTime = ThreadLocalRandom.current().nextDouble(attachmentTimes.get(minIdx), attachmentTimes.get(maxIdx));
        while (attachmentTimes.contains(newTime))
            // if attachment time already exists, generate a new one
            newTime = ThreadLocalRandom.current().nextDouble(attachmentTimes.get(minIdx), attachmentTimes.get(maxIdx));
        attachmentTimes.set(randIdx, newTime);

        randIncidence.setOldTimeOfChangedCopy(attachmentTimes.get(randIdx));
        randIncidence.setNewTimeOfChangedCopy(newTime);

        randIncidence.setAttachmentTimes(attachmentTimes);

        return 0.0;
    }
}
