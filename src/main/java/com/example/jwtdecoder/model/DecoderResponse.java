package com.example.jwtdecoder.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DecoderResponse {
	String message;
	String header;
	String payload;
}
