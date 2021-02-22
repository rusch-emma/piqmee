package piqmee.tree;

import beast.core.Description;

import java.util.ArrayList;
import java.util.Collections;

@Description("An NNN sequence representing an unsequenced incidence case.")
public class QuasiSpeciesIncidence {
    private final double samplingTime;
    // list of attachment times associated with this incidence; always keep this sorted in ascending order
    private ArrayList<Double> attachmentTimes;
    private ArrayList<Double> storedAttachmentTimes;

    private boolean attachmentTimesListChanged;
    private int count;

    private boolean isRhoSampled;

    // variables storing old and new attachment time if only a single one was changed via operators
    //  -1 if not changed
    private double oldTimeOfChangedCopy = -1;
    private double newTimeOfChangedCopy = -1;

    public QuasiSpeciesIncidence(double samplingTime, int count) {
        this.samplingTime = samplingTime;
        this.count = count;
        this.attachmentTimes = new ArrayList<>(count);
        attachmentTimesListChanged = false;
        this.isRhoSampled = false;
    }

    public QuasiSpeciesIncidence(QuasiSpeciesIncidence quasiSpeciesIncidence) {
        this.samplingTime = quasiSpeciesIncidence.samplingTime;
        this.attachmentTimes = (ArrayList<Double>) quasiSpeciesIncidence.attachmentTimes.clone();
        this.attachmentTimesListChanged = quasiSpeciesIncidence.attachmentTimesListChanged;
        this.count = quasiSpeciesIncidence.count;
        this.isRhoSampled = quasiSpeciesIncidence.isRhoSampled;
    }

    public double getSamplingTime() {
        return samplingTime;
    }

    public ArrayList<Double> getAttachmentTimes() {
        return attachmentTimes;
    }

    public ArrayList<Double> getAttachmentTimesAndReset() {
        attachmentTimesListChanged = false;
        return getAttachmentTimes();
    }

    /**
     * Set attachment times associated with this incidence and sort by ascending order.
     */
    public void setAttachmentTimes(ArrayList<Double> attachmentTimes) {
        this.attachmentTimes = attachmentTimes;
        Collections.sort(this.attachmentTimes);
        attachmentTimesListChanged = true;
    }

    public void addAttachmentTime(double attachmentTime) {
        if (!attachmentTimes.contains(attachmentTime))
            attachmentTimes.add(attachmentTime);
        Collections.sort(attachmentTimes);
    }

    public boolean attachmentTimesListChanged() {
        return attachmentTimesListChanged;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount(int count) {
        this.count += count;
    }

    public void setRhoSampled(boolean isRhoSampled) {
        this.isRhoSampled = isRhoSampled;
    }

    public boolean isRhoSampled() {
        return isRhoSampled;
    }

    public double getOldTimeOfChangedCopy() {
        return oldTimeOfChangedCopy;
    }

    public void setOldTimeOfChangedCopy(double oldTimeOfChangedCopy) {
        this.oldTimeOfChangedCopy = oldTimeOfChangedCopy;
    }

    public double getNewTimeOfChangedCopy() {
        return newTimeOfChangedCopy;
    }

    public void setNewTimeOfChangedCopy(double newTimeOfChangedCopy) {
        this.newTimeOfChangedCopy = newTimeOfChangedCopy;
    }

    public void store() {
        storedAttachmentTimes = new ArrayList<>(attachmentTimes);
    }

    public void restore() {
        ArrayList<Double> tmp = new ArrayList<>(attachmentTimes);
        attachmentTimes = storedAttachmentTimes;
        storedAttachmentTimes = tmp;
    }

    /**
     * Generates all missing attachment times for this incidence in equal step
     * from a specified upper bound (e.g. root) up to this incidence's sampling time.
     */
    public void generateAttachmentTimes(double upperBound) {
        if (attachmentTimes.size() < count) {
            double step = (upperBound - samplingTime) / (count - attachmentTimes.size());
            attachmentTimes.add(step);

            for (int i = attachmentTimes.size(); i < count; i++) {
                attachmentTimes.add(attachmentTimes.get(i - 1) + step);
            }
        }

        Collections.sort(attachmentTimes);
    }
}
