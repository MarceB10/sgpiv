package sgpiv;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Empresa;
import sgpiv.repository.EmpresaRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final EmpresaRepository empresaRepository;

    public DataLoader(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    public void run(String... args) {

        if (empresaRepository.count() == 0) {

            Empresa empresa1 = new Empresa();
            empresa1.setRazonSocial("Google");
            empresa1.setCuit("20-12345678-9");
            empresa1.setRubro("Tecnologia");
            empresa1.setEmail("google@gmail.com");
            empresa1.setEstadoEmpresa(EstadoEmpresa.INTERESADA);

            Empresa empresa2 = new Empresa();
            empresa2.setRazonSocial("Microsoft");
            empresa2.setCuit("27-98765432-1");
            empresa2.setRubro("Software");
            empresa2.setEmail("microsoft@gmail.com");
            empresa2.setEstadoEmpresa(EstadoEmpresa.RADICADA);

            empresaRepository.save(empresa1);
            empresaRepository.save(empresa2);

            System.out.println("Empresas de prueba cargadas");
        }
    }
}