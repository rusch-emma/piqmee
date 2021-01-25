package piqmee.tree;

import beast.core.Description;

@Description("An NNN sequence representing an unsequenced incidence case.")
public class QuasiSpeciesIncidence {
    private final double samplingTime;
    private double[] attachmentTimes;
    // index of the next empty slot in the attachment times list
    private int currentAttachmentTimeIndex;
    private boolean attachmentTimesListChanged;
    private int count;

    // variables storing old and new attachment time if only a single one was changed via operators
    //  -1 if not changed
    private double oldTimeOfChangedCopy = -1;
    private double newTimeOfChangedCopy = -1;

    public QuasiSpeciesIncidence(double samplingTime, int count) {
        this.samplingTime = samplingTime;
        this.count = count;
        this.attachmentTimes = new double[count];
        this.currentAttachmentTimeIndex = 0;
        attachmentTimesListChanged = false;
    }

    public QuasiSpeciesIncidence(QuasiSpeciesIncidence quasiSpeciesIncidence) {
        this.samplingTime = quasiSpeciesIncidence.samplingTime;
        this.attachmentTimes = quasiSpeciesIncidence.attachmentTimes.clone();
        this.attachmentTimesListChanged = quasiSpeciesIncidence.attachmentTimesListChanged;
        this.currentAttachmentTimeIndex = quasiSpeciesIncidence.currentAttachmentTimeIndex;
        this.count = quasiSpeciesIncidence.count;
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
        this.currentAttachmentTimeIndex = attachmentTimes.length;
        attachmentTimesListChanged = true;
    }

    public void addAttachmentTime(double attachmentTime) {
        if (currentAttachmentTimeIndex < attachmentTimes.length - 1) {
            this.attachmentTimes[currentAttachmentTimeIndex++] = attachmentTime;
        }
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
     * Generates all missing attachment times for this incidence in equal step
     * from a specified lower bound (e.g. root) up to this incidence's sampling time.
     */
    public void generateAttachmentTimes(double lowerBound) {
        double step = (samplingTime - lowerBound) / (count - currentAttachmentTimeIndex);

        if (currentAttachmentTimeIndex == 0) {
            // if no attachment times exist initialise with the first step
            attachmentTimes[currentAttachmentTimeIndex++] = step;
        }

        while (currentAttachmentTimeIndex < count) {
            attachmentTimes[currentAttachmentTimeIndex] = attachmentTimes[currentAttachmentTimeIndex - 1] + step;
            currentAttachmentTimeIndex++;
        }
    }
}
