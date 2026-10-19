package br.com.autorepairshop.infrastructure.security;

import java.time.Instant;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import br.com.autorepairshop.domain.funcionario.Funcionario;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {

        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public String gerarToken(Funcionario funcionario) {
        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(funcionario.getId().toString())
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(expirationSeconds))
                .claim("tipo", funcionario.getTipo().name())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public static SecretKey criarChave(String segredoBase64) {
        byte[] bytes = Base64.getDecoder().decode(segredoBase64);

        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                    "A chave JWT HS256 deve ter pelo menos 32 bytes."
            );
        }

        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}