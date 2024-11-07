package br.com.aygean.cleanarch.main;

import br.com.aygean.cleanarch.application.PacienteUseCase;
import br.com.aygean.cleanarch.gateway.PacienteGateway;
import br.com.aygean.cleanarch.infra.repository.PacienteRepository;
import br.com.aygean.cleanarch.infra.service.PacienteGatewayImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public PacienteGateway pacienteGateway(PacienteRepository pacienteRepository) {
        return new PacienteGatewayImpl(pacienteRepository);
    }

    @Bean
    public PacienteUseCase pacienteUseCase(PacienteGateway pacienteGateway) {
        return new PacienteUseCase(pacienteGateway);
    }
}
