package ar.edu.utn.frbb.tup.persistencia;

import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.modelos.Cuenta;
import ar.edu.utn.frbb.tup.modelos.TipoPersona;
import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public class ClienteDao extends BaseDao<Cliente>{
    private final String RUTA_ARCHIVO = "src/main/java/ar/edu/utn/frbb/tup/persistencia/data/clientes.txt";
    private final CuentaDao cuentaDao;

    public ClienteDao() {
        this.cuentaDao = new CuentaDao();
    }
    public ClienteDao(CuentaDao cuentaDao) {
        this.cuentaDao = cuentaDao;
    }

    public void inicializarClientes(){
        String encabezado = "DNI, Nombre, Apellido, Domicilio, Fecha Nacimiento, Banco, Tipo Persona, Fecha alta";
        inicializarArchivo(encabezado, RUTA_ARCHIVO);
    }

    public void saveCliente(Cliente cliente) throws ClienteExistenteException {
        String guardarCliente = cliente.getDni() + "," + cliente.getNombre() + "," + cliente.getApellido() + "," + cliente.getDomicilio() + "," + cliente.getFechaNacimiento() + "," + cliente.getBanco() + "," + cliente.getTipoPersona() + "," + cliente.getFechaAlta();
        saveInfo(guardarCliente, RUTA_ARCHIVO);
    }

    public void deleteCliente(Long dni) throws ClienteNoEncontradoException{
        deleteInfo(dni, RUTA_ARCHIVO);
    }

    public Cliente findCliente(Long dni){
        Cliente cliente =  findInfo(dni, RUTA_ARCHIVO);

        // Recuperar las cuentas del cliente
        Set<Cuenta> cuentas = cuentaDao.findAllCuentasDelCliente(dni);
        if (!cuentas.isEmpty()) {
            cliente.setCuentas(cuentas);
        }
        return cliente;
    }

    public List<Cliente> findAllClientes() throws ClientesVaciosException {
        List<Cliente> clientes = findAllInfo(RUTA_ARCHIVO);

        if (clientes.isEmpty()) {//lista esta vacia - no hay clientes registrados
            throw new ClientesVaciosException("No hay clientes registrados");
        }

        for (Cliente cliente : clientes) {
            Set<Cuenta> cuentas = cuentaDao.findAllCuentasDelCliente(cliente.getDni());
            if (!cuentas.isEmpty()) {
                cliente.setCuentas(cuentas);
            }
        }
        return clientes;
    }
        //Funcion para parsear los datos leidos del archivo a un objeto tipo 'Cliente'

        @Override
        public Cliente parseDatosToObjet (String[]datos){
            Cliente cliente = new Cliente();

            cliente.setDni(Long.parseLong(datos[0]));
            cliente.setNombre(datos[1]);
            cliente.setApellido(datos[2]);
            cliente.setDomicilio(datos[3]);
            cliente.setFechaNacimiento(LocalDate.parse(datos[4]));
            cliente.setBanco(datos[5]);
            cliente.setTipoPersona(TipoPersona.fromString(datos[6]));
            cliente.setFechaAlta(LocalDate.parse(datos[7]));

            return cliente;
        }


}