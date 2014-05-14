package org.opencb.commons.bioformats.alignment.tasks;

import org.opencb.commons.bioformats.alignment.Alignment;
import org.opencb.commons.bioformats.alignment.io.readers.AlignmentDataReader;
import org.opencb.commons.run.Task;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jmmut
 * Date: 5/14/14
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentComparatorTask extends Task<Alignment> {
    private int currentAlignment;
    private int verbosity;  // TODO change to enum?
    private AlignmentDataReader alignmentDataReader;
    private int numAlignmentsFailed;

    public AlignmentComparatorTask(AlignmentDataReader alignmentDataReader) {
        this(alignmentDataReader, 0);
    }

    public AlignmentComparatorTask(AlignmentDataReader alignmentDataReader, int verbosity) {
        this.alignmentDataReader = alignmentDataReader;
        this.verbosity = verbosity;
        currentAlignment = 0;
        numAlignmentsFailed = 0;
    }

    @Override
    public boolean pre() {
        alignmentDataReader.open();
        alignmentDataReader.pre();
        return super.pre();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean post() {
        alignmentDataReader.post();
        alignmentDataReader.close();
        System.out.println("Number of Alignments NOT equal: " + numAlignmentsFailed);
        return super.post();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean apply(List<Alignment> batch) throws IOException {
        List read = alignmentDataReader.read(batch.size());
        if (read == null) {
            return false;
        }
        int batchFails = 0;
        Iterator<Alignment> batchIterator = batch.iterator();
        Iterator<Alignment> readIterator = read.iterator();
        Alignment batchAlignment, readAlignment;
        for (; batchIterator.hasNext() && readIterator.hasNext(); currentAlignment++) {
            batchAlignment = batchIterator.next();
            readAlignment = readIterator.next();

            if (!batchAlignment.equals(readAlignment)) {
                batchFails++;
                if (verbosity == 1) {
                    System.out.println("Alignment number: " + currentAlignment);
                    printEquals(batchAlignment, readAlignment);
                }
            }
        }

        numAlignmentsFailed += batchFails;

        return batchFails == 0;
    }

    public boolean printEquals (Alignment alignment1, Alignment alignment2){
        boolean areEqual = true;

        if (!alignment1.equals(alignment2)){
            if ((alignment1.getFlags() | Alignment.SEGMENTS_PROPERLY_ALIGNED | Alignment.SEQUENCE_REVERSE_COMPLEMENTED | Alignment.SECONDARY_ALIGNMENT |Alignment.SUPPLEMENTARY_ALIGNMENT)
                    != (alignment2.getFlags() | Alignment.SEGMENTS_PROPERLY_ALIGNED | Alignment.SEQUENCE_REVERSE_COMPLEMENTED | Alignment.SECONDARY_ALIGNMENT |Alignment.SUPPLEMENTARY_ALIGNMENT)) {
                return false;
            }
            if ((alignment1.getFlags() & Alignment.SEGMENT_UNMAPPED) == 0) {    // segment NOT unmapped
                if (!(alignment1.getStart() == alignment2.getStart())) {
                    areEqual = false;
                    System.out.println("Start is not equal: " + alignment1.getStart() + ", " + alignment2.getStart());
                }
                if (!(alignment1.getEnd() == alignment2.getEnd())) {
                    areEqual = false;
                    System.out.println("End is not equal: " + alignment1.getEnd() + " != " + alignment2.getEnd());
                }
                if (!(alignment1.getUnclippedStart() == alignment2.getUnclippedStart())) {
                    areEqual = false;
                    System.out.println("Unclipped Start is not equal: " + alignment1.getUnclippedStart() + " != " + alignment2.getUnclippedStart());
                }
                if (!(alignment1.getUnclippedEnd() == alignment2.getUnclippedEnd())) {
                    areEqual = false;
                    System.out.println("Unclipped End is not equal: " + alignment1.getUnclippedEnd() + " != " + alignment2.getUnclippedEnd());
                }
                if (!(alignment1.getMateReferenceName() == alignment2.getMateReferenceName())) {
                    areEqual = false;
                    System.out.println("MateReferenceName is not equal");
                }
                if (alignment1.getDifferences() == null) {
                    if (alignment2.getDifferences() != null) {
                        areEqual = false;
                        System.out.println(" origin Differences is null and dest is not null");
                    }
                } else {
                    if (alignment2.getDifferences() == null) {
                        areEqual = false;
                        System.out.println("origin Differences is not null and dest is null");
                    } else {
                        if (!alignment1.getDifferences().equals(alignment2.getDifferences())) {
                            areEqual = false;
                            System.out.println("Differences is not equal");
                        }
                    }
                }
                if (!(alignment1.getMappingQuality() == alignment2.getMappingQuality())) {
                    areEqual = false;
                    System.out.println("MappingQuality is not equal");
                }
                if (!(alignment1.getFlags() == alignment2.getFlags())) {
                    areEqual = false;
                    System.out.println("flags is not equal");
                }
            }
            if (!alignment1.getName().equals(alignment2.getName())) {
                areEqual = false;
                System.out.println("name is not equal");
            }

            if (!alignment1.getChromosome().equals(alignment2.getChromosome())) {
                areEqual = false;
                System.out.println("chromosome is not equal");
            }
            if (!(alignment1.getLength() == alignment2.getLength())) {
                areEqual = false;
                System.out.println("Length is not equal");
            }
            if (!alignment1.getQualities().equals(alignment2.getQualities())) {
                areEqual = false;
                System.out.println("qualities is not equal");
            }
            if (!(alignment1.getMateAlignmentStart() == alignment2.getMateAlignmentStart())) {
                areEqual = false;
                System.out.println("MateAlignmentStart not equal");
            }

///*
            if (alignment1.getReadSequence() == null ^ alignment2.getReadSequence() == null) { // only one is null
                areEqual = false;
                System.out.println("only one sequence is null: " + (alignment1.getReadSequence() == null) + ", " + (alignment2.getReadSequence() == null));
            } else if (alignment1.getReadSequence() != null && !Arrays.equals(alignment1.getReadSequence(), alignment2.getReadSequence())) {  // both are not null and different
                areEqual = false;
                System.out.println(alignment1.getReadSequence());
                System.out.println(alignment2.getReadSequence());
            }
/**/

            if (alignment1.getAttributes() == null) {
                if (alignment2.getAttributes() != null) {
                    areEqual = false;
                    System.out.println(" origin Attributes is null and dest is not null");
                }
            } else {
                if (alignment2.getAttributes() == null) {
                    areEqual = false;
                    System.out.println("origin Attributes is not null and dest is null");
                } else {
                    if (!alignment1.getAttributes().equals(alignment2.getAttributes())) {
                        areEqual = false;
                        System.out.println("Attributes is not equal");
                    }
                }
            }
        }
        if (!areEqual) {
            System.out.println("Alignments are different failed!");
        }
        return areEqual;
    }


    /**
     * Responde a : d1 equivalente a d2
     *
     * @param d1    Acepta conversión a match_mismatch.
     * @param d2
     * @return
     */
    boolean diffEquals(List<Alignment.AlignmentDifference> d1, List<Alignment.AlignmentDifference> d2){

        int index = 0;
        List<Alignment.AlignmentDifference> loosenedDifferences = new LinkedList<>();
        for (Alignment.AlignmentDifference alignmentDifference : d1) {
            if (alignmentDifference.getOp() == Alignment.AlignmentDifference.MATCH_MISMATCH
                    || alignmentDifference.getOp() == Alignment.AlignmentDifference.MISMATCH){
                loosenedDifferences.add(alignmentDifference);
            }
        }
















        return true;
    }

}
