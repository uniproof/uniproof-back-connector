package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.util.Date;

@Data
public class AttachmentType {
    private String id;
    private String name;
    private String label;
    private String notaryLabel;
    private Boolean standard;
    private Boolean visible;
    private Boolean forwardable;
    private Boolean registered;
    private String icon;
    private String color;
    private Integer expirationDays;
}
