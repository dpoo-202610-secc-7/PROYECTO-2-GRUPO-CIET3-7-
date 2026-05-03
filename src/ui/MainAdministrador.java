package ui;

import java.util.List;
import java.util.Scanner;

import modelo.Administrador;
import modelo.Cafe;
import modelo.Cliente;
import modelo.DiaSemana;
import modelo.JuegoMesa;
import modelo.SistemasDulcesDados;
import modelo.Torneo;
import modelo.Usuario;
import persistencia.PersistenciaSistema;

public class MainAdministrador
{
    public static void main(String[] args)
    {
        SistemasDulcesDados sistema = new SistemasDulcesDados(new Cafe(50), new PersistenciaSistema("src/datos"));
        sistema.inicializarSistema();

        Scanner scanner = new Scanner(System.in);
        try
        {
            if (!iniciarSesionComoAdministrador(scanner, sistema))
            {
                return;
            }

            boolean continuar = true;
            while (continuar)
            {
                System.out.println("\n=== MENÚ ADMINISTRADOR ===");
                System.out.println("1. Crear torneo amistoso");
                System.out.println("2. Crear torneo competitivo");
                System.out.println("3. Registrar empleado (cliente temporal por ahora)");
                System.out.println("4. Listar torneos");
                System.out.println("0. Salir");

                int opcion = ValidadorEntradaConsola.leerEnteroEnRango(scanner, "Seleccione opción: ", 0, 4);
                switch (opcion)
                {
                    case 1:
                        crearTorneo(scanner, sistema, true);
                        break;
                    case 2:
                        crearTorneo(scanner, sistema, false);
                        break;
                    case 3:
                        registrarCliente(scanner, sistema);
                        break;
                    case 4:
                        listarTorneos(sistema);
                        break;
                    case 0:
                        continuar = false;
                        break;
                    default:
                        break;
                }
            }
        }
        finally
        {
            sistema.guardarDatos();
            scanner.close();
        }
    }

    private static boolean iniciarSesionComoAdministrador(Scanner scanner, SistemasDulcesDados sistema)
    {
        String login = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Login admin: ");
        String password = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Password: ");
        Usuario usuario = sistema.autenticarUsuario(login, password);
        if (!(usuario instanceof Administrador))
        {
            System.out.println("Credenciales inválidas o rol no autorizado.");
            return false;
        }
        return true;
    }

    private static void crearTorneo(Scanner scanner, SistemasDulcesDados sistema, boolean amistoso)
    {
        String nombre = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Nombre torneo: ");
        String juegoNombre = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Juego (nombre): ");
        int cupos = ValidadorEntradaConsola.leerEnteroEnRango(scanner, "Cupos: ", 1, 500);
        int diaIndice = ValidadorEntradaConsola.leerEnteroEnRango(scanner, "Día (1=lunes ... 7=domingo): ", 1, 7);
        double valor = ValidadorEntradaConsola.leerDoublePositivo(scanner, amistoso ? "Bono descuento (0.2 = 20%): " : "Tarifa inscripción: ");

        DiaSemana dia = DiaSemana.values()[diaIndice - 1];
        JuegoMesa juego = new JuegoMesa(juegoNombre, 2026, "N/A", 2, 6, 8, false, null);

        Torneo torneo = amistoso
            ? sistema.crearTorneoAmistoso(nombre, dia, juego, cupos, valor)
            : sistema.crearTorneoCompetitivo(nombre, dia, juego, cupos, valor);

        System.out.println(torneo != null ? "Torneo creado correctamente." : "No fue posible crear el torneo.");
    }

    private static void registrarCliente(Scanner scanner, SistemasDulcesDados sistema)
    {
        String doc = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Documento: ");
        String nombre = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Nombre: ");
        String correo = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Correo: ");
        String login = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Login: ");
        String pass = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Password: ");

        boolean agregado = sistema.agregarUsuario(new Cliente(doc, nombre, correo, login, pass));
        System.out.println(agregado ? "Usuario registrado." : "No se pudo registrar (login duplicado). ");
    }

    private static void listarTorneos(SistemasDulcesDados sistema)
    {
        List<Torneo> torneos = sistema.consultarTorneos();
        if (torneos.isEmpty())
        {
            System.out.println("No hay torneos registrados.");
            return;
        }
        for (int i = 0; i < torneos.size(); i++)
        {
            Torneo t = torneos.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " | Día: " + t.getDia() + " | Cupos disp.: " + t.getTotalCuposDisponibles());
        }
    }
}