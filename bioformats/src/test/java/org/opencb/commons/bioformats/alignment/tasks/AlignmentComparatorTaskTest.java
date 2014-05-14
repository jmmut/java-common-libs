package org.opencb.commons.bioformats.alignment.tasks;

import org.junit.Test;
import org.opencb.commons.bioformats.alignment.Alignment;
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
 * Date: 5/14/14
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentComparatorTaskTest extends GenericTest {
    @Test
    public void fromSam() {
        AlignmentSamDataReader alignmentSamDataReader = new AlignmentSamDataReader("/home/josemi/Documents/alignments/small.sam");
        AlignmentSamDataReader alignmentSamDataReader2 = new AlignmentSamDataReader("/home/josemi/Documents/alignments/small.sam");

        List<Task<Alignment>> tasks = new LinkedList<>();
        AlignmentComparatorTask alignmentComparatorTask = new AlignmentComparatorTask(alignmentSamDataReader2, 1);
        tasks.add(alignmentComparatorTask);

        List<DataWriter<Alignment>> writers = new LinkedList<>();
        Runner<Alignment> runner = new Runner<>(alignmentSamDataReader, writers, tasks, 100);
        try {
            runner.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
