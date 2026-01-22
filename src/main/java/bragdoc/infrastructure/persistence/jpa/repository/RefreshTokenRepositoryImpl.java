package bragdoc.infrastructure.persistence.jpa.repository;

import bragdoc.domain.user.RefreshToken;
import bragdoc.domain.user.RefreshTokenRepository;
import bragdoc.infrastructure.persistence.jpa.mapper.RefreshTokenEntityMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JPA do repositório de RefreshToken.
 */
@Component
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;
    private final RefreshTokenEntityMapper mapper;

    public RefreshTokenRepositoryImpl(RefreshTokenJpaRepository jpaRepository,
            RefreshTokenEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        var entity = mapper.toJpaEntity(refreshToken);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public List<RefreshToken> findByUserLogin(String userLogin) {
        return jpaRepository.findByUserLogin(userLogin)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        jpaRepository.deleteById(refreshToken.getToken());
    }

    @Override
    @Transactional
    public void deleteAllByUserLogin(String userLogin) {
        jpaRepository.deleteByUserLogin(userLogin);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens(Instant.now());
    }
}
