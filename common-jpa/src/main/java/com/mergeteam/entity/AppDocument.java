package com.mergeteam.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "app_document")
public class AppDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFileId;
    private String docName;
    @Basic(fetch = FetchType.LAZY)
    private byte[] binaryFile;
    private String mimeType;
    private Long fileSize;
}
