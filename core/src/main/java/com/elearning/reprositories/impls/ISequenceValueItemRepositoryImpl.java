package com.elearning.reprositories.impls;

import com.elearning.entities.SequenceValueItem;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.reprositories.ISequenceValueItemRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class ISequenceValueItemRepositoryImpl implements ISequenceValueItemRepositoryCustom {
    @Autowired
    protected ISequenceValueItemRepository sequenceValueItemRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized String getSequence(Class<?> forClass) {
        String sequenceName = forClass.getName();
        SequenceValueItem sequenceValueItem = sequenceValueItemRepository.findBySeqName(sequenceName);
        if (null == sequenceValueItem) {
            sequenceValueItem = new SequenceValueItem();
            sequenceValueItem.setSeqName(sequenceName);
            sequenceValueItem.setSeqId(1000);

            sequenceValueItem.setLastUpdatedStamp(new Date());
            sequenceValueItem.setCreatedStamp(new Date());
            sequenceValueItemRepository.save(sequenceValueItem);
            return "1000";
        }
        int sequenceId = sequenceValueItem.getSeqId() + 1;
        sequenceValueItem.setSeqId(sequenceId);
        sequenceValueItem.setLastUpdatedStamp(new Date());
        sequenceValueItemRepository.save(sequenceValueItem);
        return String.valueOf(sequenceId);
    }
}
