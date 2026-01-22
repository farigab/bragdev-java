package bragdoc.domain.shared.exceptions;

/**
 * Exceção lançada quando um refresh token expirou.
 */
public class TokenExpiredException extends UnauthorizedException {

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException() {
        super("Refresh token expirado");
    }
}
