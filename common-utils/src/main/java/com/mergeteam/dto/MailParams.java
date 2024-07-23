package com.mergeteam.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class MailParams {
    private String id;
    private String emailTo;
}
