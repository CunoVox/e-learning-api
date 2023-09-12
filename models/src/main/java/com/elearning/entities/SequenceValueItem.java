package com.elearning.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "sequence_value_item")
public class SequenceValueItem implements Serializable {

    @Id
    @Field("SEQ_NAME")
    private String seqName;

    @Field("SEQ_ID")
    private Integer seqId;

    @Field("LAST_UPDATED_STAMP")
    private Date lastUpdatedStamp;

    @Field("LAST_UPDATED_TX_STAMP")
    private Date lastUpdatedTxStamp;

    @Field("CREATED_STAMP")
    private Date createdStamp;

    @Field("CREATED_TX_STAMP")
    private Date createdTxStamp;

}
