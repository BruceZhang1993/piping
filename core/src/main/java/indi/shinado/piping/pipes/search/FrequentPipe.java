package indi.shinado.piping.pipes.search;

import java.util.TreeSet;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;

public abstract class FrequentPipe extends SearchablePipe {

    protected TreeSet<Pipe> frequentItems = new TreeSet<>();

    protected FrequentMap mFrequentMap;
    
    public FrequentPipe(int id) {
        super(id);
        mFrequentMap = new FrequentMap();
    }

    public void removeFrequency(Pipe vo) {
        mFrequentMap.remove(vo.getExecutable());
        frequentItems.remove(vo);
    }

    public void addFrequency(Pipe item) {
        item.addFrequency();
        boolean bExist = mFrequentMap.addFrequency(item.getExecutable());
        if (!bExist) {
            frequentItems.add(item);
        }
    }

    /**
     * fulfill result with frequency
     */
    @Override
    protected void fulfill(Pipe vo, Instruction input) {
        super.fulfill(vo, input);
        Integer freq = mFrequentMap.get(vo.getExecutable());
        if (freq != null) {
            vo.setFrequency(freq);
            frequentItems.add(vo);
        }
    }

    @Override
    protected TreeSet<Pipe> search(Instruction value) {
        TreeSet<Pipe> result = super.search(value);
        if (value.input.isEmpty() ){
            for (Pipe frequent : frequentItems){
                result.add(frequent);
            }
        }
        return result;
    }

}
