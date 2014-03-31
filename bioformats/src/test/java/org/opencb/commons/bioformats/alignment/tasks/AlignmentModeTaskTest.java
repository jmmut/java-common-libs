package org.opencb.commons.bioformats.alignment.tasks;


import org.junit.Test;
import org.opencb.commons.bioformats.alignment.AlignmentRegion;
import org.opencb.commons.bioformats.alignment.io.readers.AlignmentRegionDataReader;
import org.opencb.commons.bioformats.alignment.sam.io.AlignmentSamDataReader;
import org.opencb.commons.io.DataWriter;
import org.opencb.commons.run.Runner;
import org.opencb.commons.run.Task;
import org.opencb.commons.test.GenericTest;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jmmut
 * Date: 3/31/14
 * Time: 7:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentModeTaskTest extends GenericTest {
    @Test
    public void computeMode () {
        AlignmentSamDataReader alignmentSamDataReader = new AlignmentSamDataReader("/home/josemi/Documents/alignments/small.sam");
        AlignmentRegionDataReader alignmentRegionDataReader = new AlignmentRegionDataReader(alignmentSamDataReader, 900);


        List<Task<AlignmentRegion>> tasks = new LinkedList<>();
        AlignmentModeTask alignmentModeTask = new AlignmentModeTask();

        tasks.add(alignmentModeTask);

        List<DataWriter<AlignmentRegion>> writers = new LinkedList<>();
        Runner<AlignmentRegion> runner = new Runner<>(alignmentRegionDataReader, writers, tasks, 1);
        try {
            runner.run();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}