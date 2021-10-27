package com.example.jwtdecoder.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwtdecoder.model.DecoderResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@RestController
public class JwtTokenDecoder {
	private static String VERIFIED = "verified";
	@GetMapping("/decode")
	public DecoderResponse decoder(@RequestParam(name = "pubKey") String pubKey, @RequestParam(name = "accessToken") String token) {
		String[] chunks = token.split("\\.");
		String header;
		String payload;
		Base64.Decoder decoder = Base64.getDecoder();
		try {
			header = new String(decoder.decode(chunks[0]));
			payload = new String(decoder.decode(chunks[1]));
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Array Index Out of Bound: " + e.toString());
			return DecoderResponse.builder().message("Invalid Access Token").build();
		} catch (IllegalArgumentException e) {
			System.out.println("Illegal Argument: " + e.toString());
			return DecoderResponse.builder().message("Invalid Access Token").build();
		}

		if (pubKey.trim().isEmpty()){
			DecoderResponse decoderResponse = DecoderResponse.builder().header(header).payload(payload).build();
			return decoderResponse;
		}

		try {
			byte[] encoded = decoder.decode(pubKey);
			RSAPublicKey publickey = getPublicKeyFromString(encoded);
			String isVerified = verifyToken(token, publickey);
			if (isVerified.equalsIgnoreCase(VERIFIED)) {
				DecoderResponse decoderResponse = DecoderResponse.builder().header(header).payload(payload).build();
				return decoderResponse;
			} else {
				return DecoderResponse.builder().message(isVerified).build();
			}
		} catch (GeneralSecurityException e) {
			System.out.println("General Security Exception: " + e.toString());
			return DecoderResponse.builder().message("Invalid Public Key").build();
		} catch (IllegalArgumentException e) {
			System.out.println("Illegal Argument: " + e.toString());
			return DecoderResponse.builder().message("Invalid Public Key").build();
		}
	}

	public RSAPublicKey getPublicKeyFromString(byte[] key) throws GeneralSecurityException  {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(key));
			return pubKey;
	}

	public String verifyToken(String token,RSAPublicKey publicKey) {
		try {
			Algorithm algorithm = Algorithm.RSA256(publicKey, null);
			JWTVerifier verifier = JWT.require(algorithm)
					.build();
			DecodedJWT jwt = verifier.verify(token);
			return VERIFIED;
		} catch (Exception e) {
			System.out.println("Exception in verifying " + e.toString());
			return e.getMessage();
		}
	}
}
