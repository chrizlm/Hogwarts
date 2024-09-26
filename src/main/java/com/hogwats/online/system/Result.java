package com.hogwats.online.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Result {
    private boolean flag; //true: success  false: fail
    private Integer code; //http status code
    private String message; //message
    private Object data; //actual payload
}
