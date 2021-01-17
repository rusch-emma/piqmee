package piqmee.tree;

import beast.core.Description;

import java.util.Objects;

@Description("An NNN sequence representing an unsequenced incidence case.")
public class QuasiSpeciesIncidence {
    private final double samplingTime;
    private double[] attachmentTimes;
    private boolean attachmentTimesListChanged;
    private int count;

    // variables storing old and new attachment time if only a single one was changed via operators
    //  -1 if not changed
    private double oldTimeOfChangedCopy = -1;
    private double newTimeOfChangedCopy = -1;

    public QuasiSpeciesIncidence(double samplingTime, int count) {
        this.samplingTime = samplingTime;
        this.count = count;
        attachmentTimesListChanged = false;
    }

    public double[] getAttachmentTimes() {
        return attachmentTimes;
    }

    public double[] getAttachmentTimesAndReset() {
        attachmentTimesListChanged = false;
        return getAttachmentTimes();
    }

    public void setAttachmentTimes(double[] attachmentTimes) {
        this.attachmentTimes = attachmentTimes;
        attachmentTimesListChanged = true;
    }

    public boolean isAttachmentTimesListChanged() {
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

    /**
     * Generates attachment times in equal steps
     * from 0 up to this incidence's sampling time - 1.
     */
    public void generateAttachmentTimes() {
        attachmentTimes = new double[count];
        double step = (samplingTime - 1) / count;
        attachmentTimes[count - 1] = step;
        for (int i = count - 2; i >= 0; i--) {
            attachmentTimes[i] = attachmentTimes[i + 1] + step;
        }
    }
}
