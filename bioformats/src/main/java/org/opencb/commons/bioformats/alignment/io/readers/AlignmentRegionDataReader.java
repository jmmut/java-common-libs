package org.opencb.commons.bioformats.alignment.io.readers;

import net.sf.samtools.SAMFileHeader;
import org.opencb.commons.bioformats.alignment.Alignment;
import org.opencb.commons.bioformats.alignment.AlignmentRegion;
import org.opencb.commons.bioformats.alignment.io.readers.AlignmentDataReader;
import org.opencb.commons.io.DataReader;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 2/3/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentRegionDataReader implements DataReader<AlignmentRegion> {

    private AlignmentDataReader<SAMFileHeader> alignmentDataReader;
    private Alignment prevAlignment;
    private int chunkSize;  //Max number of alignments in one AlignmentRegion.
    private int maxSequenceSize; //Maximum size for the total sequence. Count from the start of the first alignment to the end of the last alignment.

    private static final int defaultChunkSize = 2000;
    private static final int defaultMaxSequenceSize = 100000;

    public AlignmentRegionDataReader(AlignmentDataReader<SAMFileHeader> alignmentDataReader){
        this(alignmentDataReader, defaultChunkSize);
    }

    public AlignmentRegionDataReader(AlignmentDataReader<SAMFileHeader> alignmentDataReader, int chunkSize){
        this(alignmentDataReader, chunkSize, defaultMaxSequenceSize);
    }
    public AlignmentRegionDataReader(AlignmentDataReader<SAMFileHeader> alignmentDataReader, int chunkSize, int maxSequenceSize){
        this.alignmentDataReader = alignmentDataReader;
        this.prevAlignment = null;
        this.chunkSize = chunkSize;
        this.maxSequenceSize = maxSequenceSize;
    }


    @Override
    public boolean open() {
        alignmentDataReader.open();
        return true;
    }

    @Override
    public boolean close() {
        alignmentDataReader.close();
        return true;
    }

    @Override
    public boolean pre() {
        alignmentDataReader.pre();
        return true;
    }

    @Override
    public boolean post() {
        alignmentDataReader.post();
        return true;
    }

    @Override
    public AlignmentRegion read() {
        List<Alignment> alignmentList = new LinkedList<>();
        String chromosome;
        long start;
        long end;   //To have the correct "end" value,
        boolean isEnd = false;
        boolean overlappedEnd = true;

        //First initialisation
        if(prevAlignment == null){
            prevAlignment = alignmentDataReader.read();
            if(prevAlignment == null){  //Empty source
                return null;
            }
        }

        //Properties for the whole AlignmentRegion
        chromosome = prevAlignment.getChromosome();
        start = prevAlignment.getStart();
        if((prevAlignment.getFlags() & Alignment.SEGMENT_UNMAPPED) == 0){
            end = prevAlignment.getEnd();
        } else {
            end = start;
        }

        for(int i = 0; i < chunkSize; i++){
            alignmentList.add(prevAlignment);   //The prevAlignment is ready to be added.
            if((prevAlignment.getFlags() & Alignment.SEGMENT_UNMAPPED) == 0){
                end = prevAlignment.getEnd();  //Update the end only if is a valid segment.
            }

            //Read new alignment.
            prevAlignment = alignmentDataReader.read();

            //First stop condition: End of the chromosome or file
            if(prevAlignment == null || !chromosome.equals(prevAlignment.getChromosome())){
                isEnd = true;
                overlappedEnd = false;
                break;  //Break when read alignments from other chromosome or if is the last element
            }

            //Second stop condition: Too big Region.
            if((prevAlignment.getFlags() & Alignment.SEGMENT_UNMAPPED) == 0){   //If it's a Mapped segment
                if((prevAlignment.getEnd() - start) > maxSequenceSize ){
                    if( prevAlignment.getStart() > alignmentList.get(i).getEnd()){
                        //The start of the prevAlignment doesn't overlap with the end of the last inserted Alignment
                        overlappedEnd = false;
                    }
                    break;
                }
            }
        }
        if(prevAlignment != null){
            System.out.println("(prevAlignment.getEnd() - start) = " +(prevAlignment.getEnd() - start) + " overlappedEnd = " + overlappedEnd + " isEnd = " + isEnd);
            //System.out.println("(alignmentList.get(alignmentList(size)-1).getEnd()) = " + (alignmentList.get(alignmentList.size()-1).getEnd()) + " start " + start + " i " + i);
            System.out.println("(alignmentList.get(alignmentList(size)-1).getEnd() - start) = " + (alignmentList.get(alignmentList.size()-1).getEnd() - start));
        }


        AlignmentRegion alignmentRegion = new AlignmentRegion(alignmentList);
        alignmentRegion.setChromosomeTail(isEnd);//If we get the last alignment in chromosome or in source
        alignmentRegion.setChromosomeTail(!overlappedEnd);//If we get the last alignment in chromosome or in source
        alignmentRegion.setOverlapEnd(overlappedEnd);

        alignmentRegion.setStart(start);
        alignmentRegion.setEnd(end);

//        for(Alignment al : alignmentList){        //Depuration
//            for(byte b : al.getReadSequence()){
//                System.out.print((char) b);
//            }
//            System.out.println( " <<< " + al.getStart());
//        }


//        System.out.println("Leidos " + alignmentRegion.getAlignments().size() +
//                " Start: " + alignmentRegion.getAlignments().get(0).getStart() +
//                " End " + alignmentRegion.getAlignments().get(alignmentRegion.getAlignments().size()-1).getStart()  +
//                " Size " +  (alignmentRegion.getAlignments().get(alignmentRegion.getAlignments().size()-1).getStart()-alignmentRegion.getAlignments().get(0).getStart() )
//
//        );
        return alignmentRegion;
    }


    @Override
    public List<AlignmentRegion> read(int batchSize) {
        List<AlignmentRegion> alignmentRegionList = new LinkedList<>();
//        for(int i = 0; i < batchSize; i++){
//            alignmentRegionList.add(read());
//        }
        AlignmentRegion alignmentRegion;
        for(int i = 0; i < batchSize; i++){
            alignmentRegion = read();
            if(alignmentRegion != null){
                alignmentRegionList.add(alignmentRegion);
            }
        }
        return alignmentRegionList;
    }



    public int getMaxSequenceSize() {
        return maxSequenceSize;
    }

    public void setMaxSequenceSize(int maxSequenceSize) {
        this.maxSequenceSize = maxSequenceSize;
    }
}
