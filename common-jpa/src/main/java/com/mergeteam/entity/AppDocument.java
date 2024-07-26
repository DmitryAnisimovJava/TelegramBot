package com.mergeteam.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_document", schema = "telegram_data")
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
