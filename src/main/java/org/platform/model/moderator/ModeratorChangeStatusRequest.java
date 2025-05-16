package org.platform.model.moderator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ModeratorChangeStatusRequest {
    private UUID moderatorId;
    private boolean isAdmin;
}
