package com.bituan.payjor.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnauthorizedException extends RuntimeException{
    String message;
}
