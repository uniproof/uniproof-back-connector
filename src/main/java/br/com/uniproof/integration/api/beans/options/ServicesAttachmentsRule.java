package br.com.uniproof.integration.api.beans.options;

import br.com.uniproof.integration.api.beans.Attachment;
import br.com.uniproof.integration.api.beans.LotItem;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ServicesAttachmentsRule {
    private Integer weight;
    private Set<Long> services;
    private Set<Long> rootType;
    private Set<Long> attachments;
    private Long uploadAs;
    private BehaviorEnum ifAlreadyProcessed;
    private List<Attachment> attCandidateList;
    private LotItem lotItem;

    private Map<String, Object> customSetup;

    public enum BehaviorEnum {
        SKIP,
        REPLACE,
        ATTACH_NEW
    }

}
