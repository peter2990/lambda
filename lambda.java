import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ValidadorContraseñas {

    // Expresión Lambda para validar contraseñas (al menos 8 caracteres, 1 mayúscula, 1 número)
    public static boolean validarContraseña(String contraseña) {
        return contraseña.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

    // Método para almacenar el resultado en un archivo
    public static void almacenarResultado(String contraseña, boolean esValida) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultado_validacion.txt", true))) {
            writer.write("Contraseña: " + contraseña + " - Resultado: " + (esValida ? "Válida" : "Inválida"));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Lista de contraseñas a validar
        List<String> contraseñas = Arrays.asList("Password123", "contraseña", "12345", "ValidPass1", "abcDEF123");

        // Usando ExecutorService para validar las contraseñas concurrentemente
        ExecutorService executor = Executors.newFixedThreadPool(3); // 3 hilos

        // Utilizando expresiones Lambda para validar y almacenar el resultado concurrentemente
        for (String contraseña : contraseñas) {
            executor.submit(() -> {
                boolean esValida = validarContraseña(contraseña);
                almacenarResultado(contraseña, esValida);
            });
        }

        // Apagar el ExecutorService después de que todas las tareas se hayan completado
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println("Proceso de validación y almacenamiento completado.");
    }
}
