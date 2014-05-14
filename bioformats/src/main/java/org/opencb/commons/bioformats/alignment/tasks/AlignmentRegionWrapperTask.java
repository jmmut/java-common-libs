package org.opencb.commons.bioformats.alignment.tasks;

import org.opencb.commons.bioformats.alignment.Alignment;
import org.opencb.commons.bioformats.alignment.AlignmentRegion;
import org.opencb.commons.run.Task;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper to use AlignmentRegions with Tasks that only allows Alignments.
 * User: jmmut
 * Date: 5/14/14
 * Time: 4:45 PM
 */
public class AlignmentRegionWrapperTask extends Task<AlignmentRegion> {

    private Task<Alignment> task;

    public AlignmentRegionWrapperTask(Task<Alignment> task) {
        this.task = task;
    }

    @Override
    public boolean pre() {
        return task.pre();
    }

    @Override
    public boolean post() {
        return task.post();
    }
    @Override
    public boolean apply(List<AlignmentRegion> batch) throws IOException {
        for(AlignmentRegion ar : batch){
            if(!task.apply(ar.getAlignments())){
                return false;
            }
        }
        return true;
    }
}
