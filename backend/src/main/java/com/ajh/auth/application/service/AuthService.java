package com.ajh.auth.application.service;

import com.ajh.auth.application.port.in.AuthUseCase;
import com.ajh.auth.application.port.in.LoginCommand;
import com.ajh.auth.application.port.in.RegisterCommand;
import com.ajh.auth.application.port.out.TokenServicePort;
import com.ajh.auth.domain.AuthResponse;
import com.ajh.candidate.application.port.out.CandidateRepositoryPort;
import com.ajh.candidate.domain.Candidate;
import com.ajh.candidate.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase { // Essencial para o Spring injetar no Controller

    private final CandidateRepositoryPort candidateRepository;
    private final TokenServicePort tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse login(LoginCommand command) {
        log.info(">>> [AUTH] Tentativa de login para: {}", command.getEmail());

        Candidate candidate = candidateRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Compara a senha enviada com o hash salvo no banco
        if (!passwordEncoder.matches(command.getPassword(), candidate.getPassword())) {
            log.warn(">>> [AUTH] Senha inválida para: {}", command.getEmail());
            throw new RuntimeException("Credenciais inválidas");
        }

        String token = tokenService.generateToken(candidate);
        
        log.info(">>> [AUTH] Login realizado com sucesso para: {}", candidate.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(candidate.getEmail())
                .name(candidate.getName())
                .role(candidate.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterCommand command) {
        log.info(">>> [AUTH] Registrando novo usuário: {}", command.getEmail());

        // Verifica se o email já existe para evitar duplicidade
        if (candidateRepository.findByEmail(command.getEmail()).isPresent()) {
            log.warn(">>> [AUTH] Tentativa de registro com email já existente: {}", command.getEmail());
            throw new RuntimeException("E-mail já cadastrado");
        }

        // Cria o domínio do candidato para persistência
        Candidate newCandidate = Candidate.builder()
                .name(command.getName())
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .role(Role.CANDIDATE)
                .assessmentStatus("PENDING")
                .build();

        Candidate savedCandidate = candidateRepository.save(newCandidate);
        String token = tokenService.generateToken(savedCandidate);

        log.info(">>> [AUTH] Usuário registrado e autenticado: ID {}", savedCandidate.getId());

        return AuthResponse.builder()
                .token(token)
                .email(savedCandidate.getEmail())
                .name(savedCandidate.getName())
                .role(savedCandidate.getRole().name())
                .build();
    }
}