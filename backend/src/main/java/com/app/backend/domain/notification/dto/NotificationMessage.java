package com.app.backend.domain.notification.dto;

import java.time.LocalDateTime;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {
    private String userId;
    private String title;
    private String content;
    private LocalDateTime timestamp;
    
    // Jackson이 LocalDateTime을 처리할 수 있도록
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
