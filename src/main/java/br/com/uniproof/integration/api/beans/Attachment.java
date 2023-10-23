package br.com.uniproof.integration.api.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attachment {
    @EqualsAndHashCode.Include
    private String id;
    private String ownerType;
    private String ownerId;
    private String parentId;
    private Long currentVersion;
    private Date removedAt;
    private Date expiresAt;
    private Date createdAt;
    private Date updatedAt;
    private Document document;
    private AttachmentType attachmentType;
    private Set<Attachment> children = new HashSet<>();
    private Attachment parent;

    public static Stream<Attachment> flatten(Attachment ex) {
        if (ex.getChildren() == null || ex.getChildren().isEmpty()) {
            return Stream.of(ex);
        }
        return Stream.concat(Stream.of(ex),
                ex.getChildren().stream().flatMap(Attachment::flatten));
    }
}
