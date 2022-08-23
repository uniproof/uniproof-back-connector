package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AttachmentTypeRequest {
	private Integer attachmentTypeId;
	private String attachmentId;
	private String parentId;
	private String lotItemId;

}
